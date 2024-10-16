package com.audriga.jakarta.sml.sender;

import com.audriga.jakarta.sml.TestUtils;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Properties;

public class EmailSenderIT {

    @Test(groups = "integration")
    public void testSendSmlEmailExample() throws IOException, MessagingException, URISyntaxException {
        Path propPath = TestUtils.readResourceAsPath("smtp.properties");
        Properties props = new Properties();

        try (FileInputStream input = new FileInputStream(String.valueOf(propPath))) {
            props.load(input);
        }

        EmailSender emailSender = new EmailSender(props);

        // Send the email
        Address[] to = new InternetAddress[]{new InternetAddress(props.getProperty("mail.to"))};
        Address[] from = new InternetAddress[]{new InternetAddress(props.getProperty("mail.from"))};
        emailSender.sendEmail(to, from, props.getProperty("mail.example"));
    }
}