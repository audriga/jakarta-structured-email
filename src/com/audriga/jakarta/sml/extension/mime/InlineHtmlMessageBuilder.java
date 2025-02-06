package com.audriga.jakarta.sml.extension.mime;

import com.audriga.jakarta.sml.extension.model.MimeTextContent;
import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;

import java.util.ArrayList;
import java.util.List;

public class InlineHtmlMessageBuilder extends AbstractMessageBuilder<InlineHtmlMessageBuilder> {

    private MimeTextContent textBody;
    private String htmlTag;
    private boolean htmlLast = true;
    private final List<MimeBodyPart> attachments = new ArrayList<>();

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

    public InlineHtmlMessageBuilder addAttachment(FileDataSource fileSource, String fileName) throws MessagingException {
        if (fileSource == null) {
            return this;
        }
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setDataHandler(new DataHandler(fileSource));
        if (fileName != null) {
            attachmentPart.setFileName(fileName);
        }
        attachments.add(attachmentPart);
        return this;
    }


    @Override
    public StructuredMimeMessageWrapper build() throws MessagingException {
        StructuredData jsonStructuredData = checkStructuredDataToInsert(structuredData);
        String html = insertJsonLdInHtml(htmlBody, jsonStructuredData, htmlTag);

        StructuredMimeMessageWrapper sm = initMessage();

        MimeMultipartBuilder multipartAlternativeBuilder = new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.ALTERNATIVE);

        if (htmlLast) {
            multipartAlternativeBuilder.addBodyPartText(textBody);

            if (html != null) {
                multipartAlternativeBuilder.addBodyPartHtml(new MimeTextContent(html, "utf-8"));
            }
        } else {
            if (html != null) {
                multipartAlternativeBuilder.addBodyPartHtml(new MimeTextContent(html, "utf-8"));
            }

            multipartAlternativeBuilder.addBodyPartText(textBody);
        }

        if (attachments.isEmpty()) {
            sm.resetContent(multipartAlternativeBuilder.build());
        } else {
            MimeMultipartBuilder multipartMixedBuilder = new MimeMultipartBuilder(MimeMultipartBuilder.MULTIPART.MIXED);
            for (MimeBodyPart attachment : attachments) {
                multipartMixedBuilder.addBodyPart(attachment);
            }
            multipartMixedBuilder.addBodyPart(multipartAlternativeBuilder.build());

            sm.resetContent(multipartMixedBuilder.build());
        }


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
