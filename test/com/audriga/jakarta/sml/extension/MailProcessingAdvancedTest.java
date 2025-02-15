package com.audriga.jakarta.sml.extension;

import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.extension.mime.*;
import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import com.audriga.jakarta.sml.data.MultipartRelatedEmail;
import com.audriga.jakarta.sml.data.SimpleEmail;
import jakarta.mail.MessagingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MailProcessingAdvancedTest {
    @BeforeClass(groups = "unit")
    public void setUp() {
        TestUtils.initLogging();
    }

    @DataProvider(name = "emailVariantsAlternative")
    public Object[][] emailVariantsAlternative() {
        return new Object[][] {
                { "eml/alternative-text-html-json.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), SimpleEmail.getHtmlBody(), SimpleEmail.getJson(), false },
                { "eml/alternative-html-json.eml", SimpleEmail.getSubject(), null, SimpleEmail.getHtmlBody(), SimpleEmail.getJson(), false },
                { "eml/alternative-html.eml", SimpleEmail.getSubject(), null, SimpleEmail.getHtmlBody(), null, false },
                { "eml/alternative-text-json.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), null, SimpleEmail.getJson(), false },
                { "eml/alternative-text-html.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), SimpleEmail.getHtmlBody(), null, false },
                { "eml/alternative-text-json-html.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), SimpleEmail.getHtmlBody(), SimpleEmail.getJson(), true },
        };
    }

    @DataProvider(name = "emailVariantsRelated")
    public Object[][] emailVariantsRelated() {
        return new Object[][] {
                { "eml/related-text-html-json.eml", MultipartRelatedEmail.getSubject(), MultipartRelatedEmail.getTextBody(), MultipartRelatedEmail.getHtmlBody(), MultipartRelatedEmail.getJson() },
                { "eml/related-html-json.eml", MultipartRelatedEmail.getSubject(), null, MultipartRelatedEmail.getHtmlBody(), MultipartRelatedEmail.getJson() },
                { "eml/related-html.eml", MultipartRelatedEmail.getSubject(), null, MultipartRelatedEmail.getHtmlBody(), null },
                { "eml/related-text-json.eml", MultipartRelatedEmail.getSubject(), MultipartRelatedEmail.getTextBody(), null, MultipartRelatedEmail.getJson() },
                { "eml/related-text-html.eml", MultipartRelatedEmail.getSubject(), MultipartRelatedEmail.getTextBody(), MultipartRelatedEmail.getHtmlBody(), null }
        };
    }

    @DataProvider(name = "emailVariantsHtml")
    public Object[][] emailVariantsHtml() {
        return new Object[][] {
                { "eml/html-html-json.eml", SimpleEmail.getSubject(), SimpleEmail.getHtmlBody(), SimpleEmail.getJson() },
                { "eml/html-html.eml", SimpleEmail.getSubject(), SimpleEmail.getHtmlBody(), null },
        };
    }

    @Test(dataProvider = "emailVariantsAlternative", groups = "unit")
    public void testFullMultipartAlternativeGenerator(String emlFilePath, String subject, String textBody, String htmlBody, List<StructuredData> jsonList, boolean htmlLast) throws MessagingException, IOException {
        // Parse
        StructuredMimeMessageWrapper result = TestUtils.parseEmlFile(emlFilePath);

        // Generate
        StructuredMimeMessageWrapper message = new MultipartAlternativeMessageBuilder()
                .subject(subject)
                .textBody(textBody)
                .htmlBody(htmlBody)
                .structuredData(jsonList)
                .htmlLast(htmlLast)
                .build();
        PrintStream out = System.out;
        message.writeTo(out);

        // Compare
        assertTrue(message.getContentType().contains("multipart/alternative"), "Content type should be 'multipart/alternative'");
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

    @Test(dataProvider = "emailVariantsRelated", groups = "unit")
    public void testFullMultipartRelatedGenerator(String emlFilePath, String subject, String textBody, String htmlBody, List<StructuredData> jsonList) throws MessagingException, IOException {
        // Parse
        StructuredMimeMessageWrapper result = TestUtils.parseEmlFile(emlFilePath);

        // Generate
        StructuredMimeMessageWrapper message = new MultipartRelatedMessageBuilder()
                .subject(subject)
                .textBody(textBody)
                .htmlBody(htmlBody)
                .structuredData(jsonList)
                .build();
        PrintStream out = System.out;
        message.writeTo(out);

        // Compare
        assertTrue(message.getContentType().contains("multipart/related"), "Content type should be 'multipart/related'");
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

    @Test(dataProvider = "emailVariantsHtml", groups = "unit")
    public void testFullHtmlOnlyGenerator(String emlFilePath, String subject, String htmlBody, List<StructuredData> jsonList) throws MessagingException, IOException {
        // Parse
        StructuredMimeMessageWrapper result = TestUtils.parseEmlFile(emlFilePath);

        // Generate
        StructuredMimeMessageWrapper message = new HtmlOnlyMessageBuilder()
                        .subject(subject)
                        .htmlBody(htmlBody)
                        .structuredData(jsonList)
                        .build();
        PrintStream out = System.out;
        message.writeTo(out);

        // Compare
        assertTrue(message.getContentType().contains("text/html"), "Content type should be 'text/html'");
        if (jsonList != null && result.getStructuredData() != null) {
            assertEquals(message.getStructuredData().get(0).getBody(), jsonList.get(0).getBody(), "Structured data body should match the input");
            StructuredData generatedJson = message.getStructuredData().get(0);
            StructuredData resultJson = result.getStructuredData().get(0);
            assertEquals(generatedJson.getJson().toString(), resultJson.getJson().toString(), "Structured data of generated message should be equal to the parsed message");
        }
        assertEquals(message.getHtmlBody().getText(), result.getHtmlBody().getText(), "HTML of generated message should be equal to the parsed message");
    }

    @Test(groups = "unit")
    public void testInlineHtmlGeneratorWithJsonLdInHead() throws MessagingException, IOException {
        // Generate
        List<StructuredData> json = SimpleEmail.getJson();
        StructuredMimeMessageWrapper message = new InlineHtmlMessageBuilder()
                .subject(SimpleEmail.getSubject())
                .textBody(SimpleEmail.getTextBody())
                .htmlBody(SimpleEmail.getHtmlBody())
                .structuredData(SimpleEmail.getJson())
                .htmlTag("head")
                .htmlLast(true)
                .build();

        PrintStream out = System.out;
        message.writeTo(out);

        String jsonLdScript = "<script\\s+type=\"application/ld\\+json\">\\s*" + Pattern.quote(json.get(0).getBody()) + "\\s*</script>";
        String regex = "<head>.*" + jsonLdScript + ".*</head>";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(message.getHtmlBody().getText());

        assertTrue(matcher.find(), "HTML body should contain the JSON-LD script in the head tag");
        assertEquals(message.getStructuredData().get(0).getBody(), json.get(0).getBody(), "Structured data body should match the input");
    }
}
