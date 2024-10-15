package com.audriga.jakarta.sml.mime;

import com.audriga.jakarta.sml.parser.StructuredMimeParseUtils;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public class JakartaMailUtils {

	private static final Logger logger = Logger.getLogger(JakartaMailUtils.class.getName());

	public static StructuredMimeMessageWrapper emlToMessage(InputStream eml) throws Exception{

		Properties properties = System.getProperties();
		Session session = Session.getDefaultInstance(properties, null);

        return StructuredMimeParseUtils.parseMessage(new MimeMessage(session, eml));
	}

	public static StructuredMimeMessageWrapper emlToMessage(String eml, Charset charSet) throws Exception{

		Properties properties = System.getProperties();
		Session session = Session.getDefaultInstance(properties, null);

		return StructuredMimeParseUtils.parseMessage(new MimeMessage(session, new ByteArrayInputStream(eml.getBytes(charSet))));
	}

	public static StructuredMimeMessageWrapper emlToMessage(byte[] eml) throws Exception {
		InputStream inputStream = new ByteArrayInputStream(eml);
		return emlToMessage(inputStream);
	}

}
