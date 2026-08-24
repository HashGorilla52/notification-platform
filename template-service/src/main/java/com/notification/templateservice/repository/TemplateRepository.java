package com.notification.templateservice.repository;

import com.notification.templateservice.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TemplateRepository extends JpaRepository<Template, UUID> {
    public List<Template> findByOwnerId(UUID ownerId);

    public boolean existsByNameAndOwnerId(String name, UUID ownerId);

    public Template findByNameAndOwnerId(String name, UUID ownerId);
}
