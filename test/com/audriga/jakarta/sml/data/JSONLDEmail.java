package com.audriga.jakarta.sml.data;

import com.audriga.jakarta.sml.TestUtils;
import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import com.audriga.jakarta.sml.h2lj.parser.StructuredDataExtractionUtils;
import com.audriga.jakarta.sml.structureddata.JsonLdUtils;
import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;
import jakarta.activation.FileDataSource;

import java.net.URISyntaxException;
import java.util.List;

public class JSONLDEmail extends AbstractEmail {
    public JSONLDEmail() {
        super(null, "JSONLD Email", TestUtils.readResource("html-body/simple-body.html"), "html", false);
    }

    @Override
    public JsonLdWrapper getJson() {
        String json = TestUtils.readResource("jsonld/event-reservation-better.json");
        List<StructuredData> structuredDataList = StructuredDataExtractionUtils.parseStructuredDataFromJsonStr(json);
        return JsonLdUtils.convertStructuredData(structuredDataList);
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
