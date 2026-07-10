package com.notification.userservice.repository;

import com.notification.userservice.entity.Contact;
import com.notification.userservice.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    public List<Contact> findByUser(User user, Pageable pageable);

    public boolean existsByEmailAndUserId(String email, UUID ownerId);

    public Optional<Contact> findByEmailAndUserId(String email, UUID ownerId);

    public Optional<Contact> findByIdAndUserId(UUID id, UUID ownerId);
}
