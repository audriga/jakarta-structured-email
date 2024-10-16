package com.audriga.jakarta.sml.mime;

import com.audriga.jakarta.sml.model.MimeTextContent;
import com.audriga.jakarta.sml.model.StructuredData;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.util.Properties;

public class MultipartAlternativeMessageBuilder extends AbstractMessageBuilder<MultipartAlternativeMessageBuilder> {
    private MimeTextContent textBody;
    private boolean htmlLast = true;

    public MultipartAlternativeMessageBuilder textBody(String textBody) {
        if (textBody != null) {
            this.textBody = new MimeTextContent(textBody, "utf-8");
        }
        return self();
    }

    public MultipartAlternativeMessageBuilder htmlLast(boolean htmlLast) {
        this.htmlLast = htmlLast;
        return this;
    }

    @Override
    protected MultipartAlternativeMessageBuilder self() {
        return this;
    }

    @Override
    public StructuredMimeMessageWrapper build() throws MessagingException {
        if (structuredData != null && structuredData.size() > 1) {
            throw new IllegalArgumentException("Only one structured data object is supported for now.");
        }

        StructuredData structuredDataPart = null;
        if (structuredData != null && !structuredData.isEmpty()) {
            structuredDataPart = structuredData.get(0);
        }

        Properties properties = System.getProperties();
        Session session = Session.getDefaultInstance(properties, null);
        StructuredMimeMessageWrapper sm = new StructuredMimeMessageWrapper(session);

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
            sm.setHtmlBody(htmlBody);
        }

        return sm;
    }
}
