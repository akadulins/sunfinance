package com.sunfinance.common.dto;

import java.util.Map;

public record RenderTemplateRequest(String slug, Map<String, Object> variables) {}
