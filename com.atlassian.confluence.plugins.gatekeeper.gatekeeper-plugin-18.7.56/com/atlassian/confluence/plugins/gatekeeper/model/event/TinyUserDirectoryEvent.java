/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.model.event;

import com.atlassian.confluence.plugins.gatekeeper.model.event.EventType;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.crowd.embedded.api.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TinyUserDirectoryEvent
extends TinyEvent {
    private static final long serialVersionUID = -2132736229911334109L;
    private static Logger logger = LoggerFactory.getLogger(TinyUserDirectoryEvent.class);

    private TinyUserDirectoryEvent(EventType eventType) {
        super(eventType);
    }

    public static TinyUserDirectoryEvent updated(Directory directory) {
        logger.debug("User directory {} with id {} updated", (Object)directory.getName(), (Object)directory.getId());
        return new TinyUserDirectoryEvent(EventType.USER_DIRECTORY_UPDATED);
    }

    public String toString() {
        return "TinyUserDirectoryEvent{eventType=" + this.eventType + "}";
    }
}

