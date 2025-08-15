package com.audriga.jakarta.sml.extension;

import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.extension.mime.*;
import com.audriga.jakarta.sml.extension.model.MimeTextContent;
import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import com.audriga.jakarta.sml.data.MultipartRelatedEmail;
import com.audriga.jakarta.sml.data.SimpleEmail;
import com.audriga.jakarta.sml.h2lj.model.StructuredSyntax;
import com.audriga.jakarta.sml.h2lj.parser.StructuredDataExtractionUtils;
import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.*;

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

    @DataProvider(name = "emailVariantsHtmlNewsletter")
    public Object[][] emailVariantsHtmlNewsletter() {
        return new Object[][] {
                { "eml/html-nyt-newsletter-original.eml"},
        };
    }

    @Test(dataProvider = "emailVariantsAlternative", groups = "unit")
    public void testFullMultipartAlternativeGenerator(String emlFilePath, String subject, String textBody, String htmlBody, JsonLdWrapper jsonLd, boolean htmlLast) throws MessagingException, IOException {
        // Parse
        StructuredMimeMessageWrapper result = TestUtils.parseEmlFile(emlFilePath);

        // Generate
        StructuredMimeMessageWrapper message = new MultipartAlternativeMessageBuilder()
                .subject(subject)
                .textBody(textBody)
                .htmlBody(htmlBody)
                .structuredData(jsonLd)
                .htmlLast(htmlLast)
                .build();
        PrintStream out = System.out;
        message.writeTo(out);

        // Compare
        assertTrue(message.getContentType().contains("multipart/alternative"), "Content type should be 'multipart/alternative'");
        if (jsonLd != null && result.getStructuredData() != null) {
            assertEquals(message.getStructuredData().getJsonLdText(), jsonLd.getJsonLdText(), "Structured data body should match the input");
            JsonLdWrapper generatedJson = message.getStructuredData();
            JsonLdWrapper resultJson = result.getStructuredData();
            assertEquals(generatedJson.getJsonLd().toString(), resultJson.getJsonLd().toString(), "Structured data of generated message should be equal to the parsed message");
        }
        if (htmlBody != null) {
            assertEquals(message.getHtmlBody().getText(), result.getHtmlBody().getText(), "HTML of generated message should be equal to the parsed message");
        }
        if (textBody != null) {
            assertEquals(message.getTextBody().getText(), result.getTextBody().getText(), "Text of generated message should be equal to the parsed message");
        }
    }

    @Test(dataProvider = "emailVariantsRelated", groups = "unit")
    public void testFullMultipartRelatedGenerator(String emlFilePath, String subject, String textBody, String htmlBody, JsonLdWrapper jsonLd) throws MessagingException, IOException {
        // Parse
        StructuredMimeMessageWrapper result = TestUtils.parseEmlFile(emlFilePath);

        // Generate
        StructuredMimeMessageWrapper message = new MultipartRelatedMessageBuilder()
                .subject(subject)
                .textBody(textBody)
                .htmlBody(htmlBody)
                .structuredData(jsonLd)
                .build();
        PrintStream out = System.out;
        message.writeTo(out);

        // Compare
        assertTrue(message.getContentType().contains("multipart/related"), "Content type should be 'multipart/related'");
        if (jsonLd != null && result.getStructuredData() != null) {
            assertEquals(message.getStructuredData().getJsonLdText(), jsonLd.getJsonLdText(), "Structured data body should match the input");
            JsonLdWrapper generatedJson = message.getStructuredData();
            JsonLdWrapper resultJson = result.getStructuredData();
            assertEquals(generatedJson.getJsonLd().toString(), resultJson.getJsonLd().toString(), "Structured data of generated message should be equal to the parsed message");
        }
        if (htmlBody != null) {
            assertEquals(message.getHtmlBody().getText(), result.getHtmlBody().getText(), "HTML of generated message should be equal to the parsed message");
        }
        if (textBody != null) {
            assertEquals(message.getTextBody().getText(), result.getTextBody().getText(), "Text of generated message should be equal to the parsed message");
        }
    }

    @Test(dataProvider = "emailVariantsHtml", groups = "unit")
    public void testFullHtmlOnlyGenerator(String emlFilePath, String subject, String htmlBody, JsonLdWrapper jsonLd) throws MessagingException, IOException {
        // Parse
        StructuredMimeMessageWrapper result = TestUtils.parseEmlFile(emlFilePath);

        // Generate
        StructuredMimeMessageWrapper message = new HtmlOnlyMessageBuilder()
                        .subject(subject)
                        .htmlBody(htmlBody)
                        .structuredData(jsonLd)
                        .build();
        PrintStream out = System.out;
        message.writeTo(out);

        // Compare
        assertTrue(message.getContentType().contains("text/html"), "Content type should be 'text/html'");
        if (jsonLd != null && result.getStructuredData() != null) {
            assertEquals(message.getStructuredData().getJsonLdText(), jsonLd.getJsonLdText(), "Structured data body should match the input");
            JsonLdWrapper generatedJson = message.getStructuredData();
            JsonLdWrapper resultJson = result.getStructuredData();
            assertEquals(generatedJson.getJsonLd().toString(), resultJson.getJsonLd().toString(), "Structured data of generated message should be equal to the parsed message");
        }
        assertEquals(message.getHtmlBody().getText(), result.getHtmlBody().getText(), "HTML of generated message should be equal to the parsed message");
    }

    @Test(groups = "unit")
    public void testInlineHtmlGeneratorWithJsonLdInHead() throws MessagingException, IOException {
        // Generate
        JsonLdWrapper json = SimpleEmail.getJson();
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

        String jsonLdScript = "<script\\s+type=\"application/ld\\+json\">\\s*" + Pattern.quote(json.getJsonLdText()) + "\\s*</script>";
        String regex = "<head>.*" + jsonLdScript + ".*</head>";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(message.getHtmlBody().getText());

        assertTrue(matcher.find(), "HTML body should contain the JSON-LD script in the head tag");
        assertEquals(message.getStructuredData().getJsonLdText(), json.getJsonLdText(), "Structured data body should match the input");
    }

    @Test(dataProvider = "emailVariantsHtmlNewsletter", groups = "unit")
    public void testHtmlNewsletterConversion(String emlFilePath) throws MessagingException, IOException {
        // Parse
        StructuredMimeMessageWrapper result = TestUtils.parseEmlFile(emlFilePath);
        MimeTextContent htmlBody = result.getHtmlBody();
        assertNotNull(htmlBody);
        Document doc = Jsoup.parse(htmlBody.getText());
        Elements links = doc.select("a[href]");
        PrintStream out = System.out;
        Connection session = Jsoup.newSession();
        Map<String, String> recipeIds = new HashMap<>();
        List<JSONObject> recipes = new ArrayList<>();
        for (Element linkElement : links) {
            String url = linkElement.attr("abs:href");
            if (url.startsWith("https://nl.nytimes.com/f/cooking")) {
                if (recipeIds.containsKey(url)) {
                    out.printf("(Skipping download for duplicate link <%s>\n", url);
                    String id = recipeIds.get(url);
                    linkElement.attr("data-id", id);
                } else {
                    Document downloadedRecepieDoc = session.newRequest(url).get();
                    List<StructuredData> structuredDataList = StructuredDataExtractionUtils.parseStructuredDataPart(downloadedRecepieDoc, StructuredSyntax.JSON_LD);
                    boolean foundRecipe = false;
                    for (StructuredData structuredData : structuredDataList) {
                        JSONObject json = structuredData.getJson();
                        String type = json.getString("@type");
                        if (type.equals("Recipe")) {
                            foundRecipe = true;
                            String id = json.getString("@id");
                            if (id.isEmpty()) {
                                out.printf("* Id for recipe is empty! <%s>\n", url);
                            }
                            linkElement.attr("data-id", id);
                            recipeIds.put(url, id);
                            recipes.add(json);
                        }
                    }
                    if (!foundRecipe) {
                        out.printf("* No recipe in a: <%s>  (%s)\n", url, (linkElement.text()));
                    } else {
                        out.printf("* Found recipe in a: <%s>  (%s)\n", url, (linkElement.text()));
                    }
                }
            }
//            out.printf(" * a: <%s>  (%s)\n", url, (link.text()));
        }
        out.println(doc.outerHtml());
        JSONArray jsonArray = new JSONArray(recipes);
        String jsonArrayText = jsonArray.toString(4);
        out.println(jsonArrayText);
        MimeMessage originalMessage = result.getMimeMessage();

        StructuredMimeMessageWrapper message = new MultipartAlternativeMessageBuilder()
                .subject(originalMessage.getSubject())
//                .textBody(result.getTextBody().getText()) // not getting text, original message only has html body.
                .htmlBody(htmlBody.getText())
//                .htmlBody(htmlBody) // todo: Document limitation: Encoding "quoted-printable" will throw UnsupportedEncodingException
                .from(originalMessage.getFrom())
                .to(originalMessage.getAllRecipients()[0])
//                .to(originalMessage.getAllRecipients()) // todo: Document limitation: Only one recipient
                .structuredData(new JsonLdWrapper(jsonArray))
                .build();

        message.writeTo(out);
//        out.println(recipes.stream().map(JSONObject::toString).collect(Collectors.joining("\n")));
    }
}
