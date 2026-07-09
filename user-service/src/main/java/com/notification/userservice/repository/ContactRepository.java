package com.notification.userservice.repository;

import com.notification.userservice.entity.Contact;
import com.notification.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    public List<Contact> findByUser(User user);

    public boolean existsByEmailAndOwnerId(String email, UUID ownerId);

    public Optional<Contact> findByEmailAndOwnerId(String email, UUID ownerId);

    public Optional<Contact> findByIdAndOwnerId(UUID id, UUID ownerId);
}
