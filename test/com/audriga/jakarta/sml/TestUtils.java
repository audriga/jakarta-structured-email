package com.audriga.jakarta.sml;

import com.audriga.jakarta.sml.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.parser.StructuredMimeParseUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TestUtils {

    /*
    The garbage collector might collect all changes done here.
    Solution is to assign each logger to a static variable
     */
    private static Logger rootLogger;
    private static final Level logLevel = Level.INFO;

    /* Format and set com.audriga as well as okhttp3 in a homogeneous way (okhttp3 is very usefult for debugging with FINE) */
    public static void initLogging() {
        // Remove all handlers that have been previously set for root and com.audriga
        rootLogger = Logger.getLogger("");

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(logLevel);

        rootLogger.addHandler(ch);
        rootLogger.setLevel(logLevel);
        //rootLogger.setUseParentHandlers(false);
    }

    /* Read a resource file and return its content as a string */
    public static String readResource(String s) {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(s);

        assert inputStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    public static Path readResourceAsPath(String s) throws URISyntaxException {
        URL res = TestUtils.class.getClassLoader().getResource(s);

        if (res == null) {
            throw new RuntimeException(String.format("File '%s' not found", s));
        }

        return Paths.get(res.toURI());
    }

    public static StructuredMimeMessageWrapper parseEmlFile(String filename) throws MessagingException, IOException {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filename);

        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage msg = new MimeMessage(session, inputStream);
        StructuredMimeMessageWrapper smw = StructuredMimeParseUtils.parseMessage(msg);
        smw.setStructuredData(StructuredMimeParseUtils.parseStructuredDataFromHtml(smw));
        return smw;
    }
}
