package com.sunfinance.template.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sunfinance.template.entity.Template;
import com.sunfinance.template.repository.TemplateRepository;

@Service
public class TemplateService {
	
	
    private final TemplateRepository repo;
    
    public TemplateService(TemplateRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Optional<Template> getBySlug(String slug) {
        return repo.findBySlug(slug);
    }
}