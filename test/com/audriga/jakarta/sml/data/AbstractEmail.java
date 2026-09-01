package com.audriga.jakarta.sml.data;

import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;
import jakarta.activation.FileDataSource;

import java.net.URISyntaxException;
import java.util.List;

public abstract class AbstractEmail {

    public AbstractEmail(String textBody, String subject, String htmlBody, String builderType, boolean htmlLast) {
        this.textBody = textBody;
        this.subject = subject;
        this.htmlBody = htmlBody;
        this.builderType = builderType;
        this.htmlLast = htmlLast;
    }

    private final String textBody;
    private final String subject;
    private final String htmlBody;
    private final String builderType;
    private final boolean htmlLast;

    public String getSubject() {
        return subject;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public String getTextBody() {
        return textBody;
    }

    public String getBuilderType() {
        return builderType;
    }

    public boolean isHtmlLast() {
        return htmlLast;
    }

    public abstract JsonLdWrapper getJson();

    public abstract JsonLdWrapper getJsonArray();

    public abstract FileDataSource getAttachment() throws URISyntaxException;

    public abstract String getAttachmentName();
}