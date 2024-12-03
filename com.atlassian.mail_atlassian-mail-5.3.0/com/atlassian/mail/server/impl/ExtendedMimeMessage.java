/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.MessagingException
 *  javax.mail.Session
 *  javax.mail.internet.MimeMessage
 */
package com.atlassian.mail.server.impl;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class ExtendedMimeMessage
extends MimeMessage {
    private final String customMessageId;

    public ExtendedMimeMessage(Session session, String customMessageId) {
        super(session);
        this.customMessageId = customMessageId;
    }

    protected void updateMessageID() throws MessagingException {
        if (this.customMessageId == null || this.customMessageId.trim().length() == 0) {
            super.updateMessageID();
        } else {
            this.setHeader("Message-ID", "<" + this.customMessageId + ">");
        }
    }
}

