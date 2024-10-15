package com.audriga.jakarta.sml.mime;

import com.audriga.jakarta.sml.model.MimeTextContent;
import com.audriga.jakarta.sml.model.StructuredData;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.util.Properties;

public class HtmlOnlyMessageBuilder extends AbstractMessageBuilder<HtmlOnlyMessageBuilder> {
    private String htmlTag;

    public HtmlOnlyMessageBuilder htmlTag(String htmlTag) {
        this.htmlTag = htmlTag;
        return self();
    }

    @Override
    protected HtmlOnlyMessageBuilder self() {
        return this;
    }

    @Override
    public StructuredMimeMessageWrapper build() throws MessagingException {
        StructuredData jsonStructuredData = checkStructuredDataToInsert(structuredData);
        String html = insertJsonLdInHtml(htmlBody, jsonStructuredData, htmlTag);

        Properties properties = System.getProperties();
        Session session = Session.getDefaultInstance(properties, null);
        StructuredMimeMessageWrapper sm = new StructuredMimeMessageWrapper(session);

        if (subject != null) {
            sm.setSubject(subject);
        }
        if (from != null) {
            sm.setFrom(from);
        }
        if (to != null) {
            sm.addRecipient(Message.RecipientType.TO, to);
        }
        if (structuredData != null) {
            sm.setStructuredData(structuredData);
        }
        if (htmlBody != null) {
            sm.setHtmlBody(new MimeTextContent(html, "utf-8"));
            sm.setText(html, htmlBody.getEncoding(), "html");
        }

        return sm;
    }
}
