/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.MailFactory
 *  com.atlassian.mail.Settings
 *  com.atlassian.mail.queue.MailQueueItem
 *  com.atlassian.mail.server.SMTPMailServer
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.mail.BodyPart
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMultipart
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.template;

import com.atlassian.confluence.mail.template.MultipartBuilder;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.Settings;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.mail.server.SMTPMailServer;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceMailQueueItem
implements MailQueueItem {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceMailQueueItem.class);
    public static final String MIME_TYPE_HTML = "text/html";
    public static final String MIME_TYPE_TEXT = "text/plain";
    private final String toAddress;
    private final String ccAddress;
    private String fromAddress;
    private String fromName;
    private final String subject;
    private final String body;
    private final String mimeType;
    private final Collection<DataSource> attachedImages;
    private final Date dateQueued = new Date();
    private boolean sendLogs;
    private String logsLocation;
    private boolean hasError;
    private String lastError;
    private int sendCount;

    public void setSendLogs(boolean sendLogs) {
        this.sendLogs = sendLogs;
    }

    public void setLogsLocation(String logsLocation) {
        this.logsLocation = logsLocation;
    }

    public ConfluenceMailQueueItem(String toAddress, String subject, String body, String mimeType) {
        this(toAddress, null, subject, body, mimeType, null);
    }

    public ConfluenceMailQueueItem(String toAddress, String ccAddress, String subject, String body, String mimeType) {
        this(toAddress, ccAddress, subject, body, mimeType, null);
    }

    public ConfluenceMailQueueItem(String toAddress, String ccAddress, String subject, String body, String mimeType, Collection<DataSource> attachedImages) {
        this.toAddress = toAddress;
        this.ccAddress = ccAddress;
        this.subject = subject;
        this.body = body;
        this.mimeType = mimeType;
        this.attachedImages = attachedImages;
    }

    public int compareTo(MailQueueItem o) {
        return this.sendCount - o.getSendCount();
    }

    @Deprecated
    public void send() throws MailException {
        this.send(MailFactory.getServerManager().getDefaultSMTPMailServer(), MailFactory.getSettings());
    }

    void send(SMTPMailServer smtpServer, Settings settings) throws MailException {
        ++this.sendCount;
        Email email = new Email(this.toAddress);
        email.setSubject(this.subject);
        email.setBody(this.body);
        email.setMimeType(this.mimeType);
        email.setFrom(this.fromAddress);
        email.setFromName(this.fromName);
        email.setMultipart(this.getMultipart());
        if (this.ccAddress != null) {
            email.setCc(this.ccAddress);
        }
        if (smtpServer == null) {
            this.lastError = "Unable to send email since no mail server has been configured.";
            this.hasError = true;
            log.warn(this.lastError);
            return;
        }
        if (settings.isSendingDisabled()) {
            log.info("Not sending email because sending is disabled via system property.");
            return;
        }
        if (this.sendLogs) {
            try {
                File file = new File(this.logsLocation);
                email.setMultipart((Multipart)MultipartBuilder.INSTANCE.makeMultipart(file));
            }
            catch (IOException | MessagingException e) {
                log.error("Unable to attach log files for message : " + this.subject, e);
            }
        }
        try {
            smtpServer.send(email);
            this.hasError = false;
        }
        catch (MailException me) {
            log.error("Unable to send email with subject '" + this.subject + "' to <" + this.toAddress + "> reason: " + me.getMessage(), (Throwable)me);
            this.hasError = true;
            this.lastError = me.getMessage();
            throw me;
        }
    }

    private Multipart getMultipart() {
        if (this.attachedImages == null || this.attachedImages.isEmpty()) {
            return null;
        }
        MimeMultipart multipart = new MimeMultipart("related");
        for (DataSource dataSource : this.attachedImages) {
            try {
                String contentId = dataSource.getName();
                multipart.addBodyPart((BodyPart)this.createMimeBodyPart(contentId, new DataHandler(dataSource), contentId));
            }
            catch (MessagingException e) {
                log.error("Could not create multipart attachment for email: " + this.subject, (Throwable)e);
            }
        }
        return multipart;
    }

    private MimeBodyPart createMimeBodyPart(String contentId, DataHandler dataHandler, String fileName) throws MessagingException {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDataHandler(dataHandler);
        mimeBodyPart.setHeader("Content-ID", "<" + contentId + ">");
        if (StringUtils.isNotBlank((CharSequence)fileName)) {
            mimeBodyPart.setFileName(fileName);
        }
        return mimeBodyPart;
    }

    public String getSubject() {
        return this.subject;
    }

    public Date getDateQueued() {
        return this.dateQueued;
    }

    public int getSendCount() {
        return this.sendCount;
    }

    public boolean hasError() {
        return this.hasError;
    }

    public String getLastError() {
        return this.lastError;
    }

    public String getBody() {
        return this.body;
    }

    public String getFromAddress() {
        return this.fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromName() {
        return this.fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
}

