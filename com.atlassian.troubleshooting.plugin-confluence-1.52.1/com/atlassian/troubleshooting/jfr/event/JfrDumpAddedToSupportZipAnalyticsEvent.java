/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.jfr.event;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="atst.jfr.dump.added.to.support.zip")
public class JfrDumpAddedToSupportZipAnalyticsEvent {
    private final long recordingId;
    private final String name;
    private final long size;

    public JfrDumpAddedToSupportZipAnalyticsEvent(long recordingId, String name, long size) {
        this.recordingId = recordingId;
        this.name = name;
        this.size = size;
    }

    public long getRecordingId() {
        return this.recordingId;
    }

    public String getName() {
        return this.name;
    }

    public long getSize() {
        return this.size;
    }

    public String toString() {
        return "JfrDumpAddedToSupportZipAnalyticsEvent{recordingId=" + this.recordingId + ", name='" + this.name + '\'' + ", size=" + this.size + '}';
    }
}

