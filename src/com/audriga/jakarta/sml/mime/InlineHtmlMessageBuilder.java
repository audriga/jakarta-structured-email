package com.audriga.jakarta.sml.mime;

import com.audriga.jakarta.sml.model.MimeTextContent;
import com.audriga.jakarta.sml.model.StructuredData;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.util.Properties;

public class InlineHtmlMessageBuilder extends AbstractMessageBuilder<InlineHtmlMessageBuilder> {

    private MimeTextContent textBody;
    private String htmlTag;
    private boolean htmlLast = true;

    public InlineHtmlMessageBuilder textBody(String textBody) {
        if (textBody != null) {
            this.textBody = new MimeTextContent(textBody, "utf-8");
        }
        return self();
    }

    @Override
    protected InlineHtmlMessageBuilder self() {
        return this;
    }

    public InlineHtmlMessageBuilder htmlTag(String htmlTag) {
        this.htmlTag = htmlTag;
        return this;
    }

    public InlineHtmlMessageBuilder htmlLast(boolean htmlLast) {
        this.htmlLast = htmlLast;
        return this;
    }

    @Override
    public StructuredMimeMessageWrapper build() throws MessagingException {
        StructuredData jsonStructuredData = checkStructuredDataToInsert(structuredData);
        String html = insertJsonLdInHtml(htmlBody, jsonStructuredData, htmlTag);

        StructuredMimeMessageWrapper sm = initMessage();

        MimeMultipartBuilder multipartBuilder = new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.ALTERNATIVE);
        if (htmlLast) {
            multipartBuilder.addBodyPartText(textBody);
            if (html != null) {
                multipartBuilder.addBodyPartHtml(new MimeTextContent(html, "utf-8"));
            }
        } else {
            if (html != null) {
                multipartBuilder.addBodyPartHtml(new MimeTextContent(html, "utf-8"));
            }
            multipartBuilder.addBodyPartText(textBody);
        }
        sm.resetContent(multipartBuilder.build());

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
        if (textBody != null) {
            sm.setTextBody(textBody);
        }
        if (htmlBody != null) {
            sm.setHtmlBody(new MimeTextContent(html, "utf-8"));
        }

        return sm;
    }

}
