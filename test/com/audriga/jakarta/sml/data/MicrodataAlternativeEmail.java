package com.audriga.jakarta.sml.data;

import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;
import jakarta.activation.FileDataSource;

import java.net.URISyntaxException;

public class MicrodataAlternativeEmail extends AbstractEmail {

    public MicrodataAlternativeEmail() {
        super("Text body in Microdata Email!!!", "Microdata Email with builder type alternative", TestUtils.readResource("microdata/simple-product.html"), "alternative", true);
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
