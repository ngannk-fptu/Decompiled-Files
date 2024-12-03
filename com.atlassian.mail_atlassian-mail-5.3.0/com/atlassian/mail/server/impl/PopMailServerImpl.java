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
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.auth.AuthenticationContext;
import java.util.Properties;
import javax.mail.Session;

public class PopMailServerImpl
extends AbstractMailServer
implements PopMailServer {
    private static final long serialVersionUID = 8048100989596174663L;

    public PopMailServerImpl() {
    }

    public PopMailServerImpl(Long id, String name, String description, String serverName, String username, String password) {
        this(id, name, description, MailConstants.DEFAULT_POP_PROTOCOL, serverName, "110", username, password, 10000L);
    }

    public PopMailServerImpl(Long id, String name, String description, MailProtocol popProtocol, String serverName, String popPort, String username, String password) {
        this(id, name, description, popProtocol, serverName, popPort, username, password, 10000L);
    }

    public PopMailServerImpl(Long id, String name, String description, MailProtocol popProtocol, String serverName, String popPort, String username, String password, long timeout) {
        super(id, name, description, popProtocol, serverName, popPort, username, password, timeout, null, null);
    }

    public PopMailServerImpl(Long id, String name, String description, MailProtocol popProtocol, String serverName, String popPort, String username, String password, long timeout, String socksHost, String socksPort) {
        super(id, name, description, popProtocol, serverName, popPort, username, password, timeout, socksHost, socksPort);
    }

    public PopMailServerImpl(Long id, String name, String description, String serverName, AuthenticationContext authenticator) {
        this(id, name, description, MailConstants.DEFAULT_POP_PROTOCOL, serverName, "110", authenticator, 10000L);
    }

    public PopMailServerImpl(Long id, String name, String description, MailProtocol popProtocol, String serverName, String popPort, AuthenticationContext authenticator) {
        this(id, name, description, popProtocol, serverName, popPort, authenticator, 10000L);
    }

    public PopMailServerImpl(Long id, String name, String description, MailProtocol popProtocol, String serverName, String popPort, AuthenticationContext authenticator, long timeout) {
        super(id, name, description, popProtocol, serverName, popPort, authenticator, timeout, null, null);
    }

    public PopMailServerImpl(Long id, String name, String description, MailProtocol popProtocol, String serverName, String popPort, AuthenticationContext authenticator, long timeout, String socksHost, String socksPort) {
        super(id, name, description, popProtocol, serverName, popPort, authenticator, timeout, socksHost, socksPort);
    }

    @Override
    public String getType() {
        return MailServerManager.SERVER_TYPES[0];
    }

    @Override
    public Session getSession() throws MailException {
        Properties props = this.loadSystemProperties(this.getProperties());
        return this.getSessionFromServerManager(props, this.getAuthenticator());
    }
}

