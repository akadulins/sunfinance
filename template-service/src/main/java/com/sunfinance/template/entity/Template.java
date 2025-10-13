package com.sunfinance.template.entity;

import java.util.Map;
import java.util.UUID;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Lob;

@Entity
public class Template {
	@Id
	@GeneratedValue
    private Long id;

	private String slug;    // e.g. "mobile-verification"
    @Lob
    @Column(columnDefinition = "text")
    private String content; // text or HTML
    private String type;    // "plain" or "html"

    protected Template() {}

    public Template(String slug, String content, String type) {
        this.slug = slug;
        this.content = content;
        this.type = type;
    }

    public String render(Map<String, Object> variables) {
        String rendered = content;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            rendered = rendered.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
        }
        return rendered;
    }

    public boolean isHtml() {
        return "html".equalsIgnoreCase(type);
    }
}