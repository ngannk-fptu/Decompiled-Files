/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Session
 */
package com.atlassian.mail.server.impl;

import com.atlassian.mail.MailConstants;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailProtocol;
import com.atlassian.mail.server.AbstractMailServer;
import com.atlassian.mail.server.ImapMailServer;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.auth.AuthenticationContext;
import java.util.Properties;
import javax.mail.Session;

public class ImapMailServerImpl
extends AbstractMailServer
implements ImapMailServer {
    private static final long serialVersionUID = -425764558074475005L;

    public ImapMailServerImpl() {
    }

    public ImapMailServerImpl(Long id, String name, String description, String serverName, String username, String password) {
        this(id, name, description, MailConstants.DEFAULT_IMAP_PROTOCOL, serverName, MailConstants.DEFAULT_IMAP_PORT, username, password, 10000L);
    }

    public ImapMailServerImpl(Long id, String name, String description, MailProtocol protocol, String serverName, String port, String username, String password) {
        this(id, name, description, protocol, serverName, port, username, password, 10000L);
    }

    public ImapMailServerImpl(Long id, String name, String description, MailProtocol protocol, String serverName, String port, String username, String password, long timeout) {
        super(id, name, description, protocol, serverName, port, username, password, timeout, null, null);
    }

    public ImapMailServerImpl(Long id, String name, String description, MailProtocol protocol, String serverName, String port, String username, String password, long timeout, String socksHost, String socksPort) {
        super(id, name, description, protocol, serverName, port, username, password, timeout, socksHost, socksPort);
    }

    public ImapMailServerImpl(Long id, String name, String description, String serverName, AuthenticationContext authenticator) {
        this(id, name, description, MailConstants.DEFAULT_IMAP_PROTOCOL, serverName, MailConstants.DEFAULT_IMAP_PORT, authenticator, 10000L);
    }

    public ImapMailServerImpl(Long id, String name, String description, MailProtocol protocol, String serverName, String port, AuthenticationContext authenticator) {
        this(id, name, description, protocol, serverName, port, authenticator, 10000L);
    }

    public ImapMailServerImpl(Long id, String name, String description, MailProtocol protocol, String serverName, String port, AuthenticationContext authenticator, long timeout) {
        super(id, name, description, protocol, serverName, port, authenticator, timeout, null, null);
    }

    public ImapMailServerImpl(Long id, String name, String description, MailProtocol protocol, String serverName, String port, AuthenticationContext authenticator, long timeout, String socksHost, String socksPort) {
        super(id, name, description, protocol, serverName, port, authenticator, timeout, socksHost, socksPort);
    }

    @Override
    public String getType() {
        return MailServerManager.SERVER_TYPES[2];
    }

    @Override
    public Session getSession() throws MailException {
        Properties props = this.loadSystemProperties(this.getProperties());
        return this.getSessionFromServerManager(props, this.getAuthenticator());
    }
}

