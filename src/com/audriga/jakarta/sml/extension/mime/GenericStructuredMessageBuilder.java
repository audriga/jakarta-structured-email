package com.audriga.jakarta.sml.extension.mime;

import com.audriga.jakarta.sml.extension.model.MimeTextContent;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;

public class GenericStructuredMessageBuilder extends AbstractMessageBuilder<GenericStructuredMessageBuilder> {
    private Multipart content;
    private MimeTextContent textBody;

    public GenericStructuredMessageBuilder content(Multipart content) {
        this.content = content;
        return self();
    }

    public GenericStructuredMessageBuilder textBody(String textBody) {
        if (textBody != null) {
            this.textBody = new MimeTextContent(textBody, "utf-8");
        }
        return self();
    }

    @Override
    protected GenericStructuredMessageBuilder self() {
        return this;
    }

    @Override
    public StructuredMimeMessageWrapper build() throws MessagingException {
        StructuredMimeMessageWrapper sm = initMessage();

        MimeMultipartBuilder multipartBuilder = new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.ALTERNATIVE);
        if (textBody != null) {
            multipartBuilder.addBodyPartText(textBody);
        }
        if (htmlBody != null) {
            multipartBuilder.addBodyPartHtml(htmlBody);
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
        if (content != null) {
            sm.resetContent(content);
        }
        if (structuredData != null) {
            sm.setStructuredData(structuredData);
        }
        if (textBody != null) {
            sm.setTextBody(textBody);
        }
        if (htmlBody != null) {
            sm.setHtmlBody(htmlBody);
        }

        return sm;
    }
}
