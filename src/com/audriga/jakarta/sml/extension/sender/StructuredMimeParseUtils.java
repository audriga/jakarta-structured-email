package com.audriga.jakarta.sml.extension.sender;

import com.audriga.jakarta.sml.extension.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.extension.model.MimeTextContent;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.ContentType;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StructuredMimeParseUtils {
    protected static final String TEXT = "text";
    protected static final String TEXT_PLAIN = "text/plain";
    protected static final String TEXT_ASCII = "text/ascii";
    protected static final String TEXT_HTML = "text/html";

    public static StructuredMimeMessageWrapper parseMessage(MimeMessage message) throws MessagingException, IOException {
        StructuredMimeMessageWrapper smw = new StructuredMimeMessageWrapper(message);
        MimeTextContent htmlContent = parseBody(message, Collections.singletonList(TEXT_HTML));
        smw.setHtmlBody(htmlContent);
        smw.setTextBody(parseBody(message, Arrays.asList(TEXT, TEXT_PLAIN, TEXT_ASCII)));
        // TODO also add structured data here

        return smw;
    }

    public static Part parsePart(MimeMessage message, List<String> mimeTypes) throws MessagingException, IOException {
        if (mimeTypes.stream().anyMatch(mimeType -> {
            try {
                return message.isMimeType(mimeType);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        })) {
            return message;
        }
        if (message.isMimeType("multipart/*")) {
            return getPartFromMultipart((MimeMultipart) message.getContent(), mimeTypes);
        }
        return null;
    }

    public static MimeTextContent parseBody(MimeMessage message, List<String> mimeTypes) throws MessagingException, IOException {
        if (mimeTypes.stream().anyMatch(mimeType -> {
            try {
                return message.isMimeType(mimeType);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        })) {
            return new MimeTextContent((String) message.getContent(), message.getEncoding());
        }
        if (message.isMimeType("multipart/*")) {
            return getBodyFromMultipart((MimeMultipart) message.getContent(), mimeTypes);
        }
        return null;
    }

    private static MimeTextContent getBodyFromMultipart(MimeMultipart mimeMultipart, List<String> mimeTypes) throws MessagingException, IOException {
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            for (String mimeType : mimeTypes) {
                if (bodyPart.isMimeType(mimeType)) {
                    String contentType = bodyPart.getContentType().toLowerCase();
                    ContentType contentTypeObject = new ContentType(contentType);
                    String charset = contentTypeObject.getParameter("charset");
                    return new MimeTextContent((String) bodyPart.getContent(), charset);
                }
            }
            if (bodyPart.isMimeType("multipart/*")) {
                MimeMultipart nestedMultipart = (MimeMultipart) bodyPart.getContent();
                MimeTextContent body = getBodyFromMultipart(nestedMultipart, mimeTypes);
                if (body != null) {
                    return body;
                }
            }
        }
        return null;
    }

    private static Part getPartFromMultipart(MimeMultipart mimeMultipart, List<String> mimeTypes) throws MessagingException, IOException {
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            for (String mimeType : mimeTypes) {
                if (bodyPart.isMimeType(mimeType)) {
                    return bodyPart;
                }
            }
            if (bodyPart.isMimeType("multipart/*")) {
                MimeMultipart nestedMultipart = (MimeMultipart) bodyPart.getContent();
                Part body = getPartFromMultipart(nestedMultipart, mimeTypes);
                if (body != null) {
                    return body;
                }
            }
        }
        return null;
    }
}
