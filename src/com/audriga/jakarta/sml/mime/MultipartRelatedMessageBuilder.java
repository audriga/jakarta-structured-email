package com.audriga.jakarta.sml.mime;

import com.audriga.jakarta.sml.model.MimeTextContent;
import com.audriga.jakarta.sml.model.StructuredData;
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
        if (structuredData != null && structuredData.size() > 1) {
            throw new IllegalArgumentException("Only one structured data object is supported for now.");
        }

        StructuredData structuredDataPart = null;
        if (structuredData != null && !structuredData.isEmpty()) {
            structuredDataPart = structuredData.get(0);
        }

        StructuredMimeMessageWrapper sm = initMessage();

        MimeMultipart alternative =  new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.ALTERNATIVE)
                .addBodyPartText(textBody)
                .addBodyPartHtml(htmlBody)
                .build();

        MimeMultipart mm =  new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.RELATED)
                .addBodyPart(alternative)
                .addBodyPartJsonLd(structuredDataPart, "utf-8")
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
