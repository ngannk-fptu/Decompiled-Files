/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.activation.FileDataSource
 *  javax.mail.BodyPart
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMultipart
 *  javax.mail.util.ByteArrayDataSource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext.mail;

import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.AbstractSupportMailQueueItem;
import com.atlassian.troubleshooting.stp.salext.mail.ProductAwareEmail;
import com.atlassian.troubleshooting.stp.salext.mail.SupportRequest;
import com.atlassian.troubleshooting.stp.salext.mail.SupportRequestAttachment;
import java.io.File;
import java.util.Map;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SupportRequestMailQueueItem
extends AbstractSupportMailQueueItem {
    private static final Logger LOG = LoggerFactory.getLogger(SupportRequestMailQueueItem.class);
    private final String applicationName;
    private final SupportRequest supportRequest;

    public SupportRequestMailQueueItem(SupportRequest supportRequest, SupportApplicationInfo info) {
        this.applicationName = info.getApplicationName();
        this.supportRequest = supportRequest;
    }

    public static Multipart toMultiPart(SupportRequest supportRequest) throws MailException {
        MimeMultipart bodyMimeMultipart = new MimeMultipart();
        try {
            if (supportRequest.getBody() != null) {
                MimeBodyPart textContent = new MimeBodyPart();
                textContent.setText(supportRequest.getBody());
                bodyMimeMultipart.addBodyPart((BodyPart)textContent);
            }
            for (SupportRequestAttachment attachment : supportRequest.getAttachments()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                if (attachment.getData() instanceof byte[]) {
                    ByteArrayDataSource fds = new ByteArrayDataSource((byte[])attachment.getData(), attachment.getType());
                    attachmentPart.setDataHandler(new DataHandler((DataSource)fds));
                } else if (attachment.getData() instanceof File) {
                    FileDataSource dataSource = new FileDataSource((File)attachment.getData());
                    attachmentPart.setDataHandler(new DataHandler((DataSource)dataSource));
                } else if (attachment.getData() instanceof String) {
                    attachmentPart.setText((String)((Object)attachment.getData()));
                } else {
                    LOG.error("Unrecognized attachment type: {}", (Object)attachment.getData().getClass().getName());
                }
                attachmentPart.setFileName(attachment.getName());
                LOG.debug("Adding attachment {}", (Object)attachmentPart.getFileName());
                bodyMimeMultipart.addBodyPart((BodyPart)attachmentPart);
            }
        }
        catch (MessagingException e) {
            throw new MailException(e.getMessage(), (Throwable)e);
        }
        return bodyMimeMultipart;
    }

    public SupportRequest getSupportRequest() {
        return this.supportRequest;
    }

    public void send() throws MailException {
        Email email = new ProductAwareEmail(this.supportRequest.getToAddress()).addProductHeader(this.applicationName).setFrom(this.supportRequest.getFromAddress()).setSubject(this.supportRequest.getSubject());
        for (Map.Entry<String, String> entry : this.supportRequest.getHeaders()) {
            email.addHeader(entry.getKey(), entry.getValue());
        }
        Multipart bodyMimeMultipart = SupportRequestMailQueueItem.toMultiPart(this.supportRequest);
        email.setMultipart(bodyMimeMultipart);
        this.send(email);
    }

    public String getSubject() {
        return this.supportRequest.getSubject();
    }
}

