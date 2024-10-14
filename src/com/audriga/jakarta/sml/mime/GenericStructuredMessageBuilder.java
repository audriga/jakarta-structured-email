package com.audriga.jakarta.sml.mime;

import com.audriga.jakarta.sml.model.MimeTextContent;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;

import java.util.Properties;

public class GenericStructuredMessageBuilder extends AbstractMessageBuilder<GenericStructuredMessageBuilder> {
    private Multipart content;
    private MimeTextContent textBody;

    public GenericStructuredMessageBuilder content(Multipart content) {
        this.content = content;
        return self();
    }

    public GenericStructuredMessageBuilder textBody(String textBody) {
        this.textBody = new MimeTextContent(textBody, "utf-8");
        return self();
    }

    @Override
    protected GenericStructuredMessageBuilder self() {
        return this;
    }

    @Override
    public StructuredMimeMessageWrapper build() throws MessagingException {
        Properties properties = System.getProperties();
        Session session = Session.getDefaultInstance(properties, null);
        StructuredMimeMessageWrapper sm = new StructuredMimeMessageWrapper(session);

        MimeMultipartBuilder multipartBuilder = new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.ALTERNATIVE);
        multipartBuilder.addBodyPartText(textBody.getText(), textBody.getEncoding());
        multipartBuilder.addBodyPartHtml(htmlBody.getText(), htmlBody.getEncoding());
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
