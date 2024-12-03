/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 */
package com.atlassian.confluence.mail.template;

import com.atlassian.confluence.mail.template.AbstractMailNotificationQueueItem;
import com.atlassian.mail.Email;

public class NonUserMailNotificationQueueItem
extends AbstractMailNotificationQueueItem {
    private String email;
    private String mimeType = "text/plain";

    public NonUserMailNotificationQueueItem(String email, String templateLocation, String templateFileName, String subject, String mimeType) {
        super(templateLocation, templateFileName);
        if (email == null) {
            throw new IllegalArgumentException("The email address was null for mail notification item with subject " + subject);
        }
        if (!NonUserMailNotificationQueueItem.isRecognisedMimeType(mimeType)) {
            throw new IllegalArgumentException("Unrecognized mimetype: " + mimeType);
        }
        this.setSubject(subject);
        this.email = email;
        this.mimeType = mimeType;
    }

    @Override
    protected Email createMailObject() {
        String messageBody = this.getRenderedContent();
        Email mail = new Email(this.email);
        mail.setEncoding("UTF-8");
        mail.setSubject(this.getSubject());
        mail.setBody(messageBody);
        mail.setMimeType(this.mimeType);
        this.setLastError(null);
        return mail;
    }
}

