package com.audriga.jakarta.sml.data;

import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;
import jakarta.activation.FileDataSource;

import java.net.URISyntaxException;

public class RdfAEmail extends AbstractEmail {

    public RdfAEmail() {
        super("I am a text body!", "RDFa Mail", TestUtils.readResource("rdfa/simple-example.html") , "inline", false);
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
