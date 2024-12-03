/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.mail.SMTPServer
 */
package com.atlassian.crowd.manager.mail;

import com.atlassian.crowd.util.mail.SMTPServer;
import java.util.List;

public class MailConfiguration {
    private final SMTPServer smtpServer;
    private final List<String> notificationEmails;

    public MailConfiguration(Builder builder) {
        this.smtpServer = builder.smtpServer;
        this.notificationEmails = builder.notificationEmails;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(MailConfiguration mailServerModel) {
        return new Builder(mailServerModel);
    }

    public SMTPServer getSmtpServer() {
        return this.smtpServer;
    }

    public List<String> getNotificationEmails() {
        return this.notificationEmails;
    }

    public static final class Builder {
        private SMTPServer smtpServer;
        private List<String> notificationEmails;

        private Builder() {
        }

        private Builder(MailConfiguration mailServerModel) {
            this.smtpServer = mailServerModel.getSmtpServer();
            this.notificationEmails = mailServerModel.getNotificationEmails();
        }

        public Builder setSmtpServer(SMTPServer smtpServer) {
            this.smtpServer = smtpServer;
            return this;
        }

        public Builder setNotificationEmails(List<String> serverAlertAddress) {
            this.notificationEmails = serverAlertAddress;
            return this;
        }

        public MailConfiguration build() {
            return new MailConfiguration(this);
        }
    }
}

