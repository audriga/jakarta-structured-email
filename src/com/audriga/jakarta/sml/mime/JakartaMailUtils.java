package com.audriga.jakarta.sml.mime;

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
	
	public static String BOUNDARY = "boundary=\"";
	public static String BOUNDARY2 = "boundary=";
	public static String BOUNDARY3 = "boundary*0=";
	public static String NAME = "name=\"";
	public static String NAME2 = "name=";
	public static String CONTENT_TYPE = "Content-Type: ";

	private static final Logger logger = Logger.getLogger(JakartaMailUtils.class.getName());

	public static String parseContentType(String contentType) {
		String res = null;
		
		if (contentType != null){
			if (contentType.contains("\n")) {
				String[] split = contentType.split("\n");
				res = split[0];
				res = res.replaceAll(CONTENT_TYPE, "");
				res = res.replaceAll(";", "");
				res = res.trim(); // remove \n
			} else if (contentType.contains(CONTENT_TYPE)) {
				// NOOP?
				res = contentType.trim();
				res = res.replaceAll(CONTENT_TYPE, "");
				res = res.replaceAll(";", "");
			} else {
				res = contentType.trim();
			}
		}
		
		if (res.contains(";")) {
			String[] split = res.split(";");
			res = split[0].trim();
		}
		
		if (res.contains(" charset=")) {
			String[] split = res.split(" charset=");
			res = split[0].trim();
		}
			
		return res;
	}
	
	public static String parseBoundary(String contentType) {
		String b = parseLine(contentType, BOUNDARY);
		if (b == null) b = parseLine(contentType, BOUNDARY2);
		if (b == null) b = parseLine(contentType, BOUNDARY3);
		
		if (b == null) {
			logger.warning("FAILED TO PARSE BOUNDARY FROM " + contentType);
		} else {
			if (b.contains(";")) {
				logger.finest("CONTAINS_a:" + b);
				String[] split = b.split(";");
				b = split[0].trim();
				b = b.replaceAll("\"", "");
				logger.finest("CONTAINS_b:" + b);
			}
			
		
		}
		return b;
	}
	
	public static String parseName(String contentType) {
		String n = parseLine(contentType, NAME);
		if (n == null) n = parseLine(contentType, NAME2);
		return n;
	}
	
	public static String parseLine(String contentType, String key) {
		String res = null;
		if ((contentType != null) && contentType.contains(key)) {
			contentType = contentType.trim();
			res = contentType.substring(contentType.indexOf(key) + key.length(), contentType.length()-1);
		} else {
			logger.fine("Can't find " + key + " in " + contentType);
		}
		return res;
	}
	
	public static String messageToEml(Message m) throws Exception{
		return messageToEml(m, StandardCharsets.UTF_8);
	}

	public static String messageToEml(Message m, Charset charSet) throws Exception{
		ByteArrayOutputStream bar = new ByteArrayOutputStream();
		m.writeTo(bar);
        return bar.toString(String.valueOf(charSet));
	}

	public static MimeMessage emlToMessage(InputStream eml) throws Exception{

		Properties properties = System.getProperties();
		Session session = Session.getDefaultInstance(properties, null);

        return new MimeMessage(session, eml);
	}

	public static MimeMessage emlToMessage(String eml, Charset charSet) throws Exception{

		Properties properties = System.getProperties();
		Session session = Session.getDefaultInstance(properties, null);

		return new MimeMessage(session, new ByteArrayInputStream(eml.getBytes(charSet)));
	}

	public static MimeMessage emlToMessage(byte[] eml) throws Exception {
		InputStream inputStream = new ByteArrayInputStream(eml);
		return emlToMessage(inputStream);
	}

	public static List<String> extractSpecificContent(MimeMessage mimeMessage, String contentType) throws Exception {

		List<String> contentList = new ArrayList<>();
		Object content = mimeMessage.getContent();
		logger.fine("Content-Type: " + parseContentType(mimeMessage.getContentType()));
		logger.fine("Content-Type: " + mimeMessage.getContentType());
		if (Objects.equals(contentType, parseContentType(mimeMessage.getContentType()))) {
			if(content instanceof String) {
				contentList.add((String) content);
			} else if(content instanceof InputStream) {
				InputStream inputStream = (InputStream) content;
                String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
				contentList.add(text);
			} else {
				throw new Exception("Email part is of unknown/currently not supported instance: " + content.getClass().getName());
			}
		}
		if(content instanceof MimeMultipart) {
			MimeMultipart multi = (MimeMultipart) content;
            List<String> specificContentList = extractSpecificContent(multi, contentType);
			if (!specificContentList.isEmpty()) {
				contentList.addAll(specificContentList);
			}
		}

		return contentList;

	}


	private static List<String> extractSpecificContent(MimeMultipart mimeMultipart, String contentType) throws Exception {
		List<String> contentList = new ArrayList<>();
		for(int i = 0; i < mimeMultipart.getCount(); ++i) {
			BodyPart bo = mimeMultipart.getBodyPart(i);
			logger.fine("Content-Type: " + parseContentType(mimeMultipart.getContentType()));
			logger.fine("Content-Type: " + mimeMultipart.getContentType());
			if (Objects.equals(contentType, parseContentType(bo.getContentType()))) {
				if (bo.getContent() instanceof InputStream) {
					InputStream inputStream = (InputStream) bo.getContent();
                    String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
					contentList.add(text);
					continue;
				} else if (bo.getContent() instanceof String) {
					contentList.add((String) bo.getContent());
				} else {
					throw new Exception("Email part is of unknown/currently not supported instance: " + bo.getContent().getClass().getName());
				}
			}

			// iterate multipart
			if (bo.getContent() instanceof MimeMultipart) {
				MimeMultipart multi = (MimeMultipart) bo.getContent();
                List<String> amps = extractSpecificContent(multi, contentType);
				if (!amps.isEmpty()) {
					contentList.addAll(amps);
				}
			}
		}

		return contentList;
	}


}
