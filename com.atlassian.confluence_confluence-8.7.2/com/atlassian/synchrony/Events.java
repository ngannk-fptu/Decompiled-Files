/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.synchrony;

import com.atlassian.confluence.core.NotExportable;
import com.atlassian.synchrony.EventId;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Events
implements NotExportable {
    private EventId eventId;
    private int partition;
    private int sequence;
    private byte[] event;
    private long contentId;
    private Date inserted;

    public EventId getEventId() {
        return this.eventId;
    }

    public void setEventId(EventId eventId) {
        this.eventId = eventId;
    }

    public byte[] getEvent() {
        return this.event;
    }

    public void setEvent(byte[] event) {
        this.event = event;
    }

    public int getPartition() {
        return this.partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public long getContentId() {
        return this.contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public Date getInserted() {
        return this.inserted;
    }

    public void setInserted(Date inserted) {
        this.inserted = inserted;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Events events = (Events)o;
        return Objects.equals(this.eventId, events.eventId);
    }

    public int hashCode() {
        return Objects.hash(this.eventId);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("eventId", (Object)this.eventId).append("partition", this.partition).append("sequence", this.sequence).append("contentId", this.contentId).append("inserted", (Object)this.inserted).toString();
    }
}

