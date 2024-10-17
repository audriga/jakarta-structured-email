package com.audriga.jakarta.sml.extension.model;

public class MimeTextContent {
        private String text;
        private String encoding;

        public MimeTextContent(String text, String encoding) {
            this.text = text;
            this.encoding = encoding;
        }

        public String getText() {
            return text;
        }

        public String getEncoding() {
            return encoding;
        }
}
