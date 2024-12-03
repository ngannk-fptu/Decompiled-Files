/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.mail.server.MailServer
 */
package com.atlassian.confluence.event.listeners;

import com.atlassian.confluence.event.events.admin.MailServerCreateEvent;
import com.atlassian.confluence.event.events.admin.MailServerDeleteEvent;
import com.atlassian.confluence.event.events.admin.MailServerEditEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.jmx.JmxUtil;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.mail.server.MailServer;

public class MailServerEventListener {
    @EventListener
    public void handleCreateEvent(MailServerCreateEvent createEvent) {
        MailServer server = createEvent.getServer();
        JmxUtil.registerBean(this.makeNameForServer(server.getName()), createEvent.getServer());
    }

    @EventListener
    public void handleDeleteEvent(MailServerDeleteEvent deleteEvent) {
        JmxUtil.unregisterBean(this.makeNameForServer(deleteEvent.getServer().getName()));
    }

    @EventListener
    public void handleEditEvent(MailServerEditEvent editEvent) {
        JmxUtil.unregisterBean(this.makeNameForServer(editEvent.getOriginalServerName()));
        JmxUtil.registerBean(this.makeNameForServer(editEvent.getServer().getName()), editEvent.getServer());
    }

    @EventListener
    public void handleClusterEvent(ClusterEventWrapper wrapper) {
        Event event = wrapper.getEvent();
        if (event instanceof MailServerCreateEvent) {
            this.handleCreateEvent((MailServerCreateEvent)event);
        } else if (event instanceof MailServerDeleteEvent) {
            this.handleDeleteEvent((MailServerDeleteEvent)event);
        } else if (event instanceof MailServerEditEvent) {
            this.handleEditEvent((MailServerEditEvent)event);
        }
    }

    private String makeNameForServer(String name) {
        return "Confluence:name=MailServer-" + name;
    }
}

