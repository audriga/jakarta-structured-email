package com.audriga.jakarta.sml.scanner.cli;

import com.audriga.jakarta.sml.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.model.StructuredData;
import com.audriga.jakarta.sml.model.StructuredSyntax;
import com.audriga.jakarta.sml.scanner.cli.FileDumper;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class FileDumperTest {

    @Test(groups = "unit")
    public void testCanDumpUnbelievablyLongFilename() throws MessagingException {
        // Mock the Folder and StructuredMimeMessageWrapper
        Folder mockFolder = mock(Folder.class);
        StructuredMimeMessageWrapper mockMessage = mock(StructuredMimeMessageWrapper.class);
        StructuredData mockStructuredData = mock(StructuredData.class);

        // Set up mock behavior
        when(mockFolder.getFullName()).thenReturn("INBOX._FINALIZE._ROOT");
        when(mockMessage.getReceivedDate()).thenReturn(new java.util.Date());
        when(mockMessage.getMessageID()).thenReturn("<an-unusually-long-message-id-with-some-unique-id-1090820394523098090--09--02-089235235232392349234902834-0234-0234-234>");
        when(mockMessage.getFrom()).thenReturn("an-unusually-long-mail-address-with-a-lot-of-chars@ecological-fanatic.desolate");
        when(mockStructuredData.getBody()).thenReturn("Test Content");
        when(mockStructuredData.getSchemaOrgType()).thenReturn("EmailMessage");
        when(mockStructuredData.getSyntax()).thenReturn(StructuredSyntax.JSON_LD);

        // Define a very long output directory
        String outputDir = "/tmp/file-dumper-test-output";

        // Call the method that triggers the file writing
        FileDumper.dump(mockStructuredData, mockMessage, mockFolder, outputDir);
    }
}
