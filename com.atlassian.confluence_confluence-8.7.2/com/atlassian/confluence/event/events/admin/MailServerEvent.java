/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.server.MailServer
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.admin.ConfigurationEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.mail.server.MailServer;

public abstract class MailServerEvent
extends ConfigurationEvent
implements ClusterEvent {
    private static final long serialVersionUID = -7886710504862442916L;
    private MailServer server;

    public MailServerEvent(Object src, MailServer server) {
        super(src);
        this.server = server;
    }

    public MailServer getServer() {
        return this.server;
    }
}

