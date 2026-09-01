package com.audriga.jakarta.sml.extension.mime;

import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;
import jakarta.activation.DataContentHandler;
import jakarta.activation.DataContentHandlerFactory;

public class StructuredDataContentHandlerFactory implements DataContentHandlerFactory {
    @Override
    public DataContentHandler createDataContentHandler(String mimeType) {
        if (JsonLdWrapper.MIME_TYPE.equals(mimeType)) {
            return new JsonLdContentHandler();
        }
        return null;
    }
}