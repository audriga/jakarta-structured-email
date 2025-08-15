package com.audriga.jakarta.sml.data;

import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import com.audriga.jakarta.sml.h2lj.parser.StructuredDataExtractionUtils;
import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.structureddata.JsonLdUtils;
import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;

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

    public static JsonLdWrapper getJson() {
        String json = TestUtils.readResource("jsonld/event-reservation-better.json");
        List<StructuredData> structuredDataList = StructuredDataExtractionUtils.parseStructuredDataFromJsonStr(json);
        return JsonLdUtils.convertStructuredData(structuredDataList);
    }
}
