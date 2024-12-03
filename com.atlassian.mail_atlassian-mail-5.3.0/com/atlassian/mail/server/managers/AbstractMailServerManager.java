/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.mail.Authenticator
 *  javax.mail.Session
 */
package com.atlassian.mail.server.managers;

import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.MailServerConfigurationHandler;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Nullable;
import javax.mail.Authenticator;
import javax.mail.Session;

public abstract class AbstractMailServerManager
implements MailServerManager {
    private MailServerConfigurationHandler mailServerConfigurationHandler;

    @Override
    public void init(Map params) {
    }

    @Override
    @Nullable
    public abstract MailServer getMailServer(Long var1) throws MailException;

    @Override
    @Nullable
    public abstract MailServer getMailServer(String var1) throws MailException;

    @Override
    public abstract List<String> getServerNames() throws MailException;

    @Override
    public abstract List<SMTPMailServer> getSmtpMailServers();

    @Override
    public abstract List<PopMailServer> getPopMailServers();

    @Override
    public abstract Long create(MailServer var1) throws MailException;

    @Override
    public abstract void update(MailServer var1) throws MailException;

    @Override
    public abstract void delete(Long var1) throws MailException;

    @Override
    @Nullable
    public abstract SMTPMailServer getDefaultSMTPMailServer();

    @Override
    public boolean isDefaultSMTPMailServerDefined() {
        return this.getDefaultSMTPMailServer() != null;
    }

    @Override
    @Nullable
    public abstract PopMailServer getDefaultPopMailServer();

    @Override
    public Session getSession(Properties props, Authenticator auth) {
        return Session.getInstance((Properties)props, (Authenticator)auth);
    }

    @Override
    public synchronized void setMailServerConfigurationHandler(@Nullable MailServerConfigurationHandler mailServerConfigurationHandler) {
        this.mailServerConfigurationHandler = mailServerConfigurationHandler;
    }

    protected synchronized MailServerConfigurationHandler getMailServerConfigurationHandler() {
        return this.mailServerConfigurationHandler;
    }
}

