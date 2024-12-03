/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.net.NetworkUtils
 *  com.google.common.annotations.VisibleForTesting
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.activation.FileDataSource
 *  javax.mail.Address
 *  javax.mail.BodyPart
 *  javax.mail.Message
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.Part
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeUtility
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.RandomStringUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.log4j.Logger
 */
package com.atlassian.mail;

import com.atlassian.mail.converters.HtmlConverter;
import com.atlassian.mail.options.GetBodyOptions;
import com.atlassian.net.NetworkUtils;
import com.google.common.annotations.VisibleForTesting;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

public class MailUtils {
    private static final String DEFAULT_ENCODING = "ISO-8859-1";
    static final int BUFFER_SIZE = 65536;
    static final String MULTIPART_ALTERNATE_CONTENT_TYPE = "multipart/alternative";
    static final String MULTIPART_RELATED_CONTENT_TYPE = "multipart/related";
    static final String TEXT_CONTENT_TYPE = "text/plain";
    static final String MESSAGE_CONTENT_TYPE = "message/rfc822";
    static final String HTML_CONTENT_TYPE = "text/html";
    static final String CONTENT_TYPE_X_PKCS7 = "application/x-pkcs7-signature";
    static final String CONTENT_TYPE_PKCS7 = "application/pkcs7-signature";
    private static final Logger log = Logger.getLogger(MailUtils.class);
    private static final String CONTENT_TRANSFER_ENCODING_HEADER = "Content-Transfer-Encoding";
    private static final String MAIL_HEADER_SUBJECT = "Subject";
    private static final String EML_FILE_EXTENSION = "eml";
    private static final String MAIL_CONTENT_TYPE_IMAGE_FIRST_PART = "image/";
    private static final String MAIL_SUBJECT_TO_ATTACHMENT_FILENAME_SANITIZING_REGEX = "[\\.\\-\\*#\\|\\{\\}:=%\\?\\+\\^\\~!\\\\\\/@\\[\\]\\'\"`\\$\\(\\);& ]";
    private static final String MAIL_SUBJECT_TO_ATTACHMENT_FILENAME_SANITIZING_REPLACER_CHARACTER = "_";
    private static final String CONTENT_ID_HEADER = "Content-ID";
    private static final String CONTENT_ID_WRAPPER = "<>";

    public static InternetAddress[] parseAddresses(String addresses) throws AddressException {
        ArrayList<InternetAddress> list = new ArrayList<InternetAddress>();
        list.clear();
        StringTokenizer st = new StringTokenizer(addresses, ", ");
        while (st.hasMoreTokens()) {
            list.add(new InternetAddress(st.nextToken()));
        }
        return list.toArray(new InternetAddress[list.size()]);
    }

    public static String getBody(Message message) throws MessagingException {
        return MailUtils.getBody(message, GetBodyOptions.PREFER_TEXT_BODY_STRIP_WHITESPACE);
    }

    public static String getBody(Message message, GetBodyOptions options) throws MessagingException {
        String body = MailUtils.internalGetBody(message, options.getHtmlConverter(), options.isPreferHtmlPart());
        if (options.isStripWhitespace()) {
            return StringUtils.strip((String)body);
        }
        return body;
    }

    private static String internalGetBody(Message message, HtmlConverter htmlConverter, boolean preferHtmlBody) throws MessagingException {
        try {
            String content = MailUtils.extractTextFromPart((Part)message, htmlConverter);
            if (content == null && MailUtils.getContent((Part)message) instanceof Multipart) {
                content = MailUtils.getBodyFromMultipart((Multipart)MailUtils.getContent((Part)message), htmlConverter, preferHtmlBody);
            }
            if (content == null) {
                log.info((Object)"Could not find any body to extract from the message");
            }
            return content;
        }
        catch (ClassCastException cce) {
            log.info((Object)("Exception getting the content type of message - probably not of type 'String': " + cce.getMessage()));
            return null;
        }
        catch (IOException e) {
            log.info((Object)("IOException whilst getting message content " + e.getMessage()));
            return null;
        }
    }

    public static Attachment[] getAttachments(Message message) throws MessagingException, IOException {
        List<Attachment> attachments = MailUtils.internalGetAttachments(message);
        return attachments.toArray(new Attachment[attachments.size()]);
    }

    private static List<Attachment> internalGetAttachments(Message message) throws MessagingException, IOException {
        ArrayList<Attachment> attachments = new ArrayList<Attachment>();
        if (MailUtils.getContent((Part)message) instanceof Multipart) {
            MailUtils.addAttachments(attachments, (Multipart)MailUtils.getContent((Part)message));
        } else if (MailUtils.isAttachment((Part)message)) {
            attachments.add(MailUtils.buildAttachment((Part)message));
        }
        return attachments;
    }

    private static void addAttachments(List<Attachment> attachments, Multipart parts) throws MessagingException, IOException {
        int n = parts.getCount();
        for (int i = 0; i < n; ++i) {
            BodyPart part = parts.getBodyPart(i);
            if (MailUtils.isAttachment((Part)part)) {
                attachments.add(MailUtils.buildAttachment((Part)part));
                continue;
            }
            try {
                Object content = MailUtils.getContent((Part)part);
                if (content instanceof Message) {
                    attachments.addAll(MailUtils.internalGetAttachments((Message)content));
                    continue;
                }
                if (!(content instanceof Multipart)) continue;
                MailUtils.addAttachments(attachments, (Multipart)content);
                continue;
            }
            catch (UnsupportedEncodingException e) {
                log.warn((Object)"Unsupported encoding found for part while trying to discover attachments. Attachment will be ignored.", (Throwable)e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Attachment buildAttachment(Part part) throws IOException, MessagingException {
        InputStream content = null;
        try {
            content = part.getInputStream();
            String contentType = part.getContentType();
            String contentId = MailUtils.getContentId(part);
            byte[] contents = IOUtils.toByteArray((InputStream)content);
            String fileName = MimeUtility.decodeText((String)MailUtils.getAttachmentFilename(part, contents));
            Attachment attachment = new Attachment(contentId, contentType, fileName, contents);
            return attachment;
        }
        finally {
            IOUtils.closeQuietly((InputStream)content);
        }
    }

    @VisibleForTesting
    static String getAttachmentFilename(Part attachment, byte[] contents) throws MessagingException, IOException {
        String fileName = attachment.getFileName();
        String fileExtension = "";
        if (fileName == null) {
            fileName = RandomStringUtils.randomAlphanumeric((int)8);
            if (MailUtils.isPartMessageType(attachment)) {
                String[] subjects;
                Part innerMessage = (Part)MailUtils.getContent(attachment);
                String[] stringArray = subjects = innerMessage != null ? innerMessage.getHeader(MAIL_HEADER_SUBJECT) : new String[]{};
                if (subjects != null && subjects.length > 0) {
                    fileExtension = EML_FILE_EXTENSION;
                    fileName = MailUtils.sanitizeFilenameFromMailSubject(subjects[0]);
                }
            } else {
                String formatName = MailUtils.getImageFormatName(contents);
                if (StringUtils.isNotBlank((CharSequence)formatName) && !formatName.trim().equals("?")) {
                    fileExtension = formatName.toLowerCase();
                } else if (MailUtils.getContentType(attachment).startsWith(MAIL_CONTENT_TYPE_IMAGE_FIRST_PART)) {
                    fileExtension = MailUtils.getContentType(attachment).substring(MAIL_CONTENT_TYPE_IMAGE_FIRST_PART.length());
                }
            }
        }
        return StringUtils.isNotEmpty((CharSequence)fileExtension) ? String.format("%s.%s", fileName, fileExtension) : fileName;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static String getImageFormatName(byte[] contents) {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(contents);){
            int b1 = stream.read() & 0xFF;
            int b2 = stream.read() & 0xFF;
            if (b1 == 71 && b2 == 73) {
                String string = "GIF";
                return string;
            }
            if (b1 == 137 && b2 == 80) {
                String string = "PNG";
                return string;
            }
            if (b1 == 255 && b2 == 216) {
                String string = "JPEG";
                return string;
            }
            if (b1 == 66 && b2 == 77) {
                String string = "BMP";
                return string;
            }
            if (b1 == 10 && b2 < 6) {
                String string = "PCX";
                return string;
            }
            if (b1 == 70 && b2 == 79) {
                String string = "IFF";
                return string;
            }
            if (b1 == 89 && b2 == 166) {
                String string = "RAS";
                return string;
            }
            if (b1 == 80 && b2 >= 49 && b2 <= 54) {
                int id = b2 - 48;
                if (id < 1 || id > 6) {
                    String string = null;
                    return string;
                }
                switch ((id - 49) % 3) {
                    case 0: {
                        String string = "PBM";
                        return string;
                    }
                    case 1: {
                        String string = "PGM";
                        return string;
                    }
                    case 2: {
                        String string = "PPM";
                        return string;
                    }
                }
                return null;
            }
            if (b1 == 56 && b2 == 66) {
                String string = "PSD";
                return string;
            }
            if (b1 != 70) return null;
            if (b2 != 87) return null;
            String string = "SWF";
            return string;
        }
        catch (IOException ioe) {
            log.debug((Object)"Failed to resolve image format", (Throwable)ioe);
        }
        return null;
    }

    @VisibleForTesting
    static String sanitizeFilenameFromMailSubject(String subject) {
        return subject.replaceAll(MAIL_SUBJECT_TO_ATTACHMENT_FILENAME_SANITIZING_REGEX, MAIL_SUBJECT_TO_ATTACHMENT_FILENAME_SANITIZING_REPLACER_CHARACTER);
    }

    public static boolean isAttachment(Part part) throws MessagingException {
        return "attachment".equalsIgnoreCase(part.getDisposition()) || part.getDisposition() == null && StringUtils.isNotBlank((CharSequence)part.getFileName()) || MailUtils.isPartInline(part);
    }

    public static boolean hasRecipient(String matchEmail, Message message) throws MessagingException {
        Address[] addresses = message.getAllRecipients();
        if (addresses == null || addresses.length == 0) {
            return false;
        }
        for (int i = 0; i < addresses.length; ++i) {
            InternetAddress email = (InternetAddress)addresses[i];
            if (matchEmail.compareToIgnoreCase(email.getAddress()) != 0) continue;
            return true;
        }
        return false;
    }

    public static List<String> getSenders(Message message) throws MessagingException {
        ArrayList<String> senders = new ArrayList<String>();
        Address[] addresses = message.getFrom();
        if (addresses != null) {
            for (int i = 0; i < addresses.length; ++i) {
                InternetAddress addr;
                String emailAddress;
                if (!(addresses[i] instanceof InternetAddress) || (emailAddress = StringUtils.trimToNull((String)(addr = (InternetAddress)addresses[i]).getAddress())) == null) continue;
                senders.add(emailAddress);
            }
        }
        return senders;
    }

    public static MimeBodyPart createAttachmentMimeBodyPart(String path) throws MessagingException {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        FileDataSource source = new FileDataSource(path);
        attachmentPart.setDataHandler(new DataHandler((DataSource)source));
        String fileName = MailUtils.extractFilenameFromPath(path);
        attachmentPart.setFileName(fileName);
        return attachmentPart;
    }

    private static String extractFilenameFromPath(String path) {
        String fileName;
        if (path == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(path, "\\/");
        do {
            fileName = st.nextToken();
        } while (st.hasMoreTokens());
        return fileName;
    }

    public static MimeBodyPart createZippedAttachmentMimeBodyPart(String path) throws MessagingException {
        File tmpFile = null;
        String fileName = MailUtils.extractFilenameFromPath(path);
        try {
            tmpFile = File.createTempFile("atlassian", null);
            FileOutputStream fout = new FileOutputStream(tmpFile);
            ZipOutputStream zout = new ZipOutputStream(fout);
            zout.putNextEntry(new ZipEntry(fileName));
            FileInputStream in = new FileInputStream(path);
            byte[] buffer = new byte[65536];
            int n = 0;
            while (-1 != (n = ((InputStream)in).read(buffer))) {
                zout.write(buffer, 0, n);
            }
            zout.close();
            ((InputStream)in).close();
            log.debug((Object)("Wrote temporary zip of attachment to " + tmpFile));
        }
        catch (FileNotFoundException e) {
            String err = "Couldn't find file '" + path + "' on server: " + e;
            log.error((Object)err, (Throwable)e);
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setText(err);
            return mimeBodyPart;
        }
        catch (IOException e) {
            String err = "Error zipping log file '" + path + "' on server: " + e;
            log.error((Object)err, (Throwable)e);
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setText(err);
            return mimeBodyPart;
        }
        MimeBodyPart attachmentPart = new MimeBodyPart();
        FileDataSource source = new FileDataSource(tmpFile);
        attachmentPart.setDataHandler(new DataHandler((DataSource)source));
        attachmentPart.setFileName(fileName + ".zip");
        attachmentPart.setHeader("Content-Type", "application/zip");
        return attachmentPart;
    }

    private static String getBodyFromMultipart(Multipart multipart, HtmlConverter htmlConverter, boolean preferHtmlBody) throws MessagingException, IOException {
        StringBuffer sb = new StringBuffer();
        MailUtils.getBodyFromMultipart(multipart, sb, htmlConverter, preferHtmlBody);
        return sb.toString();
    }

    private static void getBodyFromMultipart(Multipart multipart, StringBuffer sb, HtmlConverter htmlConverter, boolean preferHtmlBody) throws MessagingException, IOException {
        String multipartType = multipart.getContentType();
        if (multipartType != null && MailUtils.compareContentType(multipartType, MULTIPART_ALTERNATE_CONTENT_TYPE)) {
            LinkedList<String> contentTypeToTry = new LinkedList<String>();
            if (preferHtmlBody) {
                contentTypeToTry.add(HTML_CONTENT_TYPE);
                contentTypeToTry.add(TEXT_CONTENT_TYPE);
            } else {
                contentTypeToTry.add(TEXT_CONTENT_TYPE);
                contentTypeToTry.add(HTML_CONTENT_TYPE);
            }
            BodyPart part = MailUtils.getFirstInlinePartWithMimeType(multipart, (String)contentTypeToTry.poll());
            if (part == null) {
                part = MailUtils.getFirstInlinePartWithMimeType(multipart, (String)contentTypeToTry.poll());
            }
            MailUtils.appendMultipartText(MailUtils.extractTextFromPart((Part)part, htmlConverter), sb);
            return;
        }
        int n = multipart.getCount();
        for (int i = 0; i < n; ++i) {
            BodyPart part = multipart.getBodyPart(i);
            String contentType = part.getContentType();
            if ("attachment".equalsIgnoreCase(part.getDisposition()) || contentType == null) continue;
            try {
                String content = MailUtils.extractTextFromPart((Part)part, htmlConverter);
                if (content != null) {
                    MailUtils.appendMultipartText(content, sb);
                    continue;
                }
                Object contentPart = MailUtils.getContent((Part)part);
                if (contentPart instanceof Message) {
                    String bodyMessagePart = MailUtils.internalGetBody((Message)contentPart, htmlConverter, preferHtmlBody);
                    MailUtils.appendMultipartText(bodyMessagePart, sb);
                    continue;
                }
                if (!(contentPart instanceof Multipart)) continue;
                MailUtils.getBodyFromMultipart((Multipart)contentPart, sb, htmlConverter, preferHtmlBody);
                continue;
            }
            catch (IOException exception) {
                log.warn((Object)("Error retrieving content from part '" + exception.getMessage() + "'"), (Throwable)exception);
            }
        }
    }

    private static void appendMultipartText(String content, StringBuffer sb) throws IOException, MessagingException {
        if (content != null) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(content);
        }
    }

    private static String extractTextFromPart(Part part, HtmlConverter htmlConverter) throws MessagingException, IOException {
        if (part == null) {
            return null;
        }
        String content = null;
        if (MailUtils.isPartPlainText(part)) {
            try {
                content = (String)MailUtils.getContent(part);
            }
            catch (UnsupportedEncodingException e) {
                log.warn((Object)("Found unsupported encoding '" + e.getMessage() + "'. Reading content with " + DEFAULT_ENCODING + " encoding."));
                content = MailUtils.getBody(part, DEFAULT_ENCODING);
            }
        } else if (MailUtils.isPartHtml(part)) {
            content = htmlConverter.convert((String)MailUtils.getContent(part));
        }
        if (content == null) {
            log.debug((Object)("Unable to extract text from MIME part with Content-Type '" + part.getContentType() + "'"));
        }
        return content;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String getBody(Part part, String charsetName) throws IOException, MessagingException {
        String string;
        BufferedReader input = null;
        StringWriter output = null;
        try {
            input = new BufferedReader(new InputStreamReader(part.getInputStream(), charsetName));
            output = new StringWriter();
            IOUtils.copy((Reader)input, (Writer)output);
            string = output.getBuffer().toString();
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
            throw throwable;
        }
        IOUtils.closeQuietly((Reader)input);
        IOUtils.closeQuietly((Writer)output);
        return string;
    }

    private static BodyPart getFirstInlinePartWithMimeType(Multipart multipart, String mimeType) throws MessagingException, IOException {
        int n = multipart.getCount();
        for (int i = 0; i < n; ++i) {
            BodyPart part = multipart.getBodyPart(i);
            String contentType = part.getContentType();
            if ("attachment".equals(part.getDisposition()) || contentType == null) continue;
            if (MailUtils.compareContentType(contentType, mimeType)) {
                return part;
            }
            if (!MailUtils.isPartRelated((Part)part) || !(MailUtils.getContent((Part)part) instanceof Multipart)) continue;
            return MailUtils.getFirstInlinePartWithMimeType((Multipart)MailUtils.getContent((Part)part), mimeType);
        }
        return null;
    }

    private static boolean compareContentType(String contentType, String mimeType) {
        return contentType.toLowerCase().startsWith(mimeType);
    }

    public static boolean isPartHtml(Part part) throws MessagingException {
        String contentType = MailUtils.getContentType(part);
        return HTML_CONTENT_TYPE.equalsIgnoreCase(contentType);
    }

    public static boolean isPartPlainText(Part part) throws MessagingException {
        String contentType = MailUtils.getContentType(part);
        return TEXT_CONTENT_TYPE.equalsIgnoreCase(contentType);
    }

    public static boolean isPartMessageType(Part part) throws MessagingException {
        String contentType = MailUtils.getContentType(part);
        return MESSAGE_CONTENT_TYPE.equalsIgnoreCase(contentType);
    }

    public static boolean isPartRelated(Part part) throws MessagingException {
        String contentType = MailUtils.getContentType(part);
        return MULTIPART_RELATED_CONTENT_TYPE.equalsIgnoreCase(contentType);
    }

    public static String getContentType(Part part) throws MessagingException {
        MailUtils.checkPartNotNull(part);
        String contentType = part.getContentType();
        return MailUtils.getContentType(contentType);
    }

    public static String getContentType(String headerValue) {
        MailUtils.checkHeaderValue(headerValue);
        String out = headerValue;
        int semiColon = headerValue.indexOf(59);
        if (-1 != semiColon) {
            out = headerValue.substring(0, semiColon);
        }
        return out.trim();
    }

    private static void checkHeaderValue(String headerValue) {
        Validate.notEmpty((CharSequence)headerValue);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isContentEmpty(Part part) throws MessagingException, IOException {
        MailUtils.checkPartNotNull(part);
        boolean definitelyEmpty = false;
        Object content = MailUtils.getContent(part);
        if (null == content) {
            definitelyEmpty = true;
        } else {
            if (content instanceof String) {
                String stringContent = (String)content;
                definitelyEmpty = StringUtils.isBlank((CharSequence)stringContent);
            }
            if (content instanceof InputStream) {
                InputStream inputStream = (InputStream)content;
                try {
                    int firstByte = inputStream.read();
                    definitelyEmpty = -1 == firstByte;
                }
                finally {
                    IOUtils.closeQuietly((InputStream)inputStream);
                }
            }
        }
        return definitelyEmpty;
    }

    private static void checkPartNotNull(Part part) {
        Validate.notNull((Object)part, (String)"part should not be null.", (Object[])new Object[0]);
    }

    public static boolean isPartInline(Part part) throws MessagingException {
        MailUtils.checkPartNotNull(part);
        String disposition = part.getDisposition();
        if ("inline".equalsIgnoreCase(disposition)) {
            String file = part.getFileName();
            return StringUtils.isNotBlank((CharSequence)file);
        }
        boolean gotContentId = MailUtils.hasContentId(part);
        if (!gotContentId) {
            return false;
        }
        boolean encodingIsBase64 = MailUtils.isContentBase64Encoded(part);
        boolean typeIsText = MailUtils.isPartPlainText(part) || MailUtils.isPartHtml(part);
        return encodingIsBase64 && !typeIsText;
    }

    public static String getContentId(Part part) throws MessagingException {
        String[] contentIds = part.getHeader(CONTENT_ID_HEADER);
        if (contentIds != null) {
            for (String contentId : contentIds) {
                if (!StringUtils.isNotEmpty((CharSequence)contentId)) continue;
                return StringUtils.strip((String)contentId, (String)CONTENT_ID_WRAPPER);
            }
        }
        return "";
    }

    private static boolean hasContentId(Part part) throws MessagingException {
        return StringUtils.isNotEmpty((CharSequence)MailUtils.getContentId(part));
    }

    private static boolean isContentBase64Encoded(Part part) throws MessagingException {
        boolean gotBase64 = false;
        String[] contentTransferEncodings = part.getHeader(CONTENT_TRANSFER_ENCODING_HEADER);
        if (null != contentTransferEncodings) {
            for (int i = 0; i < contentTransferEncodings.length; ++i) {
                String contentTransferEncoding = contentTransferEncodings[i];
                if (!"base64".equals(contentTransferEncoding)) continue;
                gotBase64 = true;
                break;
            }
        }
        return gotBase64;
    }

    public static boolean isPartAttachment(Part part) throws MessagingException {
        MailUtils.checkPartNotNull(part);
        return "attachment".equalsIgnoreCase(part.getDisposition());
    }

    public static String fixMimeEncodedFilename(String filename) throws IOException {
        String newFilename = filename;
        if (filename.startsWith("=?") || filename.endsWith("?=")) {
            newFilename = MimeUtility.decodeText((String)filename);
        }
        return newFilename;
    }

    public static boolean isPartSignaturePKCS7(Part part) throws MessagingException {
        MailUtils.checkPartNotNull(part);
        String contentType = MailUtils.getContentType(part).toLowerCase(Locale.getDefault());
        return contentType.startsWith(CONTENT_TYPE_PKCS7) || contentType.startsWith(CONTENT_TYPE_X_PKCS7);
    }

    public static String getLocalHostName() {
        String hostname = null;
        try {
            hostname = NetworkUtils.getLocalHostName();
        }
        catch (UnknownHostException e) {
            return "localhost";
        }
        if (hostname == null || hostname.length() == 0) {
            return "localhost";
        }
        if (MailUtils.isIpAddress(hostname)) {
            return '[' + hostname + ']';
        }
        return hostname;
    }

    public static boolean isIpAddress(String hostname) {
        boolean containsAlpha = false;
        boolean containsDot = false;
        for (int i = 0; i < hostname.length(); ++i) {
            char ch = hostname.charAt(i);
            if (ch >= '0' && ch <= '9') continue;
            if (ch == '.') {
                containsDot = true;
                continue;
            }
            if (ch == ':') {
                return true;
            }
            if (ch >= 'a' && ch <= 'f') {
                containsAlpha = true;
                continue;
            }
            if (ch >= 'A' && ch <= 'F') {
                containsAlpha = true;
                continue;
            }
            return false;
        }
        return containsDot && !containsAlpha;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Object getContent(Part part) throws MessagingException, IOException {
        Thread currentThread = Thread.currentThread();
        ClassLoader originalClassLoader = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(MailUtils.class.getClassLoader());
            Object object = part.getContent();
            return object;
        }
        finally {
            currentThread.setContextClassLoader(originalClassLoader);
        }
    }

    public static class Attachment {
        private final String contentId;
        private final String contentType;
        private final String fileName;
        private final byte[] contents;

        public Attachment(String contentId, String contentType, String fileName, byte[] contents) {
            this.contentId = contentId;
            this.contentType = contentType;
            this.fileName = fileName;
            this.contents = contents;
        }

        public Attachment(String contentType, String fileName, byte[] contents) {
            this(null, contentType, fileName, contents);
        }

        public String getContentType() {
            return this.contentType;
        }

        public byte[] getContents() {
            return this.contents;
        }

        public String getFilename() {
            return this.fileName;
        }

        public String getContentId() {
            return this.contentId;
        }
    }
}

