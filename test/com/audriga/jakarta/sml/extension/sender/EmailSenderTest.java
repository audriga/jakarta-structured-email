package com.audriga.jakarta.sml.extension.sender;

import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.data.AbstractEmail;
import com.audriga.jakarta.sml.data.ExampleEmail;
import com.audriga.jakarta.sml.data.SimpleEmail;
import com.audriga.jakarta.sml.extension.mime.*;
import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;
import jakarta.activation.FileDataSource;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailSenderTest {
    EmailSender sender;
    private static final Logger mLogger = Logger.getLogger(EmailSenderTest.class.getName());
    private Address[] to;
    private Address[] from;

    private void sendEmail(Address[] to, Address[] from, AbstractEmail email) throws MessagingException, URISyntaxException {
        String builderType = email.getBuilderType();
        boolean htmlLast = email.isHtmlLast();
        String subject = SimpleEmail.getSubject();
        String textBody = email.getTextBody();
        String htmlBody = email.getHtmlBody();
        FileDataSource attachment = email.getAttachment();
        String attachmentName = email.getAttachmentName();
        JsonLdWrapper jsonLdWrapper = email.getJson();

        Address singleTo = to[0];
        mLogger.log(Level.INFO, "Sender return address is " + singleTo);
        StructuredMimeMessageWrapper message;

        switch (email.getBuilderType()) {
            case "inline":
                message = new InlineHtmlMessageBuilder()
                        .subject(subject)
                        .textBody(textBody)
                        .htmlBody(htmlBody)
                        .htmlLast(htmlLast)
                        .structuredData(jsonLdWrapper)
                        .to(singleTo)
                        .from(from)
                        .addAttachment(attachment, attachmentName)
                        .build();
                break;
            case "html":
                message = new HtmlOnlyMessageBuilder()
                        .subject(subject)
                        .htmlBody(htmlBody)
                        .structuredData(jsonLdWrapper)
                        .to(singleTo)
                        .from(from)
                        .build();
                break;
            case "alternative":
                message = new MultipartAlternativeMessageBuilder()
                        .subject(subject)
                        .textBody(textBody)
                        .htmlBody(htmlBody)
                        .htmlLast(htmlLast)
                        .structuredData(jsonLdWrapper)
                        .to(singleTo)
                        .from(from)
                        .build();
                break;
            case "related":
                message = new MultipartRelatedMessageBuilder()
                        .subject(subject)
                        .textBody(textBody)
                        .htmlBody(htmlBody)
                        .structuredData(jsonLdWrapper)
                        .to(singleTo)
                        .from(from)
                        .build();
                break;
            default:
                throw new IllegalArgumentException("Unknown builder type: " + builderType);
        }
        sender.sendEmail(message);
    }

    @BeforeTest
    public void setUp() throws IOException, MessagingException, URISyntaxException {
        Path propPath = TestUtils.readResourceAsPath("smtp.properties");
        Properties props = new Properties();

        try (FileInputStream input = new FileInputStream(String.valueOf(propPath))) {
            props.load(input);
        }

        sender = new EmailSender(props);

        // Send the email
        to = new InternetAddress[]{new InternetAddress(props.getProperty("mail.to"))};
        from = new InternetAddress[]{new InternetAddress(props.getProperty("mail.from"))};
    }

    @Test
    public void testSendSmlEmailExample() throws MessagingException, URISyntaxException {
        sendEmail(to, from, new ExampleEmail());
    }

}
