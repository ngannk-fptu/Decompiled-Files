/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.model.event;

import com.atlassian.confluence.plugins.gatekeeper.model.event.EventType;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.crowd.model.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TinyApplicationEvent
extends TinyEvent {
    private static final long serialVersionUID = -7251122684127177445L;
    private static Logger logger = LoggerFactory.getLogger(TinyApplicationEvent.class);

    private TinyApplicationEvent(EventType eventType) {
        super(eventType);
    }

    public static TinyApplicationEvent updated(Application application) {
        logger.debug("Application {} with id {} updated", (Object)application.getName(), (Object)application.getId());
        return new TinyApplicationEvent(EventType.APPLICATION_UPDATED);
    }

    public String toString() {
        return "TinyApplicationEvent{eventType=" + this.eventType + "}";
    }
}

