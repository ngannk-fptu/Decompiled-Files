/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.MailProtocol
 *  com.atlassian.mail.server.impl.PopMailServerImpl
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.mail;

import com.atlassian.confluence.mail.Authorization;
import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.mail.MailProtocol;
import com.atlassian.mail.server.impl.PopMailServerImpl;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConfluencePopMailServer
extends PopMailServerImpl
implements InboundMailServer {
    private static final long serialVersionUID = 7804065426878033692L;
    private String toAddress;
    private Authorization authorization;

    private ConfluencePopMailServer() {
    }

    public ConfluencePopMailServer(Long id, String name, String description, MailProtocol popProtocol, String serverName, String popPort, String username, String password, String toAddress) {
        super(id, name, description, popProtocol, serverName, popPort, username, password);
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

