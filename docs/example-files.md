# Structured Email example EML files

The folder `test/resources/eml` contains example email files (EML) for adding structured data to email messages ("structured email").

It particularly shows different technical options for adding structured data to emails for discussion in the [Structured Email Working Group](https://datatracker.ietf.org/group/sml/about/) at the IETF (see also [GitHub issue](https://github.com/hhappel/draft-happel-structured-email/issues/3)).

## Guide to example files

The file naming pattern is `<variant>{-text|-html|-json}.eml`:

* `<variant>`: The variant of the email message. Possible values are:
    * `inline` for a classic multipart-alternative message with structured data inline in the HTML body.
    * `html` for a message without any multipart structure, only containing an HTML body with structured data.
    * `alternative` for a message using a "multipart-alternative" content type.
    * `related` for a message using a top-level "multipart-related" content type.
* `-text`: The message contains a text body.
* `-html`: The message contains an HTML body.
* `-json`: The message contains structured data in JSON-LD format.

The order of the body parts in the filename reflects the order of the MIME parts in the message.

## MIME structure of message variants

This section illustrates the structure of the MIME body ("MIME tree") of different variants.

### Inline HTML

This is the format currently [supported by some ISPs](https://structured.email/related_work/frameworks/schema_org_for_email.html).

Created by `InlineHtmlMessageGenerator`:

```
multipart/alternative
├─ text/plain
└─ text/html
```

### HTML only

Created by `HtmlOnlyMessageGenerator`:

```
text/html
```

### multipart/alternative

Created by `MultipartAlternativeMessageGenerator`:

```
multipart/alternative
├─ text/plain
├─ text/html
└─ application/json+ld
```

There are same variants in the test data:
- [text-html-json (as depicted above)](../test/resources/eml/alternative-text-html-json.eml)
- [text-json-html (different order)](../test/resources/eml/alternative-text-json-html.eml)
- ...

### multipart/related

Created by `MultipartRelatedMessageGenerator`:

```
multipart/related
├─ multipart/alternative
| ├─ text/plain
| └─ text/html
└─ application/json+ld
```

## Creating additional Structured Email message variants

To create structured email messages of different variants, you can choose between one of the following classes implementing to create emails with embedded structured data:

* `InlineHtmlMessageGenerator`: Creates a MIME message with a multipart/alternative content type and an inline HTML body.
* `HtmlOnlyMessageGenerator`: Creates a MIME message with a text/html content type and an inline HTML body.
* `MultipartRelatedMessageGenerator`: Creates a MIME message with a multipart/related content type.
* `MultipartAlternativeMessageGenerator`: Creates a MIME message with a multipart/alternative content type.

Typically, the structured data represents the full email message and more, except for
`MultipartAlternativeMessageGenerator`, where the structured data only relates to a part of the email message.

See also [Mail Processing Test](../test/com/audriga/jakarta/sml/test/MailProcessingTest.java) and [Advanced Mail Processing Test](../test/com/audriga/jakarta/sml/test/MailProcessingAdvancedTest.java), which demonstrates parsing and creating the EML files.
