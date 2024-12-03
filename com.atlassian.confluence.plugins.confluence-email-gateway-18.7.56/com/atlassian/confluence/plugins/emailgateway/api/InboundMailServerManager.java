/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.server.MailServer
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.mail.server.MailServer;

public interface InboundMailServerManager {
    public MailServer getMailServer();
}

