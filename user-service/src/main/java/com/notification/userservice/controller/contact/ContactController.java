package com.notification.userservice.controller.contact;

import com.notification.userservice.dto.contact.ContactResponse;
import com.notification.userservice.dto.contact.CreateContactRequest;
import com.notification.userservice.dto.contact.UpdateContactRequest;
import com.notification.userservice.dto.contact.UploadCsvResult;
import com.notification.userservice.entity.User;
import com.notification.userservice.service.contact.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public UploadCsvResult uploadCsv(@RequestParam("file") MultipartFile file) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return contactService.uploadCsv(file, user);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse createContact(@RequestBody CreateContactRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return contactService.CreateContact(user, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ContactResponse> getContacts(@PageableDefault(sort = "id", direction = Sort.Direction.ASC)
                                                 Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return contactService.getAllContacts(user, pageable);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ContactResponse getContactByEmail(@RequestParam String email) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return contactService.getContactByEmail(email, user);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContactResponse getContactById(@PathVariable UUID id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return contactService.getContactById(id, user);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContactResponse updateContact(@PathVariable UUID id, @RequestBody UpdateContactRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return contactService.updateContact(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContact(@PathVariable UUID id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        contactService.deleteContact(user, id);
    }
}
