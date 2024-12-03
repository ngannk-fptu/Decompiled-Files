/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.MailProtocol
 *  com.atlassian.mail.server.impl.SMTPMailServerImpl
 */
package com.atlassian.confluence.jmx;

import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailProtocol;
import com.atlassian.mail.server.impl.SMTPMailServerImpl;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class JmxSMTPMailServer
extends SMTPMailServerImpl {
    private AtomicInteger emailsSent = new AtomicInteger(0);
    private AtomicInteger emailsAttempted = new AtomicInteger(0);
    private volatile Date lastSuccessful = null;
    private String fromName;

    public JmxSMTPMailServer() {
    }

    public JmxSMTPMailServer(Long id, String name, String description, String from, String prefix, boolean isSession, String location, String username, String password) {
        super(id, name, description, from, prefix, isSession, location, username, password);
    }

    public JmxSMTPMailServer(Long id, String name, String description, String from, String prefix, boolean isSession, String location, String username, String password, String smtpPort) {
        super(id, name, description, from, prefix, isSession, location, username, password);
        this.setPort(smtpPort);
    }

    public JmxSMTPMailServer(Long id, String name, String description, String from, String prefix, boolean isSession, String location, String username, String password, String smtpPort, String fromName) {
        super(id, name, description, from, prefix, isSession, location, username, password);
        this.fromName = fromName;
        this.setPort(smtpPort);
    }

    public JmxSMTPMailServer(Long id, String name, String description, String from, String prefix, boolean isSession, boolean removePrecedence, MailProtocol mailProtocol, String location, String smtpPort, boolean tlsRequired, String username, String password, long timeout) {
        super(id, name, description, from, prefix, isSession, removePrecedence, mailProtocol, location, smtpPort, tlsRequired, username, password, timeout);
    }

    public void quietSend(Email email) throws MailException {
        this.emailsAttempted.incrementAndGet();
        super.quietSend(email);
        this.lastSuccessful = new Date();
        this.emailsSent.incrementAndGet();
    }

    public void send(Email email) throws MailException {
        this.emailsAttempted.incrementAndGet();
        super.sendWithMessageId(email, email.getMessageId());
        this.lastSuccessful = new Date();
        this.emailsSent.incrementAndGet();
    }

    public int getEmailsSent() {
        return this.emailsSent.intValue();
    }

    public int getEmailsAttempted() {
        return this.emailsAttempted.intValue();
    }

    public Date getLastSuccessful() {
        return this.lastSuccessful;
    }

    public String getFromName() {
        if (this.fromName == null) {
            this.fromName = "${fullname} (Confluence)";
        }
        return this.fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
}

