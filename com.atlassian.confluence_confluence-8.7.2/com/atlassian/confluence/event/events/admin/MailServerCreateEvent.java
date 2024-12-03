/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.server.MailServer
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.MailServerEvent;
import com.atlassian.mail.server.MailServer;

public class MailServerCreateEvent
extends MailServerEvent {
    private static final long serialVersionUID = 571847793014509144L;

    public MailServerCreateEvent(Object src, MailServer server) {
        super(src, server);
    }
}

