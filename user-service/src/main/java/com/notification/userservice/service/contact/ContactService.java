package com.notification.userservice.service.contact;

import com.notification.userservice.dto.contact.*;
import com.notification.userservice.entity.Contact;
import com.notification.userservice.entity.User;
import com.notification.userservice.exception.*;
import com.notification.userservice.exception.csv.CsvProcessingException;
import com.notification.userservice.exception.csv.CsvValidationException;
import com.notification.userservice.fileparse.ContactCsvHeader;
import com.notification.userservice.fileparse.CsvParserService;
import com.notification.userservice.fileparse.ContactParseResult;
import com.notification.userservice.repository.contact.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final CsvParserService csvParserService;
    
    /**
     * Parses a CSV file and creates contacts for the given user.
     * <p>
     * The CSV file must contain at least {@code name} and {@code email} headers.
     * Optional headers are {@code phone} and {@code telegram_id}.
     * <p>
     * Each row is validated:
     * <ul>
     *   <li>{@code name} must not be blank</li>
     *   <li>{@code email} must be a valid email address</li>
     * </ul>
     * Rows with validation errors are skipped and reported in the result.
     * If no valid rows are found, a {@link CsvValidationException} is thrown.
     * If the file cannot be read, a {@link CsvProcessingException} is thrown.
     *
     * @param file the uploaded CSV file
     * @param user the owner of the contacts
     * @return result containing the number of created contacts and validation errors
     * @throws CsvValidationException if no valid contacts were found
     * @throws CsvProcessingException if the file cannot be read or
     */

    public UploadCsvResult uploadCsv(MultipartFile file, User user) {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get();

        ContactParseResult result = parseContactCsv(file, csvFormat, user);
        List<Contact> contacts = result.contacts();
        Map<Long, List<String>> errors = result.errors();

        saveContacts(contacts);
        // TODO: generate UUID in new DB table "contacts_csv_uploads"
        return new UploadCsvResult(UUID.randomUUID(), ProcessingStatus.COMPLETED,
                contacts.size(), errors.size(), errors);
    }

    private ContactParseResult parseContactCsv(MultipartFile file, CSVFormat csvFormat, User user) {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = csvFormat.parse(reader)) {

            if (parser.getRecordNumber() < 1) {
                throw new CsvValidationException("There are no records in csv file");
            }

            Set<String> requiredHeaders = ContactCsvHeader.getRequiredHeaders();
            Set<String> actualHeaders = parser.getHeaderMap().keySet();
            csvParserService.validateHeaders(requiredHeaders, actualHeaders);

            List<Contact> contacts = new ArrayList<>();
            Map<Long, List<String>> errors = new HashMap<>();
            long rowNumber = 0;

            for (CSVRecord record : parser)
            {
                rowNumber++;
                String name = record.get("name");
                String email = record.get("email");
                String phone = actualHeaders.contains("phone") ? record.get("phone") : null;
                String telegramId = actualHeaders.contains("telegram_id") ? record.get("telegram_id") : null;
                Contact contact = new Contact(name, email, phone, telegramId, user);

                List<String> rowErrors = validateContact(contact, user);
                if (rowErrors.isEmpty()) {
                    contacts.add(contact);
                }
                else {
                    errors.put(rowNumber, rowErrors);
                }
            }
            return new ContactParseResult(contacts, errors);
        }
        catch (IOException e) {
            throw new CsvProcessingException("Failed to parse CSV file: ", e);
        }
    }

    /**
     * Check the {@code contact} for the presence of a name, uniqueness of the {@code contact}
     * among another {@code user}'s ones
     * and validate email address of this one.
     * @param contact
     * @param user
     * @return List of errors.
     */
    private List<String> validateContact(Contact contact, User user) {
        List<String> errors = new ArrayList<>();
        String name = contact.getName();
        String email = contact.getEmail();

        if (name == null || name.isBlank()) {
            errors.add("name is required");
        }

        if (email == null || !CsvParserService.getEmailValidator().isValid(email)) {
            errors.add("invalid email address");
        }

        if (contactRepository.existsByEmailAndUserId(email, user.getId())) {
            errors.add("current user's contact with this email address already exists");
        }

        return errors;
    }

    //TODO: must explain where the conflicts are
    /**
     * Save {@code contacts} to database. Throws {@link CsvProcessingException} if there are some data conflicts.
     */
    private void saveContacts(List<Contact> contacts) {
            contactRepository.saveAll(contacts);
    }

    public ContactResponse CreateContact(User user, CreateContactRequest request) {
        Contact contact = new Contact(request.name(),  request.email(), request.phone(), request.telegramId(), user);
        Contact savedContact;
        try {
            savedContact = contactRepository.save(contact);
        }
        catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException("Contact already exists");
        }
        return toContactResponse(savedContact);
    }

    private ContactResponse toContactResponse(Contact contact) {
        return new ContactResponse(contact.getId(),contact.getUser().getId(),
                contact.getName(), contact.getEmail(), contact.getPhone(), contact.getTelegramId());
    }

    public ContactResponse getContactByEmail(String email, User user) {
        if (email == null || email.isBlank()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "Email is required");
            throw new ValidationException(errors);
        }
        Contact contact = contactRepository.findByEmailAndUserId(email, user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Contact with email " + email + " not found")
        );
        return toContactResponse(contact);
    }

    public ContactResponse getContactById(UUID contactId, User user) {
        Contact contact =  contactRepository.findByIdAndUserId(contactId, user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Contact with id " + contactId + " not found")
        );
        return toContactResponse(contact);
    }

    // TODO: add separated offset/limit and cursor-based pagination
    public List<ContactResponse> getAllContacts(User user, Pageable pageable) {
        return contactRepository.findByUser(user, pageable).stream()
                .map(this::toContactResponse).toList();
    }

    public ContactResponse updateContact(User user, UUID contactId, UpdateContactRequest request) {
        Map<String, String> errors = new HashMap<>();

        Contact contact = contactRepository.findByIdAndUserId(contactId, user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Contact with id " + contactId + " not found")
        );
        if (request.name() != null) {
            if (request.name().isBlank() || request.name().length() > 32) {
                errors.put("name", "name must be between 1 and 32 characters");
            }
            else {
                contact.setName(request.name());
            }
        }

        if (request.email() != null) {
            if (request.email().isBlank() || request.email().length() > 254 || request.email().length() < 6) {
                errors.put("email", "email must be between 6 and 254 characters");
            }
            else if (contactRepository.existsByEmailAndUserId(request.email(), user.getId())) {
                errors.put("email", "email already exists");
            }
            else {
                contact.setEmail(request.email());
            }
        }

        // TODO: refactor
        if (request.telegramId() != null) {
            if (request.telegramId().isBlank() || request.telegramId().length() > 10 || request.telegramId().length() < 6) {
                errors.put("telegramId", "telegramId must be between 6 and 10 characters");
            }
            else {
                contact.setTelegramId(request.telegramId());
            }
        }

        // TODO: refactor
        if  (request.phone() != null) {
            if (request.phone().isBlank()) {
                errors.put("phone", "phone must be not blank");
            }
            else {
                contact.setPhone(request.phone());
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Contact savedContact;
        try {
            savedContact = contactRepository.save(contact);
        }
        catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException("Contact with id " + contactId + " already exists");
        }
        return toContactResponse(savedContact);
    }

    public void deleteContact(User user, UUID contactId) {
        Contact contact = contactRepository.findByIdAndUserId(contactId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        contactRepository.delete(contact);
    }
}