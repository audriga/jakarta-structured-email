package com.audriga.jakarta.sml.mime;

import com.audriga.jakarta.sml.model.MimeTextContent;
import com.audriga.jakarta.sml.model.StructuredData;
import com.audriga.jakarta.sml.model.StructuredSyntax;
import jakarta.activation.DataHandler;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;

import java.util.List;

public abstract class AbstractMessageBuilder<T extends AbstractMessageBuilder<T>> {
    static {
        DataHandler.setDataContentHandlerFactory(new StructuredDataContentHandlerFactory());
    }

    protected String subject;
    protected MimeTextContent htmlBody;
    protected List<StructuredData> structuredData;
    protected Address[] from;
    protected Address to;

    public T subject(String subject) {
        this.subject = subject;
        return self();
    }

    public T from(Address[] from) {
        this.from = from;
        return self();
    }

    public T from(String address) throws MessagingException {
        this.from = InternetAddress.parse(address);
        return self();
    }

    public T to(Address to) {
        this.to = to;
        return self();
    }

    public T to(String address) throws MessagingException {
        this.to = new InternetAddress(address);
        return self();
    }

    public T htmlBody(String htmlBody) {
        if (htmlBody != null) {
            this.htmlBody = new MimeTextContent(htmlBody, "utf-8");
        }
        return self();
    }

    public T htmlBody(MimeTextContent htmlBody) {
        this.htmlBody = htmlBody;
        return self();
    }

    public T structuredData(List<StructuredData> structuredData) {
        this.structuredData = structuredData;
        return self();
    }

    protected abstract T self();

    public abstract StructuredMimeMessageWrapper build() throws MessagingException;

    // TODO move to dedicated child class?
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

    // TODO move to dedicated child class?
    protected static String insertJsonLdInHtml(MimeTextContent htmlContent, StructuredData structuredData, String htmlTag) {
        if (htmlContent == null) {
            return null;
        }
        if (structuredData == null) {
            return htmlContent.getText();
        }

        if (htmlTag == null) {
            htmlTag = "<body>";
        } else {
            htmlTag = "<" + htmlTag + ">";
        }

        String html = htmlContent.getText();
        int index = html.indexOf(htmlTag);
        if (index != -1) {
            int insertPosition = index + htmlTag.length();
            return html.substring(0, insertPosition) + "\n<script type=\"" + StructuredData.MIME_TYPE + "\">\n" + structuredData.getBody() + "\n</script>" + html.substring(insertPosition);
        } else {
            throw new IllegalArgumentException("HTML does not contain <head> tag.");
        }
    }
}