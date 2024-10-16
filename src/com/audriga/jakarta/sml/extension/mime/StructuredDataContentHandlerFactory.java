package com.audriga.jakarta.sml.extension.mime;

import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import jakarta.activation.DataContentHandler;
import jakarta.activation.DataContentHandlerFactory;

public class StructuredDataContentHandlerFactory implements DataContentHandlerFactory {
    @Override
    public DataContentHandler createDataContentHandler(String mimeType) {
        if (StructuredData.MIME_TYPE.equals(mimeType)) {
            return new StructuredDataContentHandler();
        }
        return null;
    }
}