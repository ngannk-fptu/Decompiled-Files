/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.mail.Authenticator
 *  javax.mail.Session
 */
package com.atlassian.mail.server;

import com.atlassian.mail.MailException;
import com.atlassian.mail.server.ImapMailServer;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.MailServerConfigurationHandler;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Nullable;
import javax.mail.Authenticator;
import javax.mail.Session;

public interface MailServerManager {
    public static final String[] SERVER_TYPES = new String[]{"pop", "smtp", "imap"};

    @Nullable
    public MailServer getMailServer(Long var1) throws MailException;

    @Nullable
    public MailServer getMailServer(String var1) throws MailException;

    public Long create(MailServer var1) throws MailException;

    public void update(MailServer var1) throws MailException;

    public void delete(Long var1) throws MailException;

    public List<String> getServerNames() throws MailException;

    public List<SMTPMailServer> getSmtpMailServers();

    public List<PopMailServer> getPopMailServers();

    public List<ImapMailServer> getImapMailServers();

    @Nullable
    public SMTPMailServer getDefaultSMTPMailServer();

    public boolean isDefaultSMTPMailServerDefined();

    @Nullable
    public PopMailServer getDefaultPopMailServer();

    public Session getSession(Properties var1, @Nullable Authenticator var2) throws MailException;

    public void init(Map var1);

    public void setMailServerConfigurationHandler(@Nullable MailServerConfigurationHandler var1);
}

