package com.audriga.jakarta.sml.generator;

import com.audriga.jakarta.sml.mime.StructuredMimeMessageBuilder;
import com.audriga.jakarta.sml.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.model.MimeTextContent;
import com.audriga.jakarta.sml.model.StructuredData;
import jakarta.mail.MessagingException;

import java.util.List;

public class HtmlOnlyMessageGenerator extends InlineHtmlMessageGenerator implements StructuredMimeMessageGenerator {

    @Override
    public StructuredMimeMessageWrapper generate(String subject, String textBody, String htmlBody, List<StructuredData> structuredData) throws MessagingException {
        if (textBody != null) {
            throw new MessagingException("textBody must be null for HtmlOnlyMessageGenerator");
        }

        return generate(subject, htmlBody, structuredData, null);
    }

    public StructuredMimeMessageWrapper generate(String subject, String htmlBody, List<StructuredData> structuredData, String htmlTag) throws MessagingException {
        StructuredData jsonStructuredData = checkStructuredDataToInsert(structuredData);
        String html = insertJsonLdInHtml(htmlBody, jsonStructuredData, htmlTag);

        // Create a new MimeMessage
        return new StructuredMimeMessageBuilder()
                .subject(subject)
                .text(html, "utf-8", "html")
                .structuredData(jsonStructuredData)
                .htmlText(new MimeTextContent(html, "utf-8")) // TODO refactor as distionction between ".text" and ".htmlText" is not clear
                .build();
    }
}