package com.audriga.jakarta.sml.structureddata;

import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import java.util.logging.Logger;

public class JsonLdUtils {
//    private static final Logger mLogger = Logger.getLogger(JsonLdUtils.class.getName());

    /**
     * Converts between the parsing output interface and the mail creating input interface
     * @param structuredData structured data as returned by H2LJ parsing
     * @return jsonld as expected by message builders.
     */
    public static JsonLdWrapper convertStructuredData(List<StructuredData> structuredData) {
        if (structuredData == null || structuredData.isEmpty()) {
            return null;
        }
        if (structuredData.size() == 1 ) {
            return convertStructuredData(structuredData.getFirst());
        }
        List<JSONObject> jsonObjects = new ArrayList<>(structuredData.size());
        for (StructuredData structuredDataElement : structuredData) {
            jsonObjects.add(structuredDataElement.getJson());
        }
        JSONArray jsonArray = new JSONArray(jsonObjects);
        return new JsonLdWrapper(jsonArray, structuredData);
    }

    public static JsonLdWrapper convertStructuredData(StructuredData structuredData) {
        return new JsonLdWrapper(structuredData.getJson(), Collections.singletonList(structuredData));
    }
}
