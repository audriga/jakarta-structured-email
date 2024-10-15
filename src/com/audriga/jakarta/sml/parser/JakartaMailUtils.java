package com.audriga.jakarta.sml.parser;

import com.audriga.jakarta.sml.mime.StructuredMimeMessageWrapper;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
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
