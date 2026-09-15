package com.audriga.jakarta.sml.data;

import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;
import jakarta.activation.FileDataSource;

import java.net.URISyntaxException;

public class GlobalDataAttributeEmail extends AbstractEmail {
    public GlobalDataAttributeEmail() {
        super("I am a text body!", "Global Attributes Data Email", TestUtils.readResource("html-body/global-attributes-data.html") , "html", false);
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
        return "";
    }
}
