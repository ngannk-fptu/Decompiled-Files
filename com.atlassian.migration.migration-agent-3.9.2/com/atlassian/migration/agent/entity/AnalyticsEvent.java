/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  javax.persistence.Lob
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.AnalyticsEventType;
import com.atlassian.migration.agent.entity.WithId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="MIG_ANALYTICS_EVENT")
public class AnalyticsEvent
extends WithId {
    @Column(name="eventTimestamp", nullable=false)
    private long timestamp;
    @Column(name="eventType", nullable=false)
    @Enumerated(value=EnumType.STRING)
    private AnalyticsEventType eventType;
    @Column(name="event", nullable=false)
    @Lob
    private String event;

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public AnalyticsEventType getEventType() {
        return this.eventType;
    }

    public void setEventType(AnalyticsEventType eventType) {
        this.eventType = eventType;
    }

    public String getEvent() {
        return this.event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}

