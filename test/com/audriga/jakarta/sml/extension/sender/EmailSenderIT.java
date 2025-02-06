package com.audriga.jakarta.sml.extension.sender;

import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.data.SimpleEmail;
import com.audriga.jakarta.sml.extension.mime.*;
import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import jakarta.activation.FileDataSource;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
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

public class EmailSenderIT {
    EmailSender sender;
    private static final Logger mLogger = Logger.getLogger(EmailSenderIT.class.getName());

    private void sendEmail(Address[] to, Address[] from, String exampleName) throws MessagingException, URISyntaxException {
        String[] parts = exampleName.split("-");
        String builderType = parts[0];
        boolean htmlLast = false;
        String subject = SimpleEmail.getSubject();
        String textBody = null;
        String htmlBody = null;
        FileDataSource attachment = null;
        String attachmentName = null;
        List<StructuredData> structuredDataList = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            switch (parts[i]) {
                case "text":
                    textBody  = SimpleEmail.getTextBody();
                    break;
                case "html":
                    htmlBody = SimpleEmail.getHtmlBody();
                    if (i == (parts.length - 1)) {
                        htmlLast = true;
                    }
                    break;
                case "json":
                    structuredDataList.addAll(SimpleEmail.getJson());
                    break;
                case "attachment":
                    attachment = SimpleEmail.getAttachment();
                    attachmentName = SimpleEmail.getAttachmentName();
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
                        .addAttachment(attachment, attachmentName)
                        .build();
                break;
            case "html":
                message = new HtmlOnlyMessageBuilder()
                        .subject(subject)
                        .htmlBody(htmlBody)
                        .structuredData(structuredDataList)
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
                        .structuredData(structuredDataList)
                        .to(singleTo)
                        .from(from)
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
                        .build();
                break;
            default:
                throw new IllegalArgumentException("Unknown builder type: " + builderType);
        }
        sender.sendEmail(message);
    }

    @Test(groups = "integration")
    public void testSendSmlEmailExample() throws IOException, MessagingException, URISyntaxException {
        Path propPath = TestUtils.readResourceAsPath("smtp.properties");
        Properties props = new Properties();

        try (FileInputStream input = new FileInputStream(String.valueOf(propPath))) {
            props.load(input);
        }

        sender = new EmailSender(props);

        // Send the email
        Address[] to = new InternetAddress[]{new InternetAddress(props.getProperty("mail.to"))};
        Address[] from = new InternetAddress[]{new InternetAddress(props.getProperty("mail.from"))};
        sendEmail(to, from, props.getProperty("mail.example"));
    }
}
