package com.audriga.jakarta.sml.data;

import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import com.audriga.jakarta.sml.h2lj.parser.StructuredDataExtractionUtils;
import com.audriga.jakarta.sml.TestUtils;
import jakarta.activation.FileDataSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

public class SimpleEmail {
    static String textBody = "Event Reservation Confirmation\n\n" +
            "Dear Noah Baumbach,\n\n" +
            "Thank you for your reservation. Here are the details:\n\n" +
            "Reservation Number: MBE12345\n" +
            "Event Name: Make Better Email 2024\n" +
            "Start Date: 2024-10-30\n" +
            "Location:\n" +
            "    Isode Ltd\n" +
            "    14 Castle Mews\n" +
            "    Hampton TW12 2NP\n" +
            "    UK\n\n" +
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
        return StructuredDataExtractionUtils.parseStructuredDataFromJsonStr(json);
    }

    public static List<StructuredData> getJsonArray() {
        String json = TestUtils.readResource("jsonld/promotion-card.json");
        return StructuredDataExtractionUtils.parseStructuredDataFromJsonStr(json);
    }

    public static FileDataSource getAttachment() throws URISyntaxException {
        Path atPath = TestUtils.readResourceAsPath("attachment/event-reservation.xml");
        return new FileDataSource(String.valueOf(atPath)); // TODO valueOf might not be necessary?
    }

    public static String getAttachmentName() {
        return "event-reservation.xml";
    }
}