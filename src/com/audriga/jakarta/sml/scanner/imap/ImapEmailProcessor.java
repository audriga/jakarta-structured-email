package com.audriga.jakarta.sml.scanner.imap;

import com.audriga.jakarta.sml.mime.*;
import com.audriga.jakarta.sml.model.StructuredData;
import com.audriga.jakarta.sml.parser.StructuredMimeParseUtils;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class ImapEmailProcessor implements AutoCloseable {
    private static final Logger mLogger = Logger.getLogger(ImapEmailProcessor.class.getName());
    private static final int SENDER_MAX_LENGTH = 60;
    private final Store imapStore;
    private List<Folder> folders;
    private String host;
    private int port;
    private String user;
    private String password;

    public ImapEmailProcessor(Store imapStore) throws MessagingException {
        this.imapStore = imapStore;
        setFolders(null);
    }

    public ImapEmailProcessor(String host, int port, String user, String password, boolean useSsl, List<String> imapDirs) throws MessagingException {
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        Properties props = new Properties();
        props.setProperty("mail.imap.ssl.enable", String.valueOf(useSsl));
        Session session = Session.getInstance(props);
        imapStore = session.getStore("imap");
        imapStore.connect(host, port, user, password);
        System.out.println("Successfully connected.");
        setFolders(imapDirs);
    }

    @Override
    public void close() throws MessagingException {
        if (imapStore != null && imapStore.isConnected()) {
            imapStore.close();
        }
    }

    public List<StructuredMimeMessageWrapper> scanForSchema(Folder folder) {
        List<StructuredMimeMessageWrapper> res = new ArrayList<>();
        try {
            openFolderWithRetry(folder);
            Message[] messages = folder.getMessages();
            String folderLog = "[" + folder.getFullName() + "]";

            System.out.println(folderLog + " Scanning " + messages.length + " messages...");
            mLogger.fine(folderLog + " Count " + messages.length);
            List<Message> toFetch = new ArrayList<>();

            int maxFetch = 500;

            // Fetch
            for (Message m : messages) {
                // Fetch
                if (toFetch.size() > (maxFetch-1)) {
                    mLogger.fine(folderLog + " Fetch " + toFetch.size());
                    fetch(toFetch, folder);
                }
                toFetch.add(m);
            }
            if (!toFetch.isEmpty()) {
                mLogger.fine(folderLog + " Fetch " + toFetch.size());
                fetch(toFetch, folder);
            }

            mLogger.fine(folderLog + " Iterate " + messages.length );
            for (Message m : messages) {
                try {
                    MimeMessage mm = (MimeMessage)m;

                    StructuredMimeMessageWrapper ma = StructuredMimeParseUtils.parseMessage(mm);
                        ma.setStructuredData(StructuredMimeParseUtils.parseStructuredDataFromHtml(ma));
                    List<StructuredData> structuredData = ma.getStructuredData();
                    if (structuredData != null) {
                        // Determine from
                        String from = "(from)";
                        if (mm.getFrom() != null && mm.getFrom().length > 0) from = "" + mm.getFrom()[0];

                        res.add(ma);

                        // Log it!
                        String logLine = formatLogLine(mm, from, structuredData);
                        if (!structuredData.isEmpty()) {
                            System.out.println(logLine);
                        } else {
                            // null && empty means we may have missed structured data
                            mLogger.warning("The following message indicates usage of structured data, but no structured data was found: " + logLine);
                        }
                    }
                } catch (IOException|MessagingException|RuntimeException e) {
                    mLogger.warning(String.format(
                            "Error processing message: %s \"%s\" - Message: \"%s\"",
                            m.getMessageNumber(),
                            m.getSubject(),
                            e.getMessage()));
                    mLogger.fine(Arrays.toString(e.getStackTrace()));
                }
            }
            folder.close(false);
        } catch (MessagingException|RuntimeException e) {
            mLogger.warning("Error processing folder " + folder.getFullName() + ": " + e.getMessage());
            mLogger.fine(Arrays.toString(e.getStackTrace()));
        }
        return res;
    }

    public Map<Folder, List<StructuredMimeMessageWrapper>> scanForSchema() {
        Map<Folder, List<StructuredMimeMessageWrapper>> folderMap = new HashMap<>();

        for (Folder f : folders) {
            folderMap.put(f, scanForSchema(f));
            // newline
            System.out.println();
        }
        return folderMap;
    }


    /* Determine the total amount of messages in all folders of the account */
    public int getTotalMessageCount() {
        int totalCount = 0;
        try {
            for (Folder folder : folders) {
                folder.open(Folder.READ_ONLY);
                totalCount += folder.getMessageCount();
                folder.close(false);
            }
        } catch (MessagingException e) {
            mLogger.warning(e.getMessage());
        }
        return totalCount;
    }

    public void setFolders(List<String> imapDirs) throws MessagingException {
        if (folders == null) {
            folders = new ArrayList<>();
        }
        if (imapDirs != null && !imapDirs.isEmpty()) {
            for (String dir : imapDirs) {
                Folder folder = imapStore.getDefaultFolder().getFolder(dir);
                folders.add(folder);
            }
        } else {
            folders = Arrays.asList(imapStore.getDefaultFolder().list("*"));
        }
    }

    public List<Folder> getFolders() {
        return folders;
    }

    private List<String> filterIrrelevantSchemaOrgTypes(String html) {
        return null;
    }

    private static void fetch(List<Message> toFetch, Folder f) throws MessagingException {
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.CONTENT_INFO);
        //inbox.fetch(messages,  fp);
        fetch(toFetch, f, fp);
    }

    private static void fetch(List<Message> toFetch, Folder f, FetchProfile fp) throws MessagingException {
        Message[] msgs = toFetch.toArray(new Message[0]);
        f.fetch(msgs, fp);
        toFetch.clear();
    }

    private String formatLogLine(MimeMessage mm, String from, List<StructuredData> structuredData) throws MessagingException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm");
        String dateStr = dateFormat.format(mm.getReceivedDate());

        String messageId = mm.getMessageID();

        if (from.length() > SENDER_MAX_LENGTH) {
            from = from.substring(0, SENDER_MAX_LENGTH - 3) + "...";
        } else {
            from = String.format("%-" + SENDER_MAX_LENGTH + "s", from);
        }

        int dataCount = 0;
        if (structuredData != null) {
            dataCount = structuredData.size();
        }

        return String.format("%s | %s | data objects: %d | %s", dateStr, from, dataCount, messageId);
    }

    public void openFolderWithRetry(Folder folder) throws MessagingException {
        final long BACKOFF = 1000; // 1 second

            try {
                folder.open(Folder.READ_ONLY);
            } catch (MessagingException e) {
                reconnectImapStore(); // Reconnect the store TODO unclear why this should be necessary?
                try {
                    Thread.sleep(BACKOFF); // Wait before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new MessagingException("Interrupted during backoff", ie);
                }
                try {
                    folder.open(Folder.READ_ONLY);
                } catch (MessagingException ex2) {
                    throw e;
                }
            }
    }

    private void reconnectImapStore() throws MessagingException {
        close();

        if (host == null || user == null || password == null) {
            throw new MessagingException("IMAP connection parameters not set.");
        }
        imapStore.connect(host, port, user, password);
        mLogger.warning("Reconnected to IMAP store.");
    }
}