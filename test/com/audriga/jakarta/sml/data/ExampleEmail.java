package com.audriga.jakarta.sml.data;

import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;
import jakarta.activation.FileDataSource;

import java.net.URISyntaxException;

public class ExampleEmail extends AbstractEmail {

    public ExampleEmail() {
        super(
                "Event Reservation Confirmation\n\n" +
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
                "The Event Team",
                "Make Email Better Again!",

                TestUtils.readResource("html-body/simple-body.html"),

                "inline",

                false
                );
    }


    @Override
    public JsonLdWrapper getJson() {
        return null;
    }

    @Override
    public JsonLdWrapper getJsonArray() {
        return null;
    }

    @Override
    public FileDataSource getAttachment() throws URISyntaxException {
        return null;
    }

    @Override
    public String getAttachmentName() {
        return null;
    }
}
