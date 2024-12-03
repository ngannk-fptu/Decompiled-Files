/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.MailConstants
 *  com.atlassian.mail.MailProtocol
 *  com.atlassian.mail.server.MailServer
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.mail;

import com.atlassian.confluence.jmx.JmxSMTPMailServer;
import com.atlassian.confluence.mail.ConfluenceImapMailServer;
import com.atlassian.confluence.mail.ConfluencePopMailServer;
import com.atlassian.confluence.security.InvalidOperationException;
import com.atlassian.mail.MailConstants;
import com.atlassian.mail.MailProtocol;
import com.atlassian.mail.server.MailServer;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConfluenceMailServerBuilder {
    private Long id;
    private String name;
    private MailProtocol protocol;
    private String hostName;
    private String port;
    private String username;
    private String password;
    private String emailAddress;
    private String jndiName;
    private String prefix;
    private String fromName;
    private boolean tlsRequired;

    public ConfluenceMailServerBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public ConfluenceMailServerBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ConfluenceMailServerBuilder mailProtocol(MailProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public ConfluenceMailServerBuilder hostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public ConfluenceMailServerBuilder port(String port) {
        this.port = port;
        return this;
    }

    public ConfluenceMailServerBuilder username(String username) {
        this.username = username;
        return this;
    }

    public ConfluenceMailServerBuilder password(String password) {
        this.password = password;
        return this;
    }

    public ConfluenceMailServerBuilder emailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public ConfluenceMailServerBuilder jndiName(String jndiName) {
        this.jndiName = jndiName;
        return this;
    }

    public ConfluenceMailServerBuilder prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public ConfluenceMailServerBuilder fromName(String fromName) {
        this.fromName = fromName;
        return this;
    }

    public ConfluenceMailServerBuilder tlsRequired(boolean tlsRequired) {
        this.tlsRequired = tlsRequired;
        return this;
    }

    public static ConfluenceMailServerBuilder builder() {
        return new ConfluenceMailServerBuilder();
    }

    public @NonNull MailServer buildMailServer() {
        if (this.id == null) {
            this.id = 0L;
        }
        switch (this.protocol) {
            case IMAP: 
            case SECURE_IMAP: {
                return new ConfluenceImapMailServer(this.id, this.name, "", this.protocol, this.hostName, this.port, this.username, this.password, this.emailAddress);
            }
            case POP: 
            case SECURE_POP: {
                return new ConfluencePopMailServer(this.id, this.name, "", this.protocol, this.hostName, this.port, this.username, this.password, this.emailAddress);
            }
            case SMTP: {
                return this.buildOutboundServer(this.jndiName, this.prefix, this.fromName, this.tlsRequired);
            }
        }
        throw new InvalidOperationException("Unsupported mail protocol: " + this.protocol);
    }

    public JmxSMTPMailServer buildOutboundServer(String jndiName, String prefix, String fromName, boolean tlsRequired) {
        if (this.id == null) {
            this.id = 0L;
        }
        if (this.protocol == null) {
            this.protocol = MailProtocol.SECURE_SMTP.getDefaultPort().equals(this.port) ? MailProtocol.SECURE_SMTP : MailConstants.DEFAULT_SMTP_PROTOCOL;
        }
        JmxSMTPMailServer mailServer = new JmxSMTPMailServer();
        mailServer.setId(this.id);
        mailServer.setName(this.name);
        mailServer.setMailProtocol(this.protocol);
        boolean hasJndi = StringUtils.isNotEmpty((CharSequence)jndiName);
        mailServer.setDefaultFrom(this.emailAddress);
        mailServer.setPrefix(prefix);
        mailServer.setSessionServer(hasJndi);
        if (hasJndi) {
            mailServer.setJndiLocation(jndiName);
        }
        mailServer.setUsername(this.username);
        mailServer.setPassword(this.password);
        mailServer.setFromName(fromName);
        if (this.hostName != null) {
            mailServer.setHostname(this.hostName);
            if (this.port != null) {
                mailServer.setPort(this.port);
            } else {
                mailServer.setPort(this.protocol.getDefaultPort());
            }
        }
        mailServer.setTimeout(10000L);
        mailServer.getProperties().setProperty("mail.smtp.connectiontimeout", String.valueOf(10000L));
        mailServer.setTlsRequired(tlsRequired);
        return mailServer;
    }
}

