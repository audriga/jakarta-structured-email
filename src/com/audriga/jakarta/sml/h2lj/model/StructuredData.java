package com.audriga.jakarta.sml.h2lj.model;

import org.json.JSONObject;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.logging.Logger;

public class StructuredData {
    public static final String MIME_TYPE = "application/ld+json";

    @Nullable
    private String rdfContext;

    @Nullable
    private String rdfType;

    @NonNull
    private String body;

    @NonNull
    private StructuredSyntax syntax;

    @NonNull
    private JSONObject json;

    private static final Logger mLogger = Logger.getLogger(StructuredData.class.getName());

    public StructuredData(String body) {
        this(body, new JSONObject(body));
    }

    /* Assumes JSON-LD syntax by default. */
    public StructuredData(String body, JSONObject json) {
        this(body, json, StructuredSyntax.JSON_LD);
    }

    public StructuredData(@NonNull String body, @NonNull JSONObject json, @NonNull StructuredSyntax syntax) {
        this.body = body;
        this.syntax = syntax;
        this.json = json;
        if (json.has("@type")) {
            try {
                setTypeAndContext(json.getString("@type"));
            } catch (IllegalArgumentException e) {
                mLogger.warning(e.getMessage());
            }
        } else {
            mLogger.warning("JSON-LD object does not have a @type field.");
        }
    }

    public @NonNull String getBody() {
        return body;
    }

    public String getSchemaOrgType() {
        return rdfType;
    }

    public void setSchemaOrgType(String schemaOrgType) {
        if (containsRdfContext(rdfType) && !schemaOrgType.startsWith(String.valueOf(StructuredContext.SCHEMA_ORG))) {
            throw new IllegalArgumentException("Cannot set schema.org type for non-schema.org type: " + schemaOrgType);
        }
        setTypeAndContext(schemaOrgType);
    }

    public @Nullable String getRdfType() {
        return rdfType;
    }

    public void setRdfType(String rdfType) {
        setTypeAndContext(rdfType);
    }

    public @Nullable String getRdfContext() {
        return rdfContext;
    }

    public void setRdfContext(@Nullable String rdfContext) {
        this.rdfContext = rdfContext;
    }


    public void setBody(@NonNull String html) {
        body = html;
    }

    public void setJson(@NonNull JSONObject json){
        this.json = json;
    }

    public @NonNull JSONObject getJson(){
        return json;
    }

    public @NonNull StructuredSyntax getSyntax() {
        return syntax;
    }

    public void setSyntax(@NonNull StructuredSyntax syntax) {
        this.syntax = syntax;
    }

    protected boolean containsRdfContext(String rdfType) {
        return rdfType != null && rdfType.contains("/");
    }

    protected void setTypeAndContext(String rdfType) {
        if (containsRdfContext(rdfType)) {
            int lastSlashIndex = rdfType.lastIndexOf('/');
            this.rdfContext = rdfType.substring(0, lastSlashIndex);
            this.rdfType = rdfType.substring(lastSlashIndex + 1);
        } else {
            this.rdfContext = null;
            this.rdfType = rdfType;
        }
    }
}
