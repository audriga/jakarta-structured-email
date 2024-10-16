package com.audriga.jakarta.sml.sender;

import com.audriga.jakarta.sml.TestUtils;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class EmailSenderIT {
    @Test
    public void testSendSmlEmailExample() throws IOException, MessagingException, URISyntaxException {
        Path propPath = TestUtils.readResourceAsPath("smtp.properties");
        EmailSender emailSender = new EmailSender(propPath.toString());

        // Send the email
        Address[] to = new InternetAddress[]{new InternetAddress("joris@audriga.com")};
        Address[] from = new InternetAddress[]{new InternetAddress("joris@audriga.com")};
        emailSender.sendEmail(to, from, "inline-text-html-json");
    }
}