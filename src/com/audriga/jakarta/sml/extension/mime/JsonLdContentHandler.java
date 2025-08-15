package com.audriga.jakarta.sml.extension.mime;

import com.audriga.jakarta.sml.structureddata.JsonLdWrapper;
import jakarta.activation.ActivationDataFlavor;
import jakarta.activation.DataSource;
import org.eclipse.angus.mail.handlers.text_plain;

import java.io.*;

public class JsonLdContentHandler extends text_plain {
    private static final ActivationDataFlavor[] FLAVORS = {
            new ActivationDataFlavor(JsonLdWrapper.class, JsonLdWrapper.MIME_TYPE, "Structured Data")
    };

    @Override
    public ActivationDataFlavor[] getTransferDataFlavors() {
        return FLAVORS;
    }

    @Override
    public Object getTransferData(ActivationDataFlavor flavor, DataSource dataSource) throws IOException {
        if (FLAVORS[0].equals(flavor)) {
            return getContent(dataSource);
        }
        return null;
    }

    @Override
    public Object getContent(DataSource dataSource) throws IOException {
        try (InputStream is = dataSource.getInputStream();
             ObjectInputStream ois = new ObjectInputStream(is)) {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Class not found while reading JsonStructuredData", e);
        }
    }

    @Override
    public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
        if (obj == null)
            throw new IOException("\"" + getDataFlavors()[0].getMimeType() +
                    "\" DataContentHandler is empty, " +
                    "writing it would result in NullPointerException.");
        if (!(obj instanceof JsonLdWrapper))
            throw new IOException("\"" + getDataFlavors()[0].getMimeType() +
                    "\" DataContentHandler requires JsonLdWrapper object, " +
                    "was given object of type " + obj.getClass());
        JsonLdWrapper jsonLdWrapper = (JsonLdWrapper) obj;
        super.writeTo(jsonLdWrapper.getJsonLdText(), mimeType, os);
    }
}