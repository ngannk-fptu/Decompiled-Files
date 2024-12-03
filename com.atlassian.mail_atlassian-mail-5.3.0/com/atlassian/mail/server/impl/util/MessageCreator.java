/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Address
 *  javax.mail.BodyPart
 *  javax.mail.Message$RecipientType
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.mail.server.impl.util;

import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailUtils;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Map;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;

public class MessageCreator {
    public void updateMimeMessage(Email email, String defaultFrom, String prefix, MimeMessage message) throws MailException, MessagingException, UnsupportedEncodingException {
        String from = StringUtils.trim((String)email.getFrom());
        String fromName = email.getFromName();
        String to = email.getTo();
        String cc = email.getCc();
        String bcc = email.getBcc();
        String replyTo = email.getReplyTo();
        String inReplyTo = email.getInReplyTo();
        String subject = email.getSubject();
        String body = email.getBody();
        String mimeType = email.getMimeType();
        String encoding = email.getEncoding();
        Map headers = email.getHeaders();
        Multipart multipart = email.getMultipart();
        if (StringUtils.isBlank((CharSequence)StringUtils.trim((String)to)) && StringUtils.isBlank((CharSequence)StringUtils.trim((String)cc)) && StringUtils.isBlank((CharSequence)StringUtils.trim((String)bcc))) {
            throw new MailException("Tried to send mail (" + subject + ") with no recipients.");
        }
        message.setSentDate(Calendar.getInstance().getTime());
        if (to != null) {
            message.setRecipients(Message.RecipientType.TO, (Address[])MailUtils.parseAddresses(to));
        }
        if (cc != null) {
            message.setRecipients(Message.RecipientType.CC, (Address[])MailUtils.parseAddresses(cc));
        }
        if (bcc != null) {
            message.setRecipients(Message.RecipientType.BCC, (Address[])MailUtils.parseAddresses(bcc));
        }
        if (replyTo != null) {
            message.setReplyTo((Address[])MailUtils.parseAddresses(replyTo));
        }
        if (inReplyTo != null) {
            message.setHeader("In-Reply-To", inReplyTo);
        }
        if (StringUtils.isNotBlank((CharSequence)from)) {
            message.setFrom((Address)this.buildFromFieldAsInternetAddress(from, fromName, encoding));
        } else if (StringUtils.isNotBlank((CharSequence)defaultFrom)) {
            message.setFrom((Address)this.buildFromFieldAsInternetAddress(defaultFrom, fromName, encoding));
        } else {
            throw new MailException("Tried to send mail (" + subject + ") from no one (no 'from' and 'default from' specified).");
        }
        String fullSubject = subject;
        if (this.shouldIncludeSubjectPrefix(prefix, email)) {
            fullSubject = prefix + " " + fullSubject;
        }
        if (encoding != null) {
            message.setSubject(fullSubject, encoding);
        } else {
            message.setSubject(fullSubject);
        }
        String mimeTypeAndEncoding = mimeType;
        if (encoding != null) {
            mimeTypeAndEncoding = mimeTypeAndEncoding + "; charset=" + encoding + "";
        }
        if (multipart != null) {
            if (StringUtils.isNotBlank((CharSequence)body)) {
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent((Object)body, mimeTypeAndEncoding);
                messageBodyPart.setDisposition("inline");
                multipart.addBodyPart((BodyPart)messageBodyPart, 0);
            }
            message.setContent(multipart);
        } else {
            message.setContent((Object)body, mimeTypeAndEncoding);
        }
        if (headers != null) {
            for (Map.Entry entry : headers.entrySet()) {
                message.addHeader((String)entry.getKey(), (String)entry.getValue());
            }
        }
    }

    private boolean shouldIncludeSubjectPrefix(String prefix, Email email) {
        return !email.isExcludeSubjectPrefix() && StringUtils.isNotBlank((CharSequence)prefix);
    }

    private InternetAddress buildFromFieldAsInternetAddress(String fromAddress, String fromName, String encoding) throws MailException, MessagingException, UnsupportedEncodingException {
        InternetAddress internetAddress = new InternetAddress(fromAddress);
        if (StringUtils.isNotBlank((CharSequence)fromName) && internetAddress.getPersonal() == null) {
            if (encoding != null) {
                internetAddress.setPersonal(fromName, encoding);
            } else {
                internetAddress.setPersonal(fromName);
            }
        }
        return internetAddress;
    }
}

