package com.audriga.jakarta.sml.data;

import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;
import jakarta.activation.FileDataSource;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class AttachmentEmail extends AbstractEmail {
    public AttachmentEmail() {
        super(null, "Attachment Email", TestUtils.readResource("html-body/simple-body.html"), "inline", false);
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
        Path atPath = TestUtils.readResourceAsPath("attachment/event-reservation.xml");
        return new FileDataSource(String.valueOf(atPath));
    }

    @Override
    public String getAttachmentName() {
        return "event-reservation.xml";
    }
}
