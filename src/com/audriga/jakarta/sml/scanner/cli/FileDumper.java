package com.audriga.jakarta.sml.scanner.cli;

import com.audriga.jakarta.sml.extension.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import com.audriga.jakarta.sml.h2lj.model.StructuredSyntax;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.Logger;

public class FileDumper {
    private static final Logger mLogger = Logger.getLogger(FileDumper.class.getName());

    /* Dumps the whole HTML body of a message to a file. */
    public static void dump(StructuredMimeMessageWrapper mm, Folder f, String outputDir) {
        if (mm.getHtmlBody() == null) {
            return;
        }

        String body = mm.getHtmlBody().getText();

        String fileType = "html";
        String syntax = "unknown";  // TODO determine syntax anyway
        String schemaOrgType = "unknown";

        dump(f, mm, outputDir, fileType, body, syntax, schemaOrgType);
    }

    /* Dumps the JSON or HTML body of a structured data object to a file. */
    public static void dump(StructuredData structuredDataPart, StructuredMimeMessageWrapper mm, Folder f, String outputDir) {
        String syntax;

        if (structuredDataPart.getSyntax() == StructuredSyntax.JSON_LD) {
            syntax = "jsonld";
        } else if (structuredDataPart.getSyntax() == StructuredSyntax.MICRODATA ) {
            syntax = "microdata";
        } else {
            throw new IllegalArgumentException("Unknown structured data type: " + structuredDataPart.getClass().getName());
        }

        String body = structuredDataPart.getBody();

        String fileType = "json";

        String schemaOrgType = "unknown";
        if (structuredDataPart.getSchemaOrgType() != null) {
            schemaOrgType = structuredDataPart.getSchemaOrgType().toLowerCase();
        }

        dump(f, mm, outputDir, fileType, body, syntax, schemaOrgType);
    }

    private static void dump(Folder f, StructuredMimeMessageWrapper mm, String outputDir, String fileType, String body, String syntax, String schemaOrgType) {
        // Create filename
        final int MAX_FILENAME_LENGTH = 255;

        String messageId;
        try {
            messageId = mm.getMessageID().replaceAll("[^a-zA-Z0-9]", "_");
        } catch (MessagingException|RuntimeException me) {
            mLogger.warning("Could not get message ID for message.");
            mLogger.fine(Arrays.toString(me.getStackTrace()));
            messageId = "unknown";
        }

        String dateStr;
        try {
            dateStr = new SimpleDateFormat("yyyy_MM_dd").format(mm.getReceivedDate());
        } catch (MessagingException e) {
            mLogger.warning("Could not get received date for message: " + messageId);
            mLogger.fine(Arrays.toString(e.getStackTrace()));
            dateStr = "unknown";
        }

        if (messageId.length() > 2) {
            messageId = messageId.substring(1, messageId.length() - 1);
        }
        String sender = "unknown";
        if (mm.getFrom() != null) {
            sender = mm.getFrom().replaceAll("[^a-zA-Z0-9]", "_");
        }

        String folderName = f.getFullName().replaceAll("[^a-zA-Z0-9]", "_");

        String filename = String.format("%s-%s-%s-%s-%s.%s.%s", schemaOrgType, dateStr, folderName, sender, messageId, syntax, fileType);

        if (filename.length() > MAX_FILENAME_LENGTH) {
            sender = sender.substring(0, sender.length() - (filename.length() - MAX_FILENAME_LENGTH));
            filename = String.format("%s-%s-%s-%s-%s.%s.%s", dateStr, f.getFullName(), sender, messageId, schemaOrgType, syntax, fileType);
        }

        // Write to file
        try {
            Path filePath = Paths.get(outputDir, filename);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, body.getBytes(StandardCharsets.UTF_8));
        } catch (IOException|RuntimeException e) {
            mLogger.warning("Could not write file: " + e.getMessage());
            mLogger.fine(Arrays.toString(e.getStackTrace()));
        }
    }
}