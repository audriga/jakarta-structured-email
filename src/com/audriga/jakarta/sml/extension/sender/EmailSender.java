package com.audriga.jakarta.sml.extension.sender;

import com.audriga.jakarta.sml.extension.mime.*;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.logging.Logger;

public class EmailSender {
    private static final Logger mLogger = Logger.getLogger(EmailSender.class.getName());
    private final Session session;
    private Properties properties = new Properties();

    public EmailSender() {
        Properties properties = System.getProperties();
        session = Session.getDefaultInstance(properties, null);
    }

    public EmailSender(Properties props) {
        properties = props;
        this.session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("mail.user"), properties.getProperty("mail.password"));
            }
        });
    }

    public Session getSession() {
        return session;
    }

    public void sendEmail(StructuredMimeMessageWrapper message) throws MessagingException {
        Transport transport = session.getTransport();
        MimeMessage mimeMessage = message.getMimeMessage();
        transport.connect();
        mimeMessage.saveChanges();
        transport.sendMessage(mimeMessage, message.getReplyTo());
        transport.close();
    }

}