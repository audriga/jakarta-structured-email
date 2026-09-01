package com.audriga.jakarta.sml.extension.mime;

import com.audriga.jakarta.sml.extension.model.MimeTextContent;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;

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
        String html = insertJsonLdInHtml(htmlBody, structuredData, htmlTag);

        StructuredMimeMessageWrapper sm = initMessage();

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
