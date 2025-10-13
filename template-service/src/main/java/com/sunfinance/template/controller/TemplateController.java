package com.sunfinance.template.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sunfinance.common.dto.RenderTemplateRequest;
import com.sunfinance.template.entity.Template;
import com.sunfinance.template.repository.TemplateRepository;
import com.sunfinance.template.service.TemplateService;

@RestController
@RequestMapping("/templates")
public class TemplateController {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TemplateController.class);

    private final TemplateService repository;

    public TemplateController(TemplateService repository) {
        this.repository = repository;
    }

    @PostMapping("/render")
    public ResponseEntity<String> render(@RequestBody RenderTemplateRequest request) {
        Template template = repository.getBySlug(request.slug())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));

        String rendered = template.render(request.variables());
        
        log.info("Received verification request to render: " + request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(template.isHtml() ? MediaType.TEXT_HTML : MediaType.TEXT_PLAIN);

        return new ResponseEntity<>(rendered, headers, HttpStatus.OK);
    }
}