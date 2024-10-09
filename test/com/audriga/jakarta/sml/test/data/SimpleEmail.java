package com.audriga.jakarta.sml.test.data;

import com.audriga.jakarta.sml.model.StructuredData;
import com.audriga.jakarta.sml.parser.StructuredMimeParseUtils;
import com.audriga.jakarta.sml.test.TestUtils;

import java.util.List;

public class SimpleEmail {
    static String textBody = "Event Reservation Confirmation\n\n" +
            "Dear Noah Baumbach,\n\n" +
            "Thank you for your reservation. Here are the details:\n\n" +
            "Reservation Number: IO12345\n" +
            "Event Name: Make Better Email 2024\n" +
            "Start Date: 2024-10-30\n" +
            "Location:\n" +
            "    Moscone Center\n" +
            "    800 Howard St.\n" +
            "    London, London W1K 1BE\n" +
            "    GB\n\n" +
            "We look forward to seeing you at the event!\n\n" +
            "Best regards,\n" +
            "The Event Team";

    public static String getSubject() {
        return "Make Email Better Again!";
    }

    public static String getHtmlBody() {
        return TestUtils.readResource("html-body/simple-body.html");
    }

    public static String getTextBody() {
        return textBody;
    }

    public static List<StructuredData> getJson() {
        String json = TestUtils.readResource("jsonld/event-reservation-better.json");
        return StructuredMimeParseUtils.parseStructuredDataFromJsonStr(json);
    }

    public static List<StructuredData> getJsonArray() {
        String json = TestUtils.readResource("jsonld/promotion-card.json");
        return StructuredMimeParseUtils.parseStructuredDataFromJsonStr(json);
    }
}