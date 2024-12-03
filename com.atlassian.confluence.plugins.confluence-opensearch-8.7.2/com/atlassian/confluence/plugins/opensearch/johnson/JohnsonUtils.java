/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.johnson.event.Event
 *  com.atlassian.johnson.event.EventLevel
 *  com.atlassian.johnson.event.EventType
 */
package com.atlassian.confluence.plugins.opensearch.johnson;

import com.atlassian.johnson.Johnson;
import com.atlassian.johnson.event.Event;
import com.atlassian.johnson.event.EventLevel;
import com.atlassian.johnson.event.EventType;

public class JohnsonUtils {
    private static final String ERROR_DESC = "johnson.message.opensearch.startup.error";

    private JohnsonUtils() {
    }

    public static synchronized void raiseStartupErrorIfNotExistFor(Exception exception) {
        if (JohnsonUtils.hasOpenSearchEvent()) {
            return;
        }
        Event johnsonEvent = new Event(EventType.get((String)"startup"), ERROR_DESC, exception.getMessage(), EventLevel.get((String)"error"));
        johnsonEvent.addAttribute((Object)"i18nKey", (Object)johnsonEvent.getDesc());
        Johnson.getEventContainer().addEvent(johnsonEvent);
    }

    public static boolean hasOpenSearchEvent() {
        return Johnson.getEventContainer().getEvents().stream().anyMatch(event -> ERROR_DESC.equalsIgnoreCase(event.getDesc()));
    }
}

