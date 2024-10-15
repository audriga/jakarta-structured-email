package com.audriga.jakarta.sml.generator;

import com.audriga.jakarta.sml.mime.GenericStructuredMessageBuilder;
import com.audriga.jakarta.sml.mime.MimeMultipartBuilder;
import com.audriga.jakarta.sml.model.MimeTextContent;
import com.audriga.jakarta.sml.model.StructuredData;
import com.audriga.jakarta.sml.mime.StructuredMimeMessageWrapper;
import jakarta.mail.MessagingException;

import java.util.List;

public class MultipartAlternativeMessageGenerator implements StructuredMimeMessageGenerator {
    @Override
    public StructuredMimeMessageWrapper generate(String subject, String textBody, String htmlBody, List<StructuredData> structuredData) throws MessagingException {
        return generate(subject, textBody, htmlBody, structuredData, false);
    }

    public StructuredMimeMessageWrapper generate(String subject, String textBody, String htmlBody, List<StructuredData> structuredData, boolean htmlLast) throws MessagingException {
        if (structuredData != null && structuredData.size() > 1) {
            throw new IllegalArgumentException("Only one structured data object is supported for now.");
        }

        StructuredData structuredDataPart = null;
        if (structuredData != null && !structuredData.isEmpty()) {
            structuredDataPart = structuredData.get(0);
        }

        MimeMultipartBuilder multipartBuilder = new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.ALTERNATIVE);
        if (htmlLast) {
            multipartBuilder.addBodyPartText(textBody);
            multipartBuilder.addBodyPartJsonLd(structuredDataPart);
            multipartBuilder.addBodyPartHtml(htmlBody);
        } else {
            multipartBuilder.addBodyPartText(textBody);
            multipartBuilder.addBodyPartHtml(htmlBody);
            multipartBuilder.addBodyPartJsonLd(structuredDataPart);
        }

        return new GenericStructuredMessageBuilder()
                .subject(subject)
                .content(multipartBuilder.build())
                .structuredData(structuredData)
                .textBody(textBody)
                .htmlBody(htmlBody)
                .build();
    }
}