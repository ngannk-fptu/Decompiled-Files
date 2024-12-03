/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailFactory
 *  com.atlassian.mail.queue.MailQueueItem
 *  com.atlassian.mail.queue.SingleMailQueueItem
 *  javax.mail.Multipart
 */
package com.atlassian.upm.mail;

import com.atlassian.mail.Email;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.mail.queue.SingleMailQueueItem;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.UpmEmail;
import java.util.Set;
import javax.mail.Multipart;

public abstract class AbstractAtlassianMailService
implements ProductMailService {
    private static final String EMAIL_SEPARATOR = ", ";

    @Override
    public boolean isConfigured() {
        return MailFactory.getServerManager().getDefaultSMTPMailServer() != null;
    }

    @Override
    public boolean isDisabled() {
        return MailFactory.getSettings().isSendingDisabled();
    }

    protected MailQueueItem createMailQueueItem(UpmEmail upmEmail) {
        return new SingleMailQueueItem(this.toEmail(upmEmail));
    }

    protected Email toEmail(UpmEmail upmEmail) {
        Email email = new Email(AbstractAtlassianMailService.join(upmEmail.getTo()), AbstractAtlassianMailService.join(upmEmail.getCc()), AbstractAtlassianMailService.join(upmEmail.getBcc()));
        email.setSubject(upmEmail.getSubject());
        email.setBody(upmEmail.getBody());
        for (String s : upmEmail.getFrom()) {
            email.setFrom(s);
        }
        for (String s : upmEmail.getFromName()) {
            email.setFromName(s);
        }
        if (!upmEmail.getReplyTo().isEmpty()) {
            email.setReplyTo(AbstractAtlassianMailService.join(upmEmail.getReplyTo()));
        }
        for (String s : upmEmail.getInReplyTo()) {
            email.setInReplyTo(s);
        }
        for (String s : upmEmail.getMimeType()) {
            email.setMimeType(s);
        }
        for (String s : upmEmail.getEncoding()) {
            email.setEncoding(s);
        }
        for (String s : upmEmail.getMessageId()) {
            email.setMessageId(s);
        }
        for (Multipart m : upmEmail.getMultipart()) {
            email.setMultipart(m);
        }
        for (String key : upmEmail.getHeaders().keySet()) {
            email.addHeader(key, upmEmail.getHeaders().get(key));
        }
        return email;
    }

    private static String join(Set<String> emails) {
        return String.join((CharSequence)EMAIL_SEPARATOR, emails);
    }
}

