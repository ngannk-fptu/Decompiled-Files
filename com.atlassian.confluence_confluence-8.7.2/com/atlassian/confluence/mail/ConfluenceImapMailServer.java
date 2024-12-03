/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.MailProtocol
 *  com.atlassian.mail.server.impl.ImapMailServerImpl
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.mail;

import com.atlassian.confluence.mail.Authorization;
import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.mail.MailProtocol;
import com.atlassian.mail.server.impl.ImapMailServerImpl;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConfluenceImapMailServer
extends ImapMailServerImpl
implements InboundMailServer {
    private static final long serialVersionUID = 4906913569166210935L;
    private static final long TIMEOUT = 10000L;
    private String toAddress;
    private Authorization authorization;

    private ConfluenceImapMailServer() {
    }

    public ConfluenceImapMailServer(Long id, String name, String description, MailProtocol protocol, String hostname, String port, String username, String password, String toAddress) {
        super(id, name, description, protocol, hostname, port, username, password, 10000L, null, null);
        this.toAddress = toAddress;
    }

    @Override
    public String getToAddress() {
        return this.toAddress;
    }

    @Override
    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    @Override
    public @Nullable Authorization getAuthorization() {
        return this.authorization;
    }

    @Override
    public void setAuthorization(@Nullable Authorization authorization) {
        this.authorization = authorization;
    }
}

