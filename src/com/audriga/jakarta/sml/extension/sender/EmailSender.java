package com.audriga.jakarta.sml.extension.sender;

import com.audriga.jakarta.sml.extension.mime.*;
import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import jakarta.mail.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
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

    public void sendEmail(StructuredMimeMessageWrapper message) throws MessagingException {
        Transport transport = session.getTransport();
        transport.send(message.getMimeMessage());
    }

    public void sendEmail(Address[] to, Address[] from, String exampleName) throws MessagingException {
        String[] parts = exampleName.split("-");
        String builderType = parts[0];
        boolean htmlLast = false;
        String subject = "Generated EML for " + exampleName;
        String textBody = null;
        String htmlBody = null;
        List<StructuredData> structuredDataList = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            switch (parts[i]) {
                case "text":
                    textBody = "This is a text body for " + exampleName;
                    break;
                case "html":
                    htmlBody = "<html><body>This is an <b>HTML</b> body for " + exampleName + "</body></html>";
                    htmlLast = true;
                    break;
                case "json":
                    String jsonLd = "{ \"@context\": \"http://schema.org\", \"@type\": \"EmailMessage\", \"name\": \"" + exampleName + "\" }";
                    structuredDataList.add(new StructuredData(jsonLd));
                    break;
                default:
                    throw new IllegalArgumentException(
                            String.format("Unknown body part '%s' in example name '%s' taken from email subject.",
                                    parts[i],
                                    exampleName)
                    );
            }
        }

        Address singleTo = to[0];
        mLogger.log(Level.INFO, "Sender return address is " + singleTo);
        StructuredMimeMessageWrapper message;

        switch (builderType) {
            case "inline":
                message = new InlineHtmlMessageBuilder()
                        .subject(subject)
                        .textBody(textBody)
                        .htmlBody(htmlBody)
                        .htmlLast(htmlLast)
                        .structuredData(structuredDataList)
                        .to(singleTo)
                        .from(from)
                        .session(session)
                        .build();
                break;
            case "html":
                message = new HtmlOnlyMessageBuilder()
                        .subject(subject)
                        .htmlBody(htmlBody)
                        .structuredData(structuredDataList)
                        .to(singleTo)
                        .from(from)
                        .session(session)
                        .build();
                break;
            case "alternative":
                message = new MultipartAlternativeMessageBuilder()
                        .subject(subject)
                        .textBody(textBody)
                        .htmlBody(htmlBody)
                        .structuredData(structuredDataList)
                        .to(singleTo)
                        .from(from)
                        .session(session)
                        .build();
                break;
            case "related":
                message = new MultipartRelatedMessageBuilder()
                        .subject(subject)
                        .textBody(textBody)
                        .htmlBody(htmlBody)
                        .structuredData(structuredDataList)
                        .to(singleTo)
                        .from(from)
                        .session(session)
                        .build();
                break;
            default:
                throw new IllegalArgumentException("Unknown builder type: " + builderType);
        }
        sendEmail(message);
    }
}