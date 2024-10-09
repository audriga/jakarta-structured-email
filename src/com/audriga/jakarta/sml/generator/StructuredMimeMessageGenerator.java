package com.audriga.jakarta.sml.generator;

import com.audriga.jakarta.sml.model.StructuredData;
import com.audriga.jakarta.sml.mime.StructuredMimeMessageWrapper;
import jakarta.mail.MessagingException;

import java.util.List;

public interface StructuredMimeMessageGenerator {
    StructuredMimeMessageWrapper generate(String subject, String textBody, String htmlBody, List<StructuredData> structuredData) throws MessagingException;
}
