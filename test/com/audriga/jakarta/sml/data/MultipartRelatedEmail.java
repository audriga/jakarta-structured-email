package com.audriga.jakarta.sml.data;

import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import com.audriga.jakarta.sml.h2lj.parser.StructuredDataExtractionUtils;
import com.audriga.jakarta.sml.TestUtils;

import java.util.List;

public class MultipartRelatedEmail {
    static String textBody = "I booked my spot at Better Email 2024. My right, right-hand seat is free, I wish for you to sit next to me. What do you think?";

    public static String getSubject() {
        return "Make Email Better Again!";
    }

    public static String getHtmlBody() {
        return TestUtils.readResource("html-body/related-body.html");
    }

    public static String getTextBody() {
        return textBody;
    }

    public static List<StructuredData> getJson() {
        String json = TestUtils.readResource("jsonld/event-reservation-better.json");
        return StructuredDataExtractionUtils.parseStructuredDataFromJsonStr(json);
    }
}
