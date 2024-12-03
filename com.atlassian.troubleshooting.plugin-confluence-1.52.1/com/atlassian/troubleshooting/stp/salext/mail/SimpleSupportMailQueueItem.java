/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 */
package com.atlassian.troubleshooting.stp.salext.mail;

import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.troubleshooting.stp.salext.mail.AbstractSupportMailQueueItem;

public class SimpleSupportMailQueueItem
extends AbstractSupportMailQueueItem {
    private final Email email;

    public SimpleSupportMailQueueItem(Email email) {
        this.email = email;
    }

    public void send() throws MailException {
        this.send(this.email);
    }

    public String getSubject() {
        return this.email.getSubject();
    }
}

