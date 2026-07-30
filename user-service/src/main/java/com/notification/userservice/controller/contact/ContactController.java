package com.notification.userservice.controller.contact;

import com.notification.userservice.annotation.ById;
import com.notification.userservice.annotation.CurrentUser;
import com.notification.userservice.dto.contact.ContactResponse;
import com.notification.userservice.dto.contact.CreateContactRequest;
import com.notification.userservice.dto.contact.UpdateContactRequest;
import com.notification.userservice.dto.contact.UploadCsvResult;
import com.notification.userservice.entity.User;
import com.notification.userservice.service.contact.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @PostMapping("/upload-csv")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UploadCsvResult uploadCsv(@RequestParam("file") MultipartFile file, @CurrentUser User user) {
        return contactService.uploadCsv(file, user);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse createContact(@Valid @RequestBody CreateContactRequest request, @CurrentUser User user) {
        return contactService.CreateContact(user, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ContactResponse> getContacts(@PageableDefault(sort = "id", direction = Sort.Direction.ASC)
                                                 Pageable pageable, @CurrentUser User user) {
        return contactService.getAllContacts(user, pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ContactResponse getContactByEmail(@RequestParam String email, @CurrentUser User user) {
        return contactService.getContactByEmail(email, user);
    }

    @ById()
    public ContactResponse getContactById(@PathVariable UUID id, @CurrentUser User user) {
        return contactService.getContactById(id, user);
    }

    @ById(method = RequestMethod.PATCH)
    public ContactResponse updateContact(@PathVariable UUID id, @RequestBody UpdateContactRequest request,
                                         @CurrentUser User user) {
        return contactService.updateContact(user, id, request);
    }

    @ById(method = RequestMethod.DELETE, status=HttpStatus.NO_CONTENT)
    public void deleteContact(@PathVariable UUID id, @CurrentUser User user) {
        contactService.deleteContact(user, id);
    }
}
