package com.sunfinance.template.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunfinance.template.entity.Template;

public interface TemplateRepository extends JpaRepository<Template, UUID> {
    Optional<Template> findBySlug(String slug);
}
