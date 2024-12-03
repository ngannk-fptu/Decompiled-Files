/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.server.MailServer
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.MailServerEvent;
import com.atlassian.mail.server.MailServer;

public class MailServerEditEvent
extends MailServerEvent {
    private static final long serialVersionUID = -8377944761020515130L;
    private final String originalServerName;

    public MailServerEditEvent(Object src, MailServer server, String originalServerName) {
        super(src, server);
        this.originalServerName = originalServerName;
    }

    public String getOriginalServerName() {
        return this.originalServerName;
    }
}

