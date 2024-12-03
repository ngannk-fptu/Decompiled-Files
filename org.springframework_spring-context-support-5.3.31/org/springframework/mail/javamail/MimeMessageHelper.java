/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.activation.FileDataSource
 *  javax.activation.FileTypeMap
 *  javax.mail.Address
 *  javax.mail.BodyPart
 *  javax.mail.Message$RecipientType
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimeMultipart
 *  javax.mail.internet.MimePart
 *  javax.mail.internet.MimeUtility
 *  org.springframework.core.io.InputStreamSource
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.mail.javamail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mail.javamail.SmartMimeMessage;
import org.springframework.util.Assert;

public class MimeMessageHelper {
    public static final int MULTIPART_MODE_NO = 0;
    public static final int MULTIPART_MODE_MIXED = 1;
    public static final int MULTIPART_MODE_RELATED = 2;
    public static final int MULTIPART_MODE_MIXED_RELATED = 3;
    private static final String MULTIPART_SUBTYPE_MIXED = "mixed";
    private static final String MULTIPART_SUBTYPE_RELATED = "related";
    private static final String MULTIPART_SUBTYPE_ALTERNATIVE = "alternative";
    private static final String CONTENT_TYPE_ALTERNATIVE = "text/alternative";
    private static final String CONTENT_TYPE_HTML = "text/html";
    private static final String CONTENT_TYPE_CHARSET_SUFFIX = ";charset=";
    private static final String HEADER_PRIORITY = "X-Priority";
    private final MimeMessage mimeMessage;
    @Nullable
    private MimeMultipart rootMimeMultipart;
    @Nullable
    private MimeMultipart mimeMultipart;
    @Nullable
    private final String encoding;
    private FileTypeMap fileTypeMap;
    private boolean encodeFilenames = false;
    private boolean validateAddresses = false;

    public MimeMessageHelper(MimeMessage mimeMessage) {
        this(mimeMessage, null);
    }

    public MimeMessageHelper(MimeMessage mimeMessage, @Nullable String encoding) {
        this.mimeMessage = mimeMessage;
        this.encoding = encoding != null ? encoding : this.getDefaultEncoding(mimeMessage);
        this.fileTypeMap = this.getDefaultFileTypeMap(mimeMessage);
    }

    public MimeMessageHelper(MimeMessage mimeMessage, boolean multipart) throws MessagingException {
        this(mimeMessage, multipart, null);
    }

    public MimeMessageHelper(MimeMessage mimeMessage, boolean multipart, @Nullable String encoding) throws MessagingException {
        this(mimeMessage, multipart ? 3 : 0, encoding);
    }

    public MimeMessageHelper(MimeMessage mimeMessage, int multipartMode) throws MessagingException {
        this(mimeMessage, multipartMode, null);
    }

    public MimeMessageHelper(MimeMessage mimeMessage, int multipartMode, @Nullable String encoding) throws MessagingException {
        this.mimeMessage = mimeMessage;
        this.createMimeMultiparts(mimeMessage, multipartMode);
        this.encoding = encoding != null ? encoding : this.getDefaultEncoding(mimeMessage);
        this.fileTypeMap = this.getDefaultFileTypeMap(mimeMessage);
    }

    public final MimeMessage getMimeMessage() {
        return this.mimeMessage;
    }

    protected void createMimeMultiparts(MimeMessage mimeMessage, int multipartMode) throws MessagingException {
        switch (multipartMode) {
            case 0: {
                this.setMimeMultiparts(null, null);
                break;
            }
            case 1: {
                MimeMultipart mixedMultipart = new MimeMultipart(MULTIPART_SUBTYPE_MIXED);
                mimeMessage.setContent((Multipart)mixedMultipart);
                this.setMimeMultiparts(mixedMultipart, mixedMultipart);
                break;
            }
            case 2: {
                MimeMultipart relatedMultipart = new MimeMultipart(MULTIPART_SUBTYPE_RELATED);
                mimeMessage.setContent((Multipart)relatedMultipart);
                this.setMimeMultiparts(relatedMultipart, relatedMultipart);
                break;
            }
            case 3: {
                MimeMultipart rootMixedMultipart = new MimeMultipart(MULTIPART_SUBTYPE_MIXED);
                mimeMessage.setContent((Multipart)rootMixedMultipart);
                MimeMultipart nestedRelatedMultipart = new MimeMultipart(MULTIPART_SUBTYPE_RELATED);
                MimeBodyPart relatedBodyPart = new MimeBodyPart();
                relatedBodyPart.setContent((Multipart)nestedRelatedMultipart);
                rootMixedMultipart.addBodyPart((BodyPart)relatedBodyPart);
                this.setMimeMultiparts(rootMixedMultipart, nestedRelatedMultipart);
                break;
            }
            default: {
                throw new IllegalArgumentException("Only multipart modes MIXED_RELATED, RELATED and NO supported");
            }
        }
    }

    protected final void setMimeMultiparts(@Nullable MimeMultipart root, @Nullable MimeMultipart main) {
        this.rootMimeMultipart = root;
        this.mimeMultipart = main;
    }

    public final boolean isMultipart() {
        return this.rootMimeMultipart != null;
    }

    public final MimeMultipart getRootMimeMultipart() throws IllegalStateException {
        if (this.rootMimeMultipart == null) {
            throw new IllegalStateException("Not in multipart mode - create an appropriate MimeMessageHelper via a constructor that takes a 'multipart' flag if you need to set alternative texts or add inline elements or attachments.");
        }
        return this.rootMimeMultipart;
    }

    public final MimeMultipart getMimeMultipart() throws IllegalStateException {
        if (this.mimeMultipart == null) {
            throw new IllegalStateException("Not in multipart mode - create an appropriate MimeMessageHelper via a constructor that takes a 'multipart' flag if you need to set alternative texts or add inline elements or attachments.");
        }
        return this.mimeMultipart;
    }

    @Nullable
    protected String getDefaultEncoding(MimeMessage mimeMessage) {
        if (mimeMessage instanceof SmartMimeMessage) {
            return ((SmartMimeMessage)mimeMessage).getDefaultEncoding();
        }
        return null;
    }

    @Nullable
    public String getEncoding() {
        return this.encoding;
    }

    protected FileTypeMap getDefaultFileTypeMap(MimeMessage mimeMessage) {
        FileTypeMap fileTypeMap;
        if (mimeMessage instanceof SmartMimeMessage && (fileTypeMap = ((SmartMimeMessage)mimeMessage).getDefaultFileTypeMap()) != null) {
            return fileTypeMap;
        }
        fileTypeMap = new ConfigurableMimeFileTypeMap();
        fileTypeMap.afterPropertiesSet();
        return fileTypeMap;
    }

    public void setFileTypeMap(@Nullable FileTypeMap fileTypeMap) {
        this.fileTypeMap = fileTypeMap != null ? fileTypeMap : this.getDefaultFileTypeMap(this.getMimeMessage());
    }

    public FileTypeMap getFileTypeMap() {
        return this.fileTypeMap;
    }

    public void setEncodeFilenames(boolean encodeFilenames) {
        this.encodeFilenames = encodeFilenames;
    }

    public boolean isEncodeFilenames() {
        return this.encodeFilenames;
    }

    public void setValidateAddresses(boolean validateAddresses) {
        this.validateAddresses = validateAddresses;
    }

    public boolean isValidateAddresses() {
        return this.validateAddresses;
    }

    protected void validateAddress(InternetAddress address) throws AddressException {
        if (this.isValidateAddresses()) {
            address.validate();
        }
    }

    protected void validateAddresses(InternetAddress[] addresses) throws AddressException {
        for (InternetAddress address : addresses) {
            this.validateAddress(address);
        }
    }

    public void setFrom(InternetAddress from) throws MessagingException {
        Assert.notNull((Object)from, (String)"From address must not be null");
        this.validateAddress(from);
        this.mimeMessage.setFrom((Address)from);
    }

    public void setFrom(String from) throws MessagingException {
        Assert.notNull((Object)from, (String)"From address must not be null");
        this.setFrom(this.parseAddress(from));
    }

    public void setFrom(String from, String personal) throws MessagingException, UnsupportedEncodingException {
        Assert.notNull((Object)from, (String)"From address must not be null");
        this.setFrom(this.getEncoding() != null ? new InternetAddress(from, personal, this.getEncoding()) : new InternetAddress(from, personal));
    }

    public void setReplyTo(InternetAddress replyTo) throws MessagingException {
        Assert.notNull((Object)replyTo, (String)"Reply-to address must not be null");
        this.validateAddress(replyTo);
        this.mimeMessage.setReplyTo((Address[])new InternetAddress[]{replyTo});
    }

    public void setReplyTo(String replyTo) throws MessagingException {
        Assert.notNull((Object)replyTo, (String)"Reply-to address must not be null");
        this.setReplyTo(this.parseAddress(replyTo));
    }

    public void setReplyTo(String replyTo, String personal) throws MessagingException, UnsupportedEncodingException {
        Assert.notNull((Object)replyTo, (String)"Reply-to address must not be null");
        InternetAddress replyToAddress = this.getEncoding() != null ? new InternetAddress(replyTo, personal, this.getEncoding()) : new InternetAddress(replyTo, personal);
        this.setReplyTo(replyToAddress);
    }

    public void setTo(InternetAddress to) throws MessagingException {
        Assert.notNull((Object)to, (String)"To address must not be null");
        this.validateAddress(to);
        this.mimeMessage.setRecipient(Message.RecipientType.TO, (Address)to);
    }

    public void setTo(InternetAddress[] to) throws MessagingException {
        Assert.notNull((Object)to, (String)"To address array must not be null");
        this.validateAddresses(to);
        this.mimeMessage.setRecipients(Message.RecipientType.TO, (Address[])to);
    }

    public void setTo(String to) throws MessagingException {
        Assert.notNull((Object)to, (String)"To address must not be null");
        this.setTo(this.parseAddress(to));
    }

    public void setTo(String[] to) throws MessagingException {
        Assert.notNull((Object)to, (String)"To address array must not be null");
        InternetAddress[] addresses = new InternetAddress[to.length];
        for (int i = 0; i < to.length; ++i) {
            addresses[i] = this.parseAddress(to[i]);
        }
        this.setTo(addresses);
    }

    public void addTo(InternetAddress to) throws MessagingException {
        Assert.notNull((Object)to, (String)"To address must not be null");
        this.validateAddress(to);
        this.mimeMessage.addRecipient(Message.RecipientType.TO, (Address)to);
    }

    public void addTo(String to) throws MessagingException {
        Assert.notNull((Object)to, (String)"To address must not be null");
        this.addTo(this.parseAddress(to));
    }

    public void addTo(String to, String personal) throws MessagingException, UnsupportedEncodingException {
        Assert.notNull((Object)to, (String)"To address must not be null");
        this.addTo(this.getEncoding() != null ? new InternetAddress(to, personal, this.getEncoding()) : new InternetAddress(to, personal));
    }

    public void setCc(InternetAddress cc) throws MessagingException {
        Assert.notNull((Object)cc, (String)"Cc address must not be null");
        this.validateAddress(cc);
        this.mimeMessage.setRecipient(Message.RecipientType.CC, (Address)cc);
    }

    public void setCc(InternetAddress[] cc) throws MessagingException {
        Assert.notNull((Object)cc, (String)"Cc address array must not be null");
        this.validateAddresses(cc);
        this.mimeMessage.setRecipients(Message.RecipientType.CC, (Address[])cc);
    }

    public void setCc(String cc) throws MessagingException {
        Assert.notNull((Object)cc, (String)"Cc address must not be null");
        this.setCc(this.parseAddress(cc));
    }

    public void setCc(String[] cc) throws MessagingException {
        Assert.notNull((Object)cc, (String)"Cc address array must not be null");
        InternetAddress[] addresses = new InternetAddress[cc.length];
        for (int i = 0; i < cc.length; ++i) {
            addresses[i] = this.parseAddress(cc[i]);
        }
        this.setCc(addresses);
    }

    public void addCc(InternetAddress cc) throws MessagingException {
        Assert.notNull((Object)cc, (String)"Cc address must not be null");
        this.validateAddress(cc);
        this.mimeMessage.addRecipient(Message.RecipientType.CC, (Address)cc);
    }

    public void addCc(String cc) throws MessagingException {
        Assert.notNull((Object)cc, (String)"Cc address must not be null");
        this.addCc(this.parseAddress(cc));
    }

    public void addCc(String cc, String personal) throws MessagingException, UnsupportedEncodingException {
        Assert.notNull((Object)cc, (String)"Cc address must not be null");
        this.addCc(this.getEncoding() != null ? new InternetAddress(cc, personal, this.getEncoding()) : new InternetAddress(cc, personal));
    }

    public void setBcc(InternetAddress bcc) throws MessagingException {
        Assert.notNull((Object)bcc, (String)"Bcc address must not be null");
        this.validateAddress(bcc);
        this.mimeMessage.setRecipient(Message.RecipientType.BCC, (Address)bcc);
    }

    public void setBcc(InternetAddress[] bcc) throws MessagingException {
        Assert.notNull((Object)bcc, (String)"Bcc address array must not be null");
        this.validateAddresses(bcc);
        this.mimeMessage.setRecipients(Message.RecipientType.BCC, (Address[])bcc);
    }

    public void setBcc(String bcc) throws MessagingException {
        Assert.notNull((Object)bcc, (String)"Bcc address must not be null");
        this.setBcc(this.parseAddress(bcc));
    }

    public void setBcc(String[] bcc) throws MessagingException {
        Assert.notNull((Object)bcc, (String)"Bcc address array must not be null");
        InternetAddress[] addresses = new InternetAddress[bcc.length];
        for (int i = 0; i < bcc.length; ++i) {
            addresses[i] = this.parseAddress(bcc[i]);
        }
        this.setBcc(addresses);
    }

    public void addBcc(InternetAddress bcc) throws MessagingException {
        Assert.notNull((Object)bcc, (String)"Bcc address must not be null");
        this.validateAddress(bcc);
        this.mimeMessage.addRecipient(Message.RecipientType.BCC, (Address)bcc);
    }

    public void addBcc(String bcc) throws MessagingException {
        Assert.notNull((Object)bcc, (String)"Bcc address must not be null");
        this.addBcc(this.parseAddress(bcc));
    }

    public void addBcc(String bcc, String personal) throws MessagingException, UnsupportedEncodingException {
        Assert.notNull((Object)bcc, (String)"Bcc address must not be null");
        this.addBcc(this.getEncoding() != null ? new InternetAddress(bcc, personal, this.getEncoding()) : new InternetAddress(bcc, personal));
    }

    private InternetAddress parseAddress(String address) throws MessagingException {
        InternetAddress[] parsed = InternetAddress.parse((String)address);
        if (parsed.length != 1) {
            throw new AddressException("Illegal address", address);
        }
        InternetAddress raw = parsed[0];
        try {
            return this.getEncoding() != null ? new InternetAddress(raw.getAddress(), raw.getPersonal(), this.getEncoding()) : raw;
        }
        catch (UnsupportedEncodingException ex) {
            throw new MessagingException("Failed to parse embedded personal name to correct encoding", (Exception)ex);
        }
    }

    public void setPriority(int priority) throws MessagingException {
        this.mimeMessage.setHeader(HEADER_PRIORITY, Integer.toString(priority));
    }

    public void setSentDate(Date sentDate) throws MessagingException {
        Assert.notNull((Object)sentDate, (String)"Sent date must not be null");
        this.mimeMessage.setSentDate(sentDate);
    }

    public void setSubject(String subject) throws MessagingException {
        Assert.notNull((Object)subject, (String)"Subject must not be null");
        if (this.getEncoding() != null) {
            this.mimeMessage.setSubject(subject, this.getEncoding());
        } else {
            this.mimeMessage.setSubject(subject);
        }
    }

    public void setText(String text) throws MessagingException {
        this.setText(text, false);
    }

    public void setText(String text, boolean html) throws MessagingException {
        Assert.notNull((Object)text, (String)"Text must not be null");
        Object partToUse = this.isMultipart() ? this.getMainPart() : this.mimeMessage;
        if (html) {
            this.setHtmlTextToMimePart((MimePart)partToUse, text);
        } else {
            this.setPlainTextToMimePart((MimePart)partToUse, text);
        }
    }

    public void setText(String plainText, String htmlText) throws MessagingException {
        Assert.notNull((Object)plainText, (String)"Plain text must not be null");
        Assert.notNull((Object)htmlText, (String)"HTML text must not be null");
        MimeMultipart messageBody = new MimeMultipart(MULTIPART_SUBTYPE_ALTERNATIVE);
        this.getMainPart().setContent((Object)messageBody, CONTENT_TYPE_ALTERNATIVE);
        MimeBodyPart plainTextPart = new MimeBodyPart();
        this.setPlainTextToMimePart((MimePart)plainTextPart, plainText);
        messageBody.addBodyPart((BodyPart)plainTextPart);
        MimeBodyPart htmlTextPart = new MimeBodyPart();
        this.setHtmlTextToMimePart((MimePart)htmlTextPart, htmlText);
        messageBody.addBodyPart((BodyPart)htmlTextPart);
    }

    private MimeBodyPart getMainPart() throws MessagingException {
        MimeMultipart mimeMultipart = this.getMimeMultipart();
        MimeBodyPart bodyPart = null;
        for (int i = 0; i < mimeMultipart.getCount(); ++i) {
            BodyPart bp = mimeMultipart.getBodyPart(i);
            if (bp.getFileName() != null) continue;
            bodyPart = (MimeBodyPart)bp;
        }
        if (bodyPart == null) {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeMultipart.addBodyPart((BodyPart)mimeBodyPart);
            bodyPart = mimeBodyPart;
        }
        return bodyPart;
    }

    private void setPlainTextToMimePart(MimePart mimePart, String text) throws MessagingException {
        if (this.getEncoding() != null) {
            mimePart.setText(text, this.getEncoding());
        } else {
            mimePart.setText(text);
        }
    }

    private void setHtmlTextToMimePart(MimePart mimePart, String text) throws MessagingException {
        if (this.getEncoding() != null) {
            mimePart.setContent((Object)text, "text/html;charset=" + this.getEncoding());
        } else {
            mimePart.setContent((Object)text, CONTENT_TYPE_HTML);
        }
    }

    public void addInline(String contentId, DataSource dataSource) throws MessagingException {
        Assert.notNull((Object)contentId, (String)"Content ID must not be null");
        Assert.notNull((Object)dataSource, (String)"DataSource must not be null");
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDisposition("inline");
        mimeBodyPart.setContentID("<" + contentId + ">");
        mimeBodyPart.setDataHandler(new DataHandler(dataSource));
        this.getMimeMultipart().addBodyPart((BodyPart)mimeBodyPart);
    }

    public void addInline(String contentId, File file) throws MessagingException {
        Assert.notNull((Object)file, (String)"File must not be null");
        FileDataSource dataSource = new FileDataSource(file);
        dataSource.setFileTypeMap(this.getFileTypeMap());
        this.addInline(contentId, (DataSource)dataSource);
    }

    public void addInline(String contentId, Resource resource) throws MessagingException {
        Assert.notNull((Object)resource, (String)"Resource must not be null");
        String contentType = this.getFileTypeMap().getContentType(resource.getFilename());
        this.addInline(contentId, (InputStreamSource)resource, contentType);
    }

    public void addInline(String contentId, InputStreamSource inputStreamSource, String contentType) throws MessagingException {
        Assert.notNull((Object)inputStreamSource, (String)"InputStreamSource must not be null");
        if (inputStreamSource instanceof Resource && ((Resource)inputStreamSource).isOpen()) {
            throw new IllegalArgumentException("Passed-in Resource contains an open stream: invalid argument. JavaMail requires an InputStreamSource that creates a fresh stream for every call.");
        }
        DataSource dataSource = this.createDataSource(inputStreamSource, contentType, "inline");
        this.addInline(contentId, dataSource);
    }

    public void addAttachment(String attachmentFilename, DataSource dataSource) throws MessagingException {
        Assert.notNull((Object)attachmentFilename, (String)"Attachment filename must not be null");
        Assert.notNull((Object)dataSource, (String)"DataSource must not be null");
        try {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setDisposition("attachment");
            mimeBodyPart.setFileName(this.isEncodeFilenames() ? MimeUtility.encodeText((String)attachmentFilename) : attachmentFilename);
            mimeBodyPart.setDataHandler(new DataHandler(dataSource));
            this.getRootMimeMultipart().addBodyPart((BodyPart)mimeBodyPart);
        }
        catch (UnsupportedEncodingException ex) {
            throw new MessagingException("Failed to encode attachment filename", (Exception)ex);
        }
    }

    public void addAttachment(String attachmentFilename, File file) throws MessagingException {
        Assert.notNull((Object)file, (String)"File must not be null");
        FileDataSource dataSource = new FileDataSource(file);
        dataSource.setFileTypeMap(this.getFileTypeMap());
        this.addAttachment(attachmentFilename, (DataSource)dataSource);
    }

    public void addAttachment(String attachmentFilename, InputStreamSource inputStreamSource) throws MessagingException {
        String contentType = this.getFileTypeMap().getContentType(attachmentFilename);
        this.addAttachment(attachmentFilename, inputStreamSource, contentType);
    }

    public void addAttachment(String attachmentFilename, InputStreamSource inputStreamSource, String contentType) throws MessagingException {
        Assert.notNull((Object)inputStreamSource, (String)"InputStreamSource must not be null");
        if (inputStreamSource instanceof Resource && ((Resource)inputStreamSource).isOpen()) {
            throw new IllegalArgumentException("Passed-in Resource contains an open stream: invalid argument. JavaMail requires an InputStreamSource that creates a fresh stream for every call.");
        }
        DataSource dataSource = this.createDataSource(inputStreamSource, contentType, attachmentFilename);
        this.addAttachment(attachmentFilename, dataSource);
    }

    protected DataSource createDataSource(final InputStreamSource inputStreamSource, final String contentType, final String name) {
        return new DataSource(){

            public InputStream getInputStream() throws IOException {
                return inputStreamSource.getInputStream();
            }

            public OutputStream getOutputStream() {
                throw new UnsupportedOperationException("Read-only javax.activation.DataSource");
            }

            public String getContentType() {
                return contentType;
            }

            public String getName() {
                return name;
            }
        };
    }
}

