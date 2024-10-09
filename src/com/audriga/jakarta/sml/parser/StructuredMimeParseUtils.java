package com.audriga.jakarta.sml.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.audriga.jakarta.sml.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.model.*;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.ContentType;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StructuredMimeParseUtils {
    private static final Logger mLogger = Logger.getLogger(StructuredMimeParseUtils.class.getName());
    protected static final String TEXT = "text";
    protected static final String TEXT_PLAIN = "text/plain";
    protected static final String TEXT_ASCII = "text/ascii";
    protected static final String TEXT_HTML = "text/html";

    public static StructuredMimeMessageWrapper parseMessage(MimeMessage message) throws MessagingException, IOException {
        StructuredMimeMessageWrapper smw = new StructuredMimeMessageWrapper(message);
        MimeTextContent htmlContent = parseBody(message, Collections.singletonList(TEXT_HTML));
        smw.setHtmlBody(htmlContent);
        smw.setTextBody(parseBody(message, Arrays.asList(TEXT, TEXT_PLAIN, TEXT_ASCII)));

        return smw;
    }

    public static MimeTextContent parseBody(MimeMessage message, List<String> mimeTypes) throws MessagingException, IOException {
        for (String mimeType : mimeTypes) {
            if (message.isMimeType(mimeType)) {
                return new MimeTextContent((String) message.getContent(), message.getEncoding());
            }
        }
        if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getBodyFromMultipart(mimeMultipart, mimeTypes);
        }
        return null;
    }

    private static MimeTextContent getBodyFromMultipart(MimeMultipart mimeMultipart, List<String> mimeTypes) throws MessagingException, IOException {
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            for (String mimeType : mimeTypes) {
                if (bodyPart.isMimeType(mimeType)) {
                    String contentType = bodyPart.getContentType().toLowerCase();
                    ContentType contentTypeObject = new ContentType(contentType);
                    String charset = contentTypeObject.getParameter("charset");
                    return new MimeTextContent((String) bodyPart.getContent(), charset);
                }
            }
            if (bodyPart.isMimeType("multipart/*")) {
                MimeMultipart nestedMultipart = (MimeMultipart) bodyPart.getContent();
                MimeTextContent body = getBodyFromMultipart(nestedMultipart, mimeTypes);
                if (body != null) {
                    return body;
                }
            }
        }
        return null;
    }

    public static List<StructuredData> parseStructuredDataPart(String html, StructuredSyntax syntax) {
        // TODO Filter irrelevant schema.org types (e.g. BreadcrumbList)
        // List<String> mainSchemaOrgTypes = filterIrrelevantSchemaOrgTypes(structuredData);

        List<StructuredData> structuredDataList = new ArrayList<>();
        Document doc = Jsoup.parse(html);

        if (syntax == StructuredSyntax.JSON_LD) {
            Elements scripts = doc.select("script[type=" + StructuredData.MIME_TYPE + "]");
            for (Element script : scripts) {
                // Extract the JSON content from the script tag
                String jsonContent = script.html().trim();
                structuredDataList.addAll(parseStructuredDataFromJsonStr(jsonContent));
            }
        } else if (syntax == StructuredSyntax.MICRODATA) {
            // Looks in all HTML elements for itemscope, filters out nested itemscope elements
            Elements rootItemscope = doc.select("[itemscope]:not([itemscope] [itemscope])");
            for (Element itemscope :  rootItemscope) {
                structuredDataList.add(parseStructuredDataFromDivElement(itemscope));
            }
        }

        return structuredDataList;
    }

public static List<StructuredData> parseStructuredDataFromJsonStr(String jsonContent) {
    if (jsonContent == null || jsonContent.isEmpty()) {
        return null;
    }

    List<StructuredData> structuredDataList = new ArrayList<>();
    StructuredData data;
    // Check if the JSON content is an array or an object
    if (jsonContent.startsWith("[")) {
        JSONArray jsonArray = new JSONArray(jsonContent);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            data = new StructuredData(jsonObject.toString(4), jsonObject);
            structuredDataList.add(data);
        }
    } else {
        JSONObject jsonObject = new JSONObject(jsonContent);
        data = new StructuredData(jsonObject.toString(4), jsonObject);
        structuredDataList.add(data);
    }
    return structuredDataList;
}

    public static StructuredData parseStructuredDataFromDivElement(Element div) {
        JSONObject jsonObject = createNewMicrodataObject(div);

        return new StructuredData(jsonObject.toString(4), jsonObject, StructuredSyntax.MICRODATA);
    }

    private static JSONObject createNewMicrodataObject(Element div) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("@type", div.attr("itemtype"));

        // Selects properites with itemscope, excludes the root element
        Elements propertiesNestedValue = div.select("[itemscope]:not(:root)");

        // Selects non-nested properites with itemprop
        Elements propertiesSimpleValue = div.select(":root > [itemprop]:not([itemscope])");
        for (Element property : propertiesSimpleValue) {
            String propName = property.attr("itemprop");
            switch (property.tagName()) {
                case "div":
                case "h4":
                case "span":
                    jsonObject.put(propName, property.text());
                    break;
                case "meta":
                    jsonObject.put(propName, property.attr("content"));
                    break;
                case "link":
                    jsonObject.put(propName, property.attr("href"));
                    break;
                case "time":
                    jsonObject.put(propName, property.attr("datetime"));
                    break;
                default:
                    mLogger.warning("Unknown tag name. Not all microdata properties might have been mapped. Tag name was: " + property.tagName());
                    jsonObject.put(propName, property.text());
                    break;
            }
        }

        for (Element property : propertiesNestedValue) {
            String propName = property.attr("itemprop");
            jsonObject.put(propName, createNewMicrodataObject(property));
        }

        return jsonObject;
    }

    public static List<StructuredData> parseStructuredData(String html) {
        List<StructuredData> structuredData = new ArrayList<>();
        if (html != null) {
            if (html.contains("application/ld") || (html.contains("itemscope") && html.contains("itemprop"))) {
                if (html.contains("application/ld")) {
                    structuredData.addAll(parseStructuredDataPart(html, StructuredSyntax.JSON_LD));
                }
                if (html.contains("itemscope") && html.contains("itemprop")) {
                    structuredData.addAll(parseStructuredDataPart(html, StructuredSyntax.MICRODATA));
                }

                return structuredData;
            }
        }
        return null;
    }

    /**
     * Parses structured data from the HTML body of a structured MIME message.
     * @param smw Structured MIME message wrapper.
     * @return List of structured data objects if structured data was indicated. Empty list on error.
     */
    public static List<StructuredData> parseStructuredDataFromHtml(StructuredMimeMessageWrapper smw) {
        if (smw.getHtmlBody() != null) {
            try {
                return parseStructuredData(smw.getHtmlBody().getText());
            } catch (RuntimeException e) {
                mLogger.warning(String.format(
                        "Error processing structured Data: %s - Message: \"%s\"",
                        smw.getMessageNumber(),
                        e.getMessage()));
                mLogger.fine(Arrays.toString(e.getStackTrace()));
                return new ArrayList<>();
            }
        }
        return null;
    }
}