package com.audriga.jakarta.sml.structureddata;

import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Represents the JsonLd used when creating an SML mail
 */
public class JsonLdWrapper {
    public static final String MIME_TYPE = "application/ld+json";

    /**
     * Can only ever be either a JSONObject or a JSONArray.
     */
    private final Object jsonLd;

    /**
     * The original parsing result may contain additional information
     */
    private final List<StructuredData> structuredDataList;

    public JsonLdWrapper(Object jsonLd) {
        this.structuredDataList = null;
        if (jsonLd instanceof JSONObject || jsonLd instanceof JSONArray) {
            this.jsonLd = jsonLd;
        } else {
            throw new IllegalArgumentException("jsonLd has to be either JSONObject or JSONArray. Was "+ jsonLd.getClass());
        }
    }

    JsonLdWrapper(Object jsonLd, List<StructuredData> structuredDataList) {
        this.structuredDataList = structuredDataList;
        if (jsonLd instanceof JSONObject || jsonLd instanceof JSONArray) {
            this.jsonLd = jsonLd;
        } else {
            throw new IllegalArgumentException("jsonLd has to be either JSONObject or JSONArray. Was "+ jsonLd.getClass());
        }
    }

    public Object getJsonLd() {
        return jsonLd;
    }

    public String getJsonLdText() {
        if (jsonLd instanceof JSONObject) {
            return ((JSONObject) jsonLd).toString(4);
        } else if (jsonLd instanceof  JSONArray) {
            return ((JSONArray) jsonLd).toString(4);
        } else {
            throw new IllegalStateException("jsonLd has to be either JSONObject or JSONArray. Was "+ jsonLd.getClass());
        }
    }
    public List<StructuredData> getStructuredDataList() {
        return structuredDataList;
    }

    /**
     *
     * @return the number of json elements contained
     */
    public int size() {
        if (jsonLd instanceof JSONObject) {
           return 1;
        } else if (jsonLd instanceof  JSONArray) {
            return ((JSONArray) jsonLd).length();
        } else {
            throw new IllegalStateException("jsonLd has to be either JSONObject or JSONArray. Was "+ jsonLd.getClass());
        }
    }
}
