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
        this.session = Session.getInstance(properties);
    }

    public void sendEmail(StructuredMimeMessageWrapper message) throws MessagingException {
        MimeMessage mMessage = message.getMimeMessage();

        Transport transport = session.getTransport();
        transport.connect(properties.getProperty("mail.user"), properties.getProperty("mail.password"));
        transport.sendMessage(mMessage, mMessage.getAllRecipients());
        transport.close();
    }

}