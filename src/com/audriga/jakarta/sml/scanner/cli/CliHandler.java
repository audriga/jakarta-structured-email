package com.audriga.jakarta.sml.scanner.cli;

import com.audriga.jakarta.sml.h2lj.model.StructuredSyntax;
import com.audriga.jakarta.sml.scanner.imap.ImapEmailProcessor;
import com.audriga.jakarta.sml.scanner.logging.SimpleCliFormatter;
import com.audriga.jakarta.sml.extension.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.h2lj.model.StructuredData;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import org.apache.commons.cli.*;

import java.io.Console;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CliHandler {

    private static void initLogging(boolean verbose, boolean maxVerbose) {
        // Create a handler for stdout
        ConsoleHandler newHandler = new ConsoleHandler();
        newHandler.setFormatter(new SimpleCliFormatter());

        // Attach handlers to the logger

        Logger rootLogger = Logger.getLogger("");
        Handler[] hdls =  rootLogger.getHandlers();
        for (Handler hdl : hdls) {
             rootLogger.removeHandler(hdl);
        }

        Logger audrigaLogger = Logger.getLogger("com.audriga");
        if (maxVerbose){
            audrigaLogger.setLevel(Level.ALL);
            newHandler.setLevel(Level.ALL);
        } else if (verbose) {
            audrigaLogger.setLevel(Level.INFO);
            newHandler.setLevel(Level.INFO);
        } else {
            audrigaLogger.setLevel(Level.WARNING);
            newHandler.setLevel(Level.WARNING);
        }
        audrigaLogger.addHandler(newHandler);
    }

    public static void main(String[] args) {
        // Define the options
        Options options = getOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("sml-account-scan", options);

            System.exit(1);
            return;
        }

        initLogging(cmd.hasOption("verbose"), cmd.hasOption("verbose-verbose-verbose"));

        String imapHost = cmd.getOptionValue("host");
        String userEmail = cmd.getOptionValue("user");
        boolean useSsl = !cmd.hasOption("force-no-ssl");
        int imapPort = -1;

        String outputDir = null;
        if (cmd.hasOption("output-dir")) {
            outputDir = cmd.getOptionValue("output-dir");
        }

        List<String> imapDirs = null;
        if (cmd.hasOption("imap-folders")) {
            imapDirs = Arrays.asList(cmd.getOptionValue("imap-folders").split(","));
        }

        try {
            if (cmd.hasOption("override-port")) {
                imapPort = Integer.parseInt(cmd.getOptionValue("override-port"));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number");
            System.exit(1);
            return;
        }

        // Read password
        String userPassword;
        if (cmd.hasOption("password")) {
            userPassword = cmd.getOptionValue("password");
        } else {
            // Prompt for password
            Console console = System.console();
            if (console == null) {
                System.err.println("No console available");
                System.exit(1);
            }
            char[] passwordArray = console.readPassword("Enter your IMAP password: ");
            userPassword = new String(passwordArray);
        }

        // Initialize and use ImapEmailProcessor
        try (ImapEmailProcessor processor = new ImapEmailProcessor(imapHost, imapPort, userEmail, userPassword, useSsl, imapDirs)){
            int totalMessages = processor.getTotalMessageCount();

            System.out.println("Total messages to scan: " + totalMessages);
            if (totalMessages > 9000) {
                System.out.println("Buckle up! This might take a while...");
            }

            List<Folder> folders = processor.getFolders();

            // Perform operations with processor
            Instant start = Instant.now();
            System.out.println("================================");
            Map<Folder, List<StructuredMimeMessageWrapper>> res = new HashMap<>();
            for (Folder f: folders) {
                List<StructuredMimeMessageWrapper> messages = processor.scanForSchema(f);
                res.put(f, messages);

                for (StructuredMimeMessageWrapper msg : messages) {
                    if (outputDir != null) {
                        if (msg.getStructuredData() == null) {
                            FileDumper.dump(msg, f, outputDir);
                        } else {
                            for (StructuredData structuredDataPart : msg.getStructuredData().getStructuredDataList()) {
                                FileDumper.dump(structuredDataPart, msg, f, outputDir);
                            }
                        }
                    }
                }
            }
            Duration runtime = Duration.between(start, Instant.now());
            System.out.println("================================");
            printStats(res, runtime, totalMessages);
        } catch (MessagingException|RuntimeException me) {
            System.err.println("Aborting scan. Fatal error encountered: " + me.getMessage());
            System.err.println(Arrays.toString(me.getStackTrace()));
        }
    }

    private static Options getOptions() {
        Options options = new Options();

        Option hostOption = new Option("h", "host", true,
                "remote IMAP system host");
        hostOption.setRequired(true);
        options.addOption(hostOption);

        Option portOption = new Option("o", "override-port", true,
                "override default remote system port");
        portOption.setRequired(false);
        options.addOption(portOption);

        Option userOption = new Option("u", "user", true, "IMAP user");
        userOption.setRequired(true);
        options.addOption(userOption);

        Option passwordOption = new Option("p", "password", true, "IMAP user password");
        passwordOption.setRequired(false);
        options.addOption(passwordOption);

        Option useSslOption = new Option("f", "force-no-ssl", false,
                "Disable SSL for connection");
        useSslOption.setRequired(false);
        options.addOption(useSslOption);

        Option dirOption = new Option("d", "output-dir", true,
                "output found structured data to this directory");
        dirOption.setRequired(false);
        options.addOption(dirOption);

        Option verboseOption = new Option("v", "verbose", false,
                "Verbose output");
        verboseOption.setRequired(false);
        options.addOption(verboseOption);

        Option verboseVVOption = new Option("vvv", "verbose-verbose-verbose", false,
                "As verbose as it gets");
        verboseVVOption.setRequired(false);
        options.addOption(verboseVVOption);

        Option onlyImapFolderOption = new Option("i", "imap-folders", true,
                "Comma-separated list of IMAP folders to scan. Sample input: 'INBOX,lists.sml' ");
        onlyImapFolderOption.setRequired(false);
        options.addOption(onlyImapFolderOption);

        return options;
    }

    private static void printStats(Map<Folder, List<StructuredMimeMessageWrapper>> res, Duration runtime, int totalMessages) {
        long jsonLdMailCount = res.values().stream()
                .flatMap(List::stream)
                .flatMap(msg -> msg.getStructuredData().getStructuredDataList().stream())
                .filter(data -> data.getSyntax() == StructuredSyntax.JSON_LD)
                .count();

        long microdataMailCount = res.values().stream()
                .flatMap(List::stream)
                .flatMap(msg -> msg.getStructuredData().getStructuredDataList().stream())
                .filter(data -> data.getSyntax() == StructuredSyntax.MICRODATA)
                .count();

        Map<String, Long> messagesPerSender = res.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(msg -> {
                    try {
                        String sender = msg.getSender().toString();
                        return sender.replaceAll(".*<|>.*", "");
                    } catch (jakarta.mail.MessagingException e) {
                        return "unknown";
                    }
                }, Collectors.counting()));

        long totalSmlMessages = jsonLdMailCount + microdataMailCount;
        double percentage = totalMessages > 0 ? (double) totalSmlMessages / totalMessages * 100 : 0;

        System.out.println("Statistics:");
        System.out.println("-----------");
        System.out.println("Runtime: " + runtime.toMinutes() + " mins " + runtime.getSeconds() % 60 + " secs");
        System.out.println("Total Messages: " + totalMessages);
        System.out.println("Total SML Messages: " + totalSmlMessages);
        System.out.println("SML Messages / Total Messages: " + String.format("%.2f", percentage) + "%");
        System.out.println("JSON-LD Mails: " + jsonLdMailCount);
        System.out.println("Microdata Mails: " + microdataMailCount);
        System.out.println("Messages Per Sender:");
        messagesPerSender.forEach((sender, count) -> System.out.println("  * " + sender + ": " + count));
    }
}