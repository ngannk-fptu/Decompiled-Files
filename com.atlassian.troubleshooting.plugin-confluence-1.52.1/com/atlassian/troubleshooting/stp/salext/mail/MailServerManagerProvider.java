/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.MailFactory
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.mail.server.PopMailServer
 *  com.atlassian.mail.server.SMTPMailServer
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.stp.salext.mail;

import com.atlassian.mail.MailFactory;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import javax.annotation.Nullable;

public class MailServerManagerProvider {
    public MailServerManager getServerManager() {
        return MailFactory.getServerManager();
    }

    @Nullable
    public SMTPMailServer getDefaultSMTPMailServer() {
        return this.getServerManager().getDefaultSMTPMailServer();
    }

    @Nullable
    public PopMailServer getDefaultPopMailServer() {
        return this.getServerManager().getDefaultPopMailServer();
    }
}

