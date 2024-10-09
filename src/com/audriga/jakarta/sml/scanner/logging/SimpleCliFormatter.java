package com.audriga.jakarta.sml.scanner.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleCliFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        String message = record.getMessage().replaceAll("\\r?\\n", " ");
        return String.format("[%s] %s: %s%n",
                record.getLevel().getName(),
                record.getLoggerName(),
                message);
    }
}