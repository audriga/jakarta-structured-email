package com.audriga.jakarta.sml.mime;

import com.audriga.jakarta.sml.model.StructuredData;
import jakarta.activation.ActivationDataFlavor;
import jakarta.activation.DataSource;
import org.eclipse.angus.mail.handlers.text_plain;

import java.io.*;

public class StructuredDataContentHandler extends text_plain {
    private static final ActivationDataFlavor[] FLAVORS = {
            new ActivationDataFlavor(StructuredData.class, StructuredData.MIME_TYPE, "Structured Data")
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
        if (!(obj instanceof StructuredData))
            throw new IOException("\"" + getDataFlavors()[0].getMimeType() +
                    "\" DataContentHandler requires JsonStructuredData object, " +
                    "was given object of type " + obj.getClass());
        StructuredData structuredData = (StructuredData) obj;
        super.writeTo(structuredData.getBody(), mimeType, os);
    }
}