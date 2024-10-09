package com.audriga.jakarta.sml.test;
import com.audriga.jakarta.sml.generator.InlineHtmlMessageGenerator;
import com.audriga.jakarta.sml.model.StructuredData;
import com.audriga.jakarta.sml.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.test.data.SimpleEmail;
import jakarta.mail.MessagingException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class MailProcessingTest {
    @BeforeClass
    public void setUp() {
        TestUtils.initLogging();
    }

    @DataProvider(name = "emailVariantsInline")
    public Object[][] emailVariantsInline() {
        return new Object[][] {
                { "eml/inline-text-html-json.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), SimpleEmail.getHtmlBody(), SimpleEmail.getJson(), true },
                { "eml/inline-html-json.eml", SimpleEmail.getSubject(), null, SimpleEmail.getHtmlBody(), SimpleEmail.getJson(), true },
                { "eml/inline-html.eml", SimpleEmail.getSubject(), null, SimpleEmail.getHtmlBody(), null, true },
                { "eml/inline-text.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), null, SimpleEmail.getJson(), true },
                { "eml/inline-text-html.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), SimpleEmail.getHtmlBody(), null, true },
                { "eml/inline-html-text-json.eml", SimpleEmail.getSubject(), SimpleEmail.getTextBody(), SimpleEmail.getHtmlBody(), null, false }
        };
    }

    @Test(dataProvider = "emailVariantsInline")
    public void testInlineHtmlGenerator(String emlFilePath, String subject, String textBody, String htmlBody, List<StructuredData> jsonList, boolean htmlLast) throws MessagingException, IOException {
        // Parse
        StructuredMimeMessageWrapper result = TestUtils.parseEmlFile(emlFilePath);

        // Generate
        InlineHtmlMessageGenerator gen = new InlineHtmlMessageGenerator();
        StructuredMimeMessageWrapper message = gen.generate(subject, textBody, htmlBody, jsonList, null, htmlLast);
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