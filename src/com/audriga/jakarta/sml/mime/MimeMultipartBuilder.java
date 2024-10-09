package com.audriga.jakarta.sml.mime;

import com.audriga.jakarta.sml.model.StructuredData;
import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

public class MimeMultipartBuilder {

	private final MimeMultipart m;

	public enum MULTIPART {
		
		ALTERNATIVE("alternative"),
		MIXED("mixed"),
		RELATED("related");
		
		public final String value;
		
		MULTIPART(String value) {
			this.value = value;
		}
		
	}
	
	/*
	public MIMEMultipartBuilder createMultipartAlternative() {
		
	}*/
	
	public MimeMultipartBuilder(MULTIPART type) {
		m = new MimeMultipart(type.value);
	}

	public MimeMultipartBuilder addBodyPart(MimeMultipart mp) throws MessagingException {
		MimeBodyPart bodyPart = new MimeBodyPart();
		//String bodyPartType = mp.getContentType();
		bodyPart.setContent(mp);
		m.addBodyPart(bodyPart);
		return this;
	}

	public MimeMultipartBuilder addAttachment(byte[] data, String contentType, String contentId) throws MessagingException {
		MimeBodyPart bodyPart = new MimeBodyPart();

		bodyPart.setDataHandler(new DataHandler(
				new ByteArrayDataSource(data, contentType)));
		bodyPart.setContentID(contentId);
		
		m.addBodyPart(bodyPart);
		return this;
	}
	
	public MimeMultipartBuilder addBodyPartText(String text, String textEncoding) throws MessagingException {
		if (text == null) {
			return this;
		}
		MimeBodyPart bodyPart = new MimeBodyPart();
		String bodyPartType = "text/plain; charset=" + textEncoding;
		bodyPart.setContent(text, bodyPartType);
		m.addBodyPart(bodyPart);
		return this;
	}
	
	public MimeMultipartBuilder addBodyPartHtml(String htmlText, String htmlTextEncoding) throws MessagingException {
		if (htmlText == null) {
			return this;
		}
		MimeBodyPart bodyPart = new MimeBodyPart();
		String bodyPartType = "text/html; charset=" + htmlTextEncoding;
		bodyPart.setContent(htmlText, bodyPartType);
		m.addBodyPart(bodyPart);
		return this;
	}

	public MimeMultipartBuilder addBodyPartJsonLd(StructuredData structuredData, String jsonLdEncoding) throws MessagingException {
		if (structuredData == null) {
			return this;
		}
		MimeBodyPart bodyPart = new MimeBodyPart();
		String bodyPartType = StructuredData.MIME_TYPE + "; charset=" + jsonLdEncoding;
		bodyPart.setContent(structuredData, bodyPartType);
		m.addBodyPart(bodyPart);
		return this;
	}

	public MimeMultipartBuilder addFileAttachment(byte[] data, String contentType, String contentId) throws MessagingException {
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setDataHandler(new DataHandler(
				new ByteArrayDataSource(data, contentType)));
		bodyPart.setContentID(contentId);
		m.addBodyPart(bodyPart);

		
		return this;
	}
	

	public MimeMultipartBuilder addBodyPart(MimeBodyPart bodyPart) throws MessagingException {
		m.addBodyPart(bodyPart);
		return this;
	}
	
	public MimeMultipart build() {
		return m;
	}
	
	/*
	private static MimeMultipart createAlternativePart(String bodyText, String bodyHtml, String bodyTextEncoding,
			String bodyHtmlEncoding) throws MessagingException {
		MimeMultipart contentMultipart = new MimeMultipart("alternative");
		mLogger.log(Level.FINE, "Creating alternative body part");
		if (bodyText != null) {
			MimeBodyPart bodyPart = createBodyPart(bodyText, createPlainBodyPartType(bodyTextEncoding));
			contentMultipart.addBodyPart(bodyPart);
		}

		if (bodyHtml != null) {
			MimeBodyPart bodyPart = createBodyPart(bodyHtml, createHtmlBodyPartType(bodyHtmlEncoding));
			contentMultipart.addBodyPart(bodyPart);
		}

		return contentMultipart;
	}*/
	
}
