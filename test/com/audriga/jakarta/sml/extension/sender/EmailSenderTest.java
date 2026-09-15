package com.audriga.jakarta.sml.extension.sender;

import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.data.*;
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
        String subject = email.getSubject();
        String textBody = email.getTextBody();
        String htmlBody = email.getHtmlBody();
        FileDataSource attachment = email.getAttachment();
        String attachmentName = email.getAttachmentName();
        JsonLdWrapper jsonLdWrapper = email.getJson();

        Address singleTo = to[0];
        mLogger.log(Level.INFO, "Sender return address is " + singleTo);
        StructuredMimeMessageWrapper message = switch (email.getBuilderType()) {
            case "inline" -> new InlineHtmlMessageBuilder()
                    .subject(subject)
                    .textBody(textBody)
                    .htmlBody(htmlBody)
                    .htmlLast(htmlLast)
                    .structuredData(jsonLdWrapper)
                    .to(singleTo)
                    .from(from)
                    .addAttachment(attachment, attachmentName)
                    .build();
            case "html" -> new HtmlOnlyMessageBuilder()
                    .subject(subject)
                    .htmlBody(htmlBody)
                    .structuredData(jsonLdWrapper)
                    .to(singleTo)
                    .from(from)
                    .build();
            case "alternative" -> new MultipartAlternativeMessageBuilder()
                    .subject(subject)
                    .textBody(textBody)
                    .htmlBody(htmlBody)
                    .htmlLast(htmlLast)
                    .structuredData(jsonLdWrapper)
                    .to(singleTo)
                    .from(from)
                    .build();
            case "related" -> new MultipartRelatedMessageBuilder()
                    .subject(subject)
                    .textBody(textBody)
                    .htmlBody(htmlBody)
                    .structuredData(jsonLdWrapper)
                    .to(singleTo)
                    .from(from)
                    .build();
            default -> throw new IllegalArgumentException("Unknown builder type: " + builderType);
        };

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

    @Test
    public void testSendRdfAExample() throws MessagingException, URISyntaxException {
        sendEmail(to, from, new RdfAEmail());
    }

    @Test
    public void testSendMicrodataExample() throws MessagingException, URISyntaxException {
        sendEmail(to, from, new MicrodataEmail());
    }

    @Test
    public void testSendMicrodataAlternativeExample() throws MessagingException, URISyntaxException {
        sendEmail(to, from, new MicrodataAlternativeEmail());
    }

    @Test
    public void testSendJSONLDExample() throws MessagingException, URISyntaxException {
        sendEmail(to, from, new JsonLdEmail());
    }

    @Test
    public void testAttachmentExample() throws MessagingException, URISyntaxException {
        sendEmail(to, from, new AttachmentEmail());
    }

    @Test
    public void testGlobalDataAttributesExample() throws MessagingException, URISyntaxException {
        sendEmail(to, from, new GlobalDataAttributeEmail());
    }

    @Test
    public void testSendJSONLDInlineExample() throws MessagingException, URISyntaxException {
        sendEmail(to, from, new JsonLdInlineEmail());
    }
}
