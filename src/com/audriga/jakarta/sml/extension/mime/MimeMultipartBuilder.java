package com.audriga.jakarta.sml.extension.mime;

import com.audriga.jakarta.sml.extension.model.MimeTextContent;
import com.audriga.jakarta.sml.h2lj.model.StructuredData;
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

	public MimeMultipartBuilder addBodyPartText(String text) throws MessagingException {
		if (text != null) {
			return addBodyPartText(new MimeTextContent(text, "utf-8"));
		}
		return this;
	}

	public MimeMultipartBuilder addBodyPartText(MimeTextContent textContent) throws MessagingException {
		if (textContent == null) {
			return this;
		}
		MimeBodyPart bodyPart = new MimeBodyPart();
		String bodyPartType = "text/plain; charset=" + textContent.getEncoding();
		bodyPart.setContent(textContent.getText(), bodyPartType);
		m.addBodyPart(bodyPart);
		return this;
	}

	public MimeMultipartBuilder addBodyPartHtml(String html) throws MessagingException {
		if (html != null) {
			return addBodyPartText(new MimeTextContent(html, "utf-8"));
		}
		return this;
	}

	public MimeMultipartBuilder addBodyPartHtml(MimeTextContent htmlContent) throws MessagingException {
		if (htmlContent == null) {
			return this;
		}
		MimeBodyPart bodyPart = new MimeBodyPart();
		String bodyPartType = "text/html; charset=" + htmlContent.getEncoding();
		bodyPart.setContent(htmlContent.getText(), bodyPartType);
		m.addBodyPart(bodyPart);
		return this;
	}

	public MimeMultipartBuilder addBodyPartJsonLd(StructuredData structuredData) throws MessagingException {
		if (structuredData == null) {
			return this;
		}
		return addBodyPartJsonLd(structuredData, "utf-8");
	}

	public MimeMultipartBuilder addBodyPartJsonLd(
			StructuredData structuredData,
			String jsonLdEncoding
	) throws MessagingException {
		return addBodyPartJsonLd(structuredData, jsonLdEncoding, null);
	}

	public MimeMultipartBuilder addBodyPartJsonLd(
			StructuredData structuredData,
			String jsonLdEncoding,
			String disposition
	) throws MessagingException {
		if (structuredData == null) {
			return this;
		}
		MimeBodyPart bodyPart = new MimeBodyPart();
		String bodyPartType = StructuredData.MIME_TYPE + "; charset=" + jsonLdEncoding;
		bodyPart.setContent(structuredData, bodyPartType);
		if (disposition != null) {
			bodyPart.setDisposition(disposition);
		}
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
