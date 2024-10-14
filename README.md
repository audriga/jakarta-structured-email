# Jakarta Structured Email

Jakarta Structured Email provides extensions for the Java [Jakarta Mail](https://jakartaee.github.io/mail-api/) library for **creating** and **parsing** email messages containing structured data ([structured email](https://structured.email)).

## Features

- Create and parse structured email data located in the HTML email body or in other MIME body parts
- Parse JSON-LD and Microdata

In addition, this project contains
- An [IMAP account scanner](#imap-account-scanner) which uses this library to extract structured data from messages in an email account
- A collection of [example EML files](#structured-email-example-eml-files) generated with this library

## Usage

The following examples demonstrate the creating and parsing of structured email based on JSON-LD embedded into the body of HTML emails. This particular approach is currently [supported by some ISPs](https://structured.email/related_work/frameworks/schema_org_for_email.html).

Note that there is ongoing discussion about alternative approaches of embedded structured data in emails within in the [Structured Email Working Group](https://datatracker.ietf.org/group/sml/about/) at the IETF. See the [example files documentation](docs/example-files.md) for more details.

The goal of this library is to support and showcase multiple possible approaches, allowing users to easily adopt an ultimately standardized approach.

### Creating Structured Email messages

To create structured email messages, simply use the generator to create a MIME message with structured data included in the HTML body via `<script>` tag:

```java
import com.audriga.jakarta.sml.model.StructuredData;
import com.audriga.jakarta.sml.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.mime.InlineHtmlMessageBuilder;
import jakarta.mail.MessagingException;

import java.util.ArrayList;
import java.util.Collections;

public class Example {

    public static void main(String[] args) throws MessagingException {
    
        // Comment email content elements
        String emailSubject = "My first structured email";
        String textEmailBody = "This is a test email";
        String htmlEmailBody = "<html><body>This is a <b>test email</b></body></html>";
    
        // Structured data
        String jsonLd = "{\r\n    \"@context\":              \"http://schema.org\",\r\n    \"@type\":                 \"EventReservation\",\r\n    \"reservationId\":         \"MBE12345\",\r\n    \"underName\": {\r\n        \"@type\":               \"Person\",\r\n        \"name\":                \"Noah Baumbach\"\r\n    },\r\n    \"reservationFor\": {\r\n        \"@type\":               \"Event\",\r\n        \"name\":                \"Make Better Email 2024\",\r\n        \"startDate\":           \"2024-10-15\",\r\n        \"organizer\": {\r\n            \"@type\":            \"Organization\",\r\n            \"name\":             \"Fastmail Pty Ltd.\",\r\n            \"logo\":             \"https://www.fastmail.com/assets/images/FM-Logo-RGB-IiFj8alCx1-3073.webp\"\r\n        },\r\n        \"location\": {\r\n            \"@type\":             \"Place\",\r\n            \"name\":              \"Isode Ltd\",\r\n            \"address\": {\r\n                \"@type\":           \"PostalAddress\",\r\n                \"streetAddress\":   \"14 Castle Mews\",\r\n                \"addressLocality\": \"Hampton\",\r\n                \"addressRegion\":   \"Greater London\",\r\n                \"postalCode\":      \"TW12 2NP\",\r\n                \"addressCountry\":  \"UK\"\r\n            }\r\n        }\r\n    }\r\n}";

        List<StructuredData> structuredDataList = new ArrayList<>();
        structuredDataList.add(new StructuredData(jsonLd));
            
        StructuredMimeMessageWrapper message = new InlineHtmlMessageBuilder()
                .subject(emailSubject)
                .textBody(textEmailBody)
                .htmlBody(htmlEmailBody)
                .structuredData(structuredDataList)
                .build();

        // Use the message as needed
    }
}
```

### Parsing Structured Email messages

To parse structured email messages, you can use the provided classes and methods to extract structured data from the email content.

```java
import com.audriga.jakarta.sml.mime.StructuredMimeMessageWrapper;
import com.audriga.jakarta.sml.parser.StructuredEmailParser;
import com.audriga.jakarta.sml.model.StructuredData;
import jakarta.mail.internet.MimeMessage;

import java.util.List;

public class Example {

    public static void main(String[] args) throws Exception {
    
        MimeMessage message = ... // obtain a MimeMessage instance
        
        StructuredMimeMessageWrapper structuredMessage = new StructuredMimeParser().parseMessage(message);

        for (StructuredData data : structuredMessage.getStructuredData()) {
            System.out.println(data.getBody());
        }
    }
}
```

### Further examples

For more complete examples, see the [MailProcessingTest](test/com/audriga/jakarta/sml/test/MailProcessingTest.java) class, which demonstrates parsing and creating mails.

## Building

Ensure that the following prerequisites are in place:

- Java 8 or higher
- [Apache Ant](https://ant.apache.org/) and [Apache Ivy](https://ant.apache.org/ivy/)

Then, to build the project, use the following command:

```shell
ant jar
```

## Additional resources

Beyond the Jakarta Mail extension code, there are two further artifacts currently in this repository.

### IMAP account scanner

This project contains an IMAP account scanner command line tool, which can be used to find and extract structured email data from existing email accounts. See the [IMAP account scanner documentation](docs/scanner.md) for details.

### Structured Email example EML files

The folder `test/resources/eml` contains several example files generated with this library. Refer to the [example files documentation](docs/example-files.md) for more information.

## Contributing

Contributions are welcome! Please open new issues or pull requests on GitHub.

## License

This project is licensed under the MIT License. See the LICENSE file for details.

## Acknowledgements

This project was partially funded through the [NGI0](https://nlnet.nl/core) Core Fund, a fund established by [NLnet](https://nlnet.nl/) with financial support from the European Commission's [Next Generation Internet](https://ngi.eu/) programme, under the aegis of DG Communications Networks, Content and Technology under grant agreement No [101092990](https://cordis.europa.eu/project/id/101092990). Thank you!

See also [Structured Email for Roundcube](https://nlnet.nl/project/StructuredEmail/).
