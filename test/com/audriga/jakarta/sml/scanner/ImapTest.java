package com.audriga.jakarta.sml.scanner;

import com.audriga.jakarta.sml.scanner.imap.ImapEmailProcessor;
import com.audriga.jakarta.sml.extension.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.TestUtils;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ImapTest {

    @Mock
    private Store mockStore;

    @Mock
    private Folder mockFolder;

    @Mock
    private MimeMessage mockMessage;

    private ImapEmailProcessor processor;

    @BeforeClass (groups = "unit")
    public void setUp() throws MessagingException {
        TestUtils.initLogging();

        MockitoAnnotations.openMocks(this);

        // Mock the behavior of the Store
        when(mockStore.getDefaultFolder()).thenReturn(mockFolder);
        when(mockFolder.list("*")).thenReturn(new Folder[]{mockFolder});

        // Initialize the processor with the mocked Store
        processor = new ImapEmailProcessor(mockStore);

        assertNotNull(processor);
    }

    @AfterClass(groups = "unit")
    public void tearDown() throws MessagingException {
        processor.close();
    }

    @Test(groups = "unit")
    public void testCheckSchema() throws Exception {
        // Mock the behavior of the Folder
        when(mockFolder.getMessages()).thenReturn(new Message[]{mockMessage});
        when(mockFolder.getFullName()).thenReturn("INBOX");
        when(mockMessage.getFrom()).thenReturn(null);
        when(mockMessage.getReceivedDate()).thenReturn(null);
        when(mockMessage.getSubject()).thenReturn("Test Subject");
        when(mockMessage.getContentType()).thenReturn("text/html; charset=utf-8");
        when(mockMessage.getContent()).thenReturn("Test Content");
        when(mockMessage.isMimeType("text/html")).thenReturn(true);

        Map<Folder, List<StructuredMimeMessageWrapper>> res = processor.scanForSchema();
        assertFalse(res.isEmpty());
    }

    @Test(groups = "unit", dependsOnMethods = "testCheckSchema")
    public void testCheckSchemaBrokenJson() throws Exception {
        when(mockMessage.getContent()).thenReturn(TestUtils.readResource("html-body/jsonld-inline-body-broken.html"));

        processor.scanForSchema();
    }
}