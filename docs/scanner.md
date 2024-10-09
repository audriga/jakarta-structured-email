# Structured Email IMAP account scanner

This is a command line tool for scanning an IMAP account. 

Its goal is to **find existing messages which contain Schema.org markup** (JSON-LD or Microdata) and to optionally dump the findings as JSON-LD.

**Please consider donating test data** (in anonymized/pseudonomized form) to the [schema-org-examples dataset](https://github.com/audriga/schema-org-examples/).

## Building

See build instructions in the [main README](../README.md#Building)

## Running

Requires Java 8+.

You'll also need your IMAP host name and corresponding login credentials to get started.

The following command will traverse all IMAP folders and output all structured data found in the emails **to the console**:

```shell
java -jar sml-account-scan.jar -h <host> -u <user> -p <password>
```

Optionally, you can dump structured data **to a directory** (one file per source message):

```shell
java -jar sml-account-scan.jar -h <host> -u <user> -p <password> -d <output-directory>
```

Example:
```shell
java -jar sml-account-scan.jar -h imap.example.com -u taylor@example.com -p "G37-5CHW1F7Y" -d /tmp/scanner-output/
```

See also additional config options below.

### Using with FastMail/Gmail/Microsoft accounts

Some email providers, such as FastMail, Google, and Microsoft recommend OAuth as the default authentication mechnanism. Since this scanner currently does not support OAuth, you can alternatively set up a so-called "app-specific passwords" for those providers.

Please see the corresponding provider documentation for details:

- **FastMail**: [Adding a new third-party app](https://www.fastmail.help/hc/en-us/articles/360058752854-App-passwords#third-party)
- **Gmail**: [Sign in with app passwords](https://support.google.com/accounts/answer/185833)
- **Microsoft (Outlook.com / Microsoft 365)**: [How to get and use app passwords](https://support.microsoft.com/en-us/account-billing/how-to-get-and-use-app-passwords-5896ed9b-4263-e681-128a-a6f2979a7944)

Use the generated app-specific password for the `<password>` parameter when configuring the scanner CLI application.

## Additional config options

### IMAP Folders

You can specify a comma-separated list of IMAP folders to scan using the `-i` option.

Example:
```shell
java -jar sml-account-scan.jar -h imap.example.com -u taylor@example.com -p "G37-5CHW1F7Y" -d /tmp/scanner-output/ -i git,INBOX,lists.sml
```

### IMAP/IMAPS/STARTTLS and ports

By default, the tool will use IMAPS (IMAP over SSL) to connect to the email server. You can override this behavior and specify the connection type and port using the following options:

- `-f, --force-no-ssl`: Disable SSL for the connection (use plain IMAP)
- `-o, --override-port <arg>`: Override the default remote system port
- STARTTLS is currently not supported

## Output

### Console log format

Each log line corresponds to an email that contains structured data. The format is as follows:

```
<date> | <sender> | data objects: <structured-object-count> | <message-id>
```

Example: `09/27/24 16:09 | Noah Baumbach <pullrequests-reply@bitbucket.org>                | data objects: 1 | <pr-audriga/jsonld2html-javascript/40/updated/a3@bitbucket.org>`

The meaning of each part is:

* `date`: The date and time of the email.
* `sender`: The sender of the email.
* `structured-data-object-count`: The number of structured data objects found in the email.
* `message-id`: The message ID of the email.

### File output format

If `-d` has been used, the output directory will contain one file for each structured data object found in an email. The naming convention is:

```
<main-schema-org-type>-<date>-<folder>-<messageid>-<sender>.<syntax>.[json|html]
```

Example: `emailmessage-2024_04_12-git-gfaudriga__notifications_github_com-audriga_nextcloud_mail_pull_1_push_1797267404_github_com.jsonld.json`

More details:

* `schema-org-type`: The schema.org type found in the email, derived from something like `EmailMessage`
* `date`: The date of the email
* `folder`: The folder the email was found in
* `messageid`: The message ID of the email, derived from something like `<audriga/nextcloud-mail/pull/1/push/1797267404@github.com>`
* `sender`: The sender of the email, derived from something like `gfaudriga <notifications@github.com>`
* `syntax`: The syntax of the structured data, derived from the content type of the structured data. Supports `jsonld` or `microdata`.

In case the scanner encountered an error during parsing, the output file will contain the full HTML body of the email instead. The syntax will be "unknown" in that case.
