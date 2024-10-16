package com.audriga.jakarta.sml.mime;

import com.audriga.jakarta.sml.model.MimeTextContent;
import com.audriga.jakarta.sml.model.StructuredData;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StructuredMimeMessageWrapper {
    private List<StructuredData> structuredData;
    private final MimeMessage mm;
    private MimeTextContent textBody;
    private MimeTextContent htmlBody;

    private String from;

    public StructuredMimeMessageWrapper(Session session) {
        mm = new MimeMessage(session);
    }

    public StructuredMimeMessageWrapper(MimeMessage source) throws MessagingException {
        mm = source;
        setFromStr(source.getFrom());
    }

    public StructuredMimeMessageWrapper(MimeMessage source, List<StructuredData> structuredData) throws MessagingException {
        this(source);
        this.structuredData = structuredData;
    }

    public List<StructuredData> getStructuredData() {
        return structuredData;
    }

    public void setStructuredData(List<StructuredData> structuredData) {
        this.structuredData = structuredData;
    }

    public void addStructuredData(StructuredData sd) {
        if (structuredData != null) {
            structuredData.add(sd);
        } else {
            structuredData = new ArrayList<>();
            structuredData.add(sd);
        }
    }

    public Address getSender() throws MessagingException {
        return mm.getSender();
    }

    public void setSubject(String subject) throws MessagingException {
        mm.setSubject(subject);
    }

    public void setFrom(Address[] address) throws MessagingException {
        if (address != null && address.length > 0) mm.setFrom(address[0]);
        setFromStr(address);
    }

    public void setFrom(String address) throws MessagingException {
        mm.setFrom(address);
        from = address;
    }

    public String getFrom() {
        return from;
    }

    public void addRecipient(Message.RecipientType to, Address address) throws MessagingException {
        mm.addRecipient(to, address);
    }

    public void addRecipients(Message.RecipientType to, String address) throws  MessagingException {
        mm.addRecipients(to, address);
    }

    /* Sets main content of MIME Message. WARNING: Resets structured data and body part contents in the Wrapper. */
    public void resetContent(Multipart mp) throws MessagingException {
        mm.setContent(mp);
        structuredData = null;
        textBody = null;
        htmlBody = null;
    }

    public void setText(String text, String encoding, String subtype) throws MessagingException {
        mm.setText(text, encoding, subtype);
    }

    public void writeTo(PrintStream out) throws MessagingException, IOException {
        mm.writeTo(out);
    }

    public void writeTo(ByteArrayOutputStream out) throws MessagingException, IOException {
        mm.writeTo(out);
    }

    public String getContentType() throws MessagingException {
        return mm.getContentType();
    }

    public MimeTextContent getTextBody() {
        return textBody;
    }

    public void setTextBody(MimeTextContent textBody) {
        this.textBody = textBody;
    }

    public MimeTextContent getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(MimeTextContent htmlBody) {
        this.htmlBody = htmlBody;
    }

    private void setFromStr(Address[] from) {
        this.from = "(from)";
        if (from != null && from.length > 0) {
            if (from[0] instanceof InternetAddress) {
                this.from = ((InternetAddress) from[0]).getAddress();
            } else {
                this.from = "" + from[0];
            }
        }
    }

    public Date getReceivedDate() throws MessagingException {
        return mm.getReceivedDate();
    }

    public String getMessageID() throws MessagingException {
        return mm.getMessageID();
    }

    public int getMessageNumber() {
        return mm.getMessageNumber();
    }

    public String getSubject() throws MessagingException {
        return mm.getSubject();
    }

    public void setRecipient(Message.RecipientType type, Address address) throws MessagingException {
        mm.setRecipient(type, address);
    }

    public void getRecipients(Message.RecipientType type) throws MessagingException {
        mm.getRecipients(type);
    }

    public void setReplyTo(Address[] addresses) throws MessagingException {
        mm.setReplyTo(addresses);
    }

    public Address[] getReplyTo() throws MessagingException {
        return mm.getReplyTo();
    }

    public MimeMessage getMimeMessage() {
        return mm;
    }
}
