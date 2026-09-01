package com.audriga.jakarta.sml.h2lj.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import com.audriga.jakarta.sml.h2lj.model.StructuredSyntax;
import com.audriga.jakarta.sml.extension.mime.StructuredMimeMessageWrapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StructuredDataExtractionUtils {
    private static final Logger mLogger = Logger.getLogger(StructuredDataExtractionUtils.class.getName());

    public static List<StructuredData> parseStructuredDataPart(String html, StructuredSyntax syntax) {
        // TODO Filter irrelevant schema.org types (e.g. BreadcrumbList)
        // List<String> mainSchemaOrgTypes = filterIrrelevantSchemaOrgTypes(structuredData);
        Document doc = Jsoup.parse(html);

        return parseStructuredDataPart(doc, syntax);
    }

    public static List<StructuredData> parseStructuredDataPart(Document doc, StructuredSyntax syntax) {
        List<StructuredData> structuredDataList = new ArrayList<>();

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
        data = new StructuredData(jsonContent);
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