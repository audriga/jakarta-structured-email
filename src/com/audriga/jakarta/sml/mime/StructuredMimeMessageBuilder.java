package com.audriga.jakarta.sml.mime;

import com.audriga.jakarta.sml.model.MimeTextContent;
import com.audriga.jakarta.sml.model.StructuredData;
import jakarta.activation.DataHandler;
import jakarta.mail.Address;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;

import java.util.Properties;

public class StructuredMimeMessageBuilder {
	static {
		DataHandler.setDataContentHandlerFactory(new StructuredDataContentHandlerFactory());
	}

	private final StructuredMimeMessageWrapper sm;
	
	public StructuredMimeMessageBuilder() {
		Properties properties = System.getProperties();  
	    Session session = Session.getDefaultInstance(properties, null);
		
		sm = new StructuredMimeMessageWrapper(session);
	}
	
	public StructuredMimeMessageBuilder subject(String subject) throws MessagingException {
		sm.setSubject(subject);
		return this;
	}
	
	public StructuredMimeMessageBuilder from(Address[] address) throws MessagingException {
		sm.setFrom(address);
		return this;
	}
	
	public StructuredMimeMessageBuilder from(String address) throws MessagingException {
		sm.setFrom(address);
		return this;
	}
	
	public StructuredMimeMessageBuilder to(Address address) throws MessagingException {
		sm.addRecipient(RecipientType.TO, address);
		return this;
	}
	
	public StructuredMimeMessageBuilder to(String address) throws MessagingException {
		sm.addRecipients(RecipientType.TO, address);
		return this;
	}
	
	public StructuredMimeMessageBuilder content(Multipart mp) throws MessagingException {
		sm.resetContent(mp);
		return this;
	}
	
	public StructuredMimeMessageBuilder text(String text, String encoding, String subtype) throws MessagingException {
		sm.setText(text, encoding, subtype);
		return this;
	}

	public StructuredMimeMessageBuilder structuredData(StructuredData sd) {
		sm.addStructuredData(sd);
		return this;
	}

	public StructuredMimeMessageBuilder plainText(MimeTextContent textBody) {
		sm.setTextBody(textBody);
		return this;
	}

	public StructuredMimeMessageBuilder htmlText(MimeTextContent htmlBody) {
		sm.setHtmlBody(htmlBody);
		return this;
	}

	public StructuredMimeMessageWrapper build() {
		return sm;
	}

}
