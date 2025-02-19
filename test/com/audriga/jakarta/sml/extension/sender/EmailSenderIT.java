package com.audriga.jakarta.sml.extension.sender;

import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.data.SimpleEmail;
import com.audriga.jakarta.sml.extension.mime.*;
import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import jakarta.activation.FileDataSource;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.InternetAddress;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailSenderIT {
    EmailSender sender;
    private static final Logger mLogger = Logger.getLogger(EmailSenderIT.class.getName());
    private static final String INLINE_BUILDER = "inline";
    private static final String HTML_BUILDER = "html";
    private static final String ALT_BUILDER = "alternative";
    private static final String RELATED_BUILDER = "related";

    private void sendEmail(Address[] to, Address[] from, String exampleName) throws MessagingException, URISyntaxException {
        String[] parts = exampleName.split("-");
        String builderType = parts[0];
        boolean htmlLast = false;
        String subject = String.format("[%s] %s", exampleName, SimpleEmail.getSubject());
        String textBody = null;
        String htmlBody = null;
        FileDataSource attachment = null;
        String attachmentName = null;
        List<StructuredData> structuredDataList = new ArrayList<>();
        String disposition = null;

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
                case "inline":
                    disposition = Part.INLINE;
                    if (!Objects.equals(builderType, ALT_BUILDER)) {
                        mLogger.log(Level.WARNING, "For now inline disposition is only supported for multipart alternative Messages.");
                    }
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
            case INLINE_BUILDER:
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
            case HTML_BUILDER:
                message = new HtmlOnlyMessageBuilder()
                        .subject(subject)
                        .htmlBody(htmlBody)
                        .structuredData(structuredDataList)
                        .to(singleTo)
                        .from(from)
                        .build();
                break;
            case ALT_BUILDER:
                message = new MultipartAlternativeMessageBuilder()
                        .disposition(disposition)
                        .subject(subject)
                        .textBody(textBody)
                        .htmlBody(htmlBody)
                        .htmlLast(htmlLast)
                        .structuredData(structuredDataList)
                        .to(singleTo)
                        .from(from)
                        .build();
                break;
            case RELATED_BUILDER:
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
