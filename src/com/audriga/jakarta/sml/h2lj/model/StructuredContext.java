package com.audriga.jakarta.sml.h2lj.model;

public enum StructuredContext {
    SCHEMA_ORG("https://schema.org");

    private final String url;

    StructuredContext(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}