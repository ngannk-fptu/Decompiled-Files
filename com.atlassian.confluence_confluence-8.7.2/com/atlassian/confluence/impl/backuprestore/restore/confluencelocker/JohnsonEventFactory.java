/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.event.Event
 *  com.atlassian.johnson.event.EventLevel
 *  com.atlassian.johnson.event.EventType
 */
package com.atlassian.confluence.impl.backuprestore.restore.confluencelocker;

import com.atlassian.johnson.event.Event;
import com.atlassian.johnson.event.EventLevel;
import com.atlassian.johnson.event.EventType;

public class JohnsonEventFactory {
    Event createInProgressEvent() {
        String description = "Confluence is being restored from the backup and will be available when the process is completed.";
        EventType eventType = new EventType("Site restore is in progress", "Confluence is being restored from the backup and will be available when the process is completed.");
        EventLevel level = EventLevel.get((String)"warning");
        return new Event(eventType, "Confluence is being restored from the backup and will be available when the process is completed.", level);
    }

    Event createRestoreFailureEvent(String message) {
        String description = "Site restore process failed with an error: " + message;
        EventType eventType = new EventType("Site restore failed", description);
        EventLevel level = EventLevel.get((String)"fatal");
        return new Event(eventType, description, level);
    }
}

