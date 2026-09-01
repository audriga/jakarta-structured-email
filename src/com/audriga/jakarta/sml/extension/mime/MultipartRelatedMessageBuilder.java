package com.audriga.jakarta.sml.extension.mime;

import com.audriga.jakarta.sml.extension.model.MimeTextContent;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;

public class MultipartRelatedMessageBuilder extends AbstractMessageBuilder<MultipartRelatedMessageBuilder> {
    private MimeTextContent textBody;

    public MultipartRelatedMessageBuilder textBody(String textBody) {
        if (textBody != null) {
            this.textBody = new MimeTextContent(textBody, "utf-8");
        }
        return self();
    }

    @Override
    protected MultipartRelatedMessageBuilder self() {
        return this;
    }

    @Override
    public StructuredMimeMessageWrapper build() throws MessagingException {

        StructuredMimeMessageWrapper sm = initMessage();

        MimeMultipart alternative =  new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.ALTERNATIVE)
                .addBodyPartText(textBody)
                .addBodyPartHtml(htmlBody)
                .build();

        MimeMultipart mm =  new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.RELATED)
                .addBodyPart(alternative)
                .addBodyPartJsonLd(structuredData, "utf-8")
                .build();
        sm.resetContent(mm);

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
