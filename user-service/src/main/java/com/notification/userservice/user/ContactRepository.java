package com.notification.userservice.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    public List<Contact> findByOwnerId(UUID ownerId);

    public List<Contact> findByOwner(User owner);
}
