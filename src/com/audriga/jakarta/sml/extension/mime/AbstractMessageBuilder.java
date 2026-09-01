package com.audriga.jakarta.sml.extension.mime;

import com.audriga.jakarta.sml.extension.model.MimeTextContent;
import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;
import jakarta.activation.DataHandler;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;

import java.util.Properties;

public abstract class AbstractMessageBuilder<T extends AbstractMessageBuilder<T>> {
    static {
        DataHandler.setDataContentHandlerFactory(new StructuredDataContentHandlerFactory());
    }

    protected String subject;
    protected MimeTextContent htmlBody;
    protected JsonLdWrapper structuredData;
    protected Address[] from;
    protected Address to;
    protected Session session;

    public T session(Session session) {
        this.session = session;
        return self();
    }

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

    public T structuredData(JsonLdWrapper structuredData) {
        this.structuredData = structuredData;
        return self();
    }

    protected abstract T self();

    public abstract StructuredMimeMessageWrapper build() throws MessagingException;



    // TODO move to dedicated child class?
    protected static String insertJsonLdInHtml(MimeTextContent htmlContent, JsonLdWrapper structuredData, String htmlTag) {
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
            return html.substring(0, insertPosition) + "\n<script type=\"" + JsonLdWrapper.MIME_TYPE + "\">\n" + structuredData.getJsonLdText() + "\n</script>" + html.substring(insertPosition);
        } else {
            throw new IllegalArgumentException("HTML does not contain <head> tag.");
        }
    }

    protected StructuredMimeMessageWrapper initMessage() {
        if (session == null) {
            Properties properties = System.getProperties();
            session = Session.getDefaultInstance(properties, null);
        }
        return new StructuredMimeMessageWrapper(session);
    }
}