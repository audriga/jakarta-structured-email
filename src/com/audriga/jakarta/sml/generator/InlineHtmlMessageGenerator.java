package com.audriga.jakarta.sml.generator;

import com.audriga.jakarta.sml.mime.*;
import com.audriga.jakarta.sml.model.MimeTextContent;
import com.audriga.jakarta.sml.model.StructuredData;
import com.audriga.jakarta.sml.model.StructuredSyntax;
import jakarta.mail.MessagingException;

import java.util.List;

public class InlineHtmlMessageGenerator implements StructuredMimeMessageGenerator {
    @Override
    public StructuredMimeMessageWrapper generate(String subject, String textBody, String htmlBody, List<StructuredData> structuredData) throws MessagingException {
        return generate(subject, textBody, htmlBody, structuredData, null, true);
    }

    public StructuredMimeMessageWrapper generate(String subject, String textBody, String htmlBody, List<StructuredData> structuredData, String htmlTag, boolean htmlLast) throws MessagingException {
        StructuredData jsonStructuredData = checkStructuredDataToInsert(structuredData);
        String html = insertJsonLdInHtml(htmlBody, jsonStructuredData, htmlTag);

        MimeMultipartBuilder multipartBuilder = new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.ALTERNATIVE);
        if (htmlLast) {
            multipartBuilder.addBodyPartText(textBody, "utf-8");
            multipartBuilder.addBodyPartHtml(html, "utf-8");
        } else {
            multipartBuilder.addBodyPartHtml(html, "utf-8");
            multipartBuilder.addBodyPartText(textBody, "utf-8");
        }

        return new StructuredMimeMessageBuilder()
                .subject(subject)
                .content(multipartBuilder.build())
                .structuredData(jsonStructuredData)
                .plainText(new MimeTextContent(textBody, "utf-8"))
                .htmlText(new MimeTextContent(html, "utf-8"))
                .build();
    }

    protected static StructuredData checkStructuredDataToInsert(List<StructuredData> structuredData) {
        if (structuredData != null && structuredData.size() > 1) {
            throw new IllegalArgumentException("Only one structured data object is supported for now.");
        }

        if (structuredData != null && !structuredData.isEmpty()) {
            if (structuredData.get(0).getSyntax() != StructuredSyntax.JSON_LD) {
                throw new IllegalArgumentException("Only JSON-LD is supported for now.");
            }
            return structuredData.get(0);
        } else {
            return null;
        }
    }

    protected static String insertJsonLdInHtml(String html, StructuredData structuredData, String htmlTag) {
        if (structuredData == null || html == null) {
            return html;
        }

        if (htmlTag == null) {
            htmlTag = "<body>";
        } else {
            htmlTag = "<" + htmlTag + ">";
        }
        int index = html.indexOf(htmlTag);
        if (index != -1) {
            int insertPosition = index + htmlTag.length();
            return html.substring(0, insertPosition) + "\n<script type=\"" + StructuredData.MIME_TYPE + "\">\n" + structuredData.getBody() + "\n</script>" + html.substring(insertPosition);
        } else {
            throw new IllegalArgumentException("HTML does not contain <head> tag.");
        }
    }

    private static String insertJsonLdInHtml(String html, StructuredData structuredData) {
        return insertJsonLdInHtml(html, structuredData, null);
    }
}