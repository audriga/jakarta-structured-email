package com.audriga.jakarta.sml.extension;
import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.extension.mime.InlineHtmlMessageBuilder;
import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import com.audriga.jakarta.sml.extension.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.data.SimpleEmail;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class MailProcessingTest {
    @BeforeClass
    public void setUp() {
        TestUtils.initLogging();
    }

    @DataProvider(name = "emailVariantsInline")
    public Object[][] emailVariantsInline() throws URISyntaxException {
        return new Object[][] {
                { "eml/inline-text-html-json.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), SimpleEmail.getHtmlBody(), SimpleEmail.getJson(), true, null, null },
                { "eml/inline-html-json.eml", SimpleEmail.getSubject(), null, SimpleEmail.getHtmlBody(), SimpleEmail.getJson(), true, null, null },
                { "eml/inline-html.eml", SimpleEmail.getSubject(), null, SimpleEmail.getHtmlBody(), null, true, null, null },
                { "eml/inline-text.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), null, SimpleEmail.getJson(), true, null, null },
                { "eml/inline-text-html.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), SimpleEmail.getHtmlBody(), null, true, null, null },
                { "eml/inline-html-text-json.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), SimpleEmail.getHtmlBody(), null, false, null, null },
                { "eml/inline-html-text-json-attachment.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), SimpleEmail.getHtmlBody(), null, false, SimpleEmail.getAttachment(), SimpleEmail.getAttachmentName() },
        };
    }

    /* This is copy+pasted from README */
    @Test(groups = "unit")
    public void testInlineHtmlGeneratorSimple() throws MessagingException {
        // Comment email content elements
        String emailSubject = "My first structured email";
        String textEmailBody = "This is a test email";
        String htmlEmailBody = "<html><body>This is a <b>test email</b></body></html>";

        // Structured data
        String jsonLd = "{\r\n    \"@context\":              \"http://schema.org\",\r\n    \"@type\":                 \"EventReservation\",\r\n    \"reservationId\":         \"MBE12345\",\r\n    \"underName\": {\r\n        \"@type\":               \"Person\",\r\n        \"name\":                \"Noah Baumbach\"\r\n    },\r\n    \"reservationFor\": {\r\n        \"@type\":               \"Event\",\r\n        \"name\":                \"Make Better Email 2024\",\r\n        \"startDate\":           \"2024-10-15\",\r\n        \"organizer\": {\r\n            \"@type\":            \"Organization\",\r\n            \"name\":             \"Fastmail Pty Ltd.\",\r\n            \"logo\":             \"https://www.fastmail.com/assets/images/FM-Logo-RGB-IiFj8alCx1-3073.webp\"\r\n        },\r\n        \"location\": {\r\n            \"@type\":             \"Place\",\r\n            \"name\":              \"Isode Ltd\",\r\n            \"address\": {\r\n                \"@type\":           \"PostalAddress\",\r\n                \"streetAddress\":   \"14 Castle Mews\",\r\n                \"addressLocality\": \"Hampton\",\r\n                \"addressRegion\":   \"Greater London\",\r\n                \"postalCode\":      \"TW12 2NP\",\r\n                \"addressCountry\":  \"UK\"\r\n            }\r\n        }\r\n    }\r\n}";

        List<StructuredData> structuredDataList = new ArrayList<>();
        structuredDataList.add(new StructuredData(jsonLd));

        StructuredMimeMessageWrapper message = new InlineHtmlMessageBuilder()
                .subject(emailSubject)
                .textBody(textEmailBody)
                .htmlBody(htmlEmailBody)
                .structuredData(structuredDataList)
                .build();

        assertEquals(message.getTextBody().getText(), textEmailBody, "Text of generated message should be equal to the parsed message");
        assertEquals(message.getStructuredData().get(0).getBody(), jsonLd, "Structured data body should match the input");
        StructuredData generatedJson = message.getStructuredData().get(0);
        StructuredData resultJson = new StructuredData(jsonLd);
        assertEquals(generatedJson.getJson().toString(), resultJson.getJson().toString(), "Structured data of generated message should be equal to the parsed message");
    }

    @Test(dataProvider = "emailVariantsInline", groups = "unit")
    public void testInlineHtmlGenerator(String emlFilePath, String subject, String textBody, String htmlBody, List<StructuredData> jsonList, boolean htmlLast, FileDataSource attachmentSource, String attachmentName) throws MessagingException, IOException {
        // Parse
        StructuredMimeMessageWrapper result = TestUtils.parseEmlFile(emlFilePath);

        // Generate
        StructuredMimeMessageWrapper message = new InlineHtmlMessageBuilder()
                .subject(subject)
                .textBody(textBody)
                .htmlBody(htmlBody)
                .structuredData(jsonList)
                .htmlLast(htmlLast)
                .addAttachment(attachmentSource, attachmentName)
                .build();
        PrintStream out = System.out;
        message.writeTo(out);

        // Compare
        if (jsonList != null && result.getStructuredData() != null) {
            assertEquals(message.getStructuredData().get(0).getBody(), jsonList.get(0).getBody(), "Structured data body should match the input");
            StructuredData generatedJson = message.getStructuredData().get(0);
            StructuredData resultJson = result.getStructuredData().get(0);
            assertEquals(generatedJson.getJson().toString(), resultJson.getJson().toString(), "Structured data of generated message should be equal to the parsed message");
        }
        if (htmlBody != null) {
            assertEquals(message.getHtmlBody().getText(), result.getHtmlBody().getText(), "HTML of generated message should be equal to the parsed message");
        }
        if (textBody != null) {
            assertEquals(message.getTextBody().getText(), result.getTextBody().getText(), "Text of generated message should be equal to the parsed message");
        }
    }


}