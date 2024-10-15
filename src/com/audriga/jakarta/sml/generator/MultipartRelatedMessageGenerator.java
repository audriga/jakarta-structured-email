package com.audriga.jakarta.sml.generator;

import com.audriga.jakarta.sml.mime.GenericStructuredMessageBuilder;
import com.audriga.jakarta.sml.mime.MimeMultipartBuilder;
import com.audriga.jakarta.sml.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.model.MimeTextContent;
import com.audriga.jakarta.sml.model.StructuredData;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;

import java.util.List;

public class MultipartRelatedMessageGenerator implements StructuredMimeMessageGenerator {
    @Override
    public StructuredMimeMessageWrapper generate(String subject, String textBody, String htmlBody, List<StructuredData> structuredData) throws MessagingException {
        if (structuredData != null && structuredData.size() > 1) {
            throw new IllegalArgumentException("Only one structured data object is supported for now.");
        }

        StructuredData structuredDataPart = null;
        if (structuredData != null && !structuredData.isEmpty()) {
            structuredDataPart = structuredData.get(0);
        }

        MimeMultipart alternative =  new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.ALTERNATIVE)
                .addBodyPartText(textBody)
                .addBodyPartHtml(htmlBody)
                .build();

        MimeMultipart mm =  new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.RELATED)
                .addBodyPart(alternative)
                .addBodyPartJsonLd(structuredDataPart, "utf-8")
                .build();

        return new GenericStructuredMessageBuilder()
                .subject(subject)
                .content(mm)
                .structuredData(structuredData)
                .textBody(textBody)
                .htmlBody(htmlBody)
                .build();
    }
}
