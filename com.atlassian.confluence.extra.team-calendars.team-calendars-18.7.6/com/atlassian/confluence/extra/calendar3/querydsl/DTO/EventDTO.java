/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.DTO;

import java.io.Serializable;

public class EventDTO
implements Serializable {
    private int eventId;
    private String subCalendarId;
    private long utcStart;
    private long utcEnd;
    private long period;
    private String recurrenceRule;
    private String summary;
    private String description;
    private String location;
    private String url;
    private String organiser;
    private long recurrenceIdTimestamp;
    private long created;
    private long lastModified;
    private int sequence;
    private String storeKey;
    private boolean isAllDay;
    private long start;
    private long end;
    private String subCalendarTimeZoneId;
    private String veventUuid;
    private String calendarName;
    private String parentSubCalendarId;
    private String parentCalendarName;
    private String customEventTypeId;
    private String subscriptionId;
    private String eventTypeName;

    public EventDTO() {
    }

    public EventDTO(int eventId, String subCalendarId, long utcStart, long utcEnd, long period, String recurrenceRule, String summary, String description, String location, String url, String organiser, long recurrenceIdTimestamp, long created, long lastModified, int sequence, String storeKey, boolean isAllDay, long start, long end, String subCalendarTimeZoneId, String veventUuid, String calendarName, String parentSubCalendarId, String customEventTypeId, String eventTypeName, String subscriptionId) {
        this.eventId = eventId;
        this.subCalendarId = subCalendarId;
        this.utcStart = utcStart;
        this.utcEnd = utcEnd;
        this.period = period;
        this.recurrenceRule = recurrenceRule;
        this.summary = summary;
        this.description = description;
        this.location = location;
        this.url = url;
        this.organiser = organiser;
        this.recurrenceIdTimestamp = recurrenceIdTimestamp;
        this.created = created;
        this.lastModified = lastModified;
        this.sequence = sequence;
        this.storeKey = storeKey;
        this.isAllDay = isAllDay;
        this.start = start;
        this.end = end;
        this.subCalendarTimeZoneId = subCalendarTimeZoneId;
        this.veventUuid = veventUuid;
        this.calendarName = calendarName;
        this.parentSubCalendarId = parentSubCalendarId;
        this.customEventTypeId = customEventTypeId;
        this.eventTypeName = eventTypeName;
        this.subscriptionId = subscriptionId;
    }

    public String getEventTypeName() {
        return this.eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public int getEventId() {
        return this.eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public long getUtcStart() {
        return this.utcStart;
    }

    public void setUtcStart(long utcStart) {
        this.utcStart = utcStart;
    }

    public long getUtcEnd() {
        return this.utcEnd;
    }

    public void setUtcEnd(long utcEnd) {
        this.utcEnd = utcEnd;
    }

    public long getPeriod() {
        return this.period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public String getRecurrenceRule() {
        return this.recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrganiser() {
        return this.organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public long getRecurrenceIdTimestamp() {
        return this.recurrenceIdTimestamp;
    }

    public void setRecurrenceIdTimestamp(long recurrenceIdTimestamp) {
        this.recurrenceIdTimestamp = recurrenceIdTimestamp;
    }

    public long getCreated() {
        return this.created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getStoreKey() {
        return this.storeKey;
    }

    public void setStoreKey(String storeKey) {
        this.storeKey = storeKey;
    }

    public boolean isAllDay() {
        return this.isAllDay;
    }

    public void setAllDay(boolean isAllDay) {
        this.isAllDay = isAllDay;
    }

    public long getStart() {
        return this.start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return this.end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getSubCalendarTimeZoneId() {
        return this.subCalendarTimeZoneId;
    }

    public void setSubCalendarTimeZoneId(String subCalendarTimeZoneId) {
        this.subCalendarTimeZoneId = subCalendarTimeZoneId;
    }

    public String getVeventUuid() {
        return this.veventUuid;
    }

    public void setVeventUuid(String veventUuid) {
        this.veventUuid = veventUuid;
    }

    public String getCalendarName() {
        return this.calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public String getParentSubCalendarId() {
        return this.parentSubCalendarId;
    }

    public void setParentSubCalendarId(String parentSubCalendarId) {
        this.parentSubCalendarId = parentSubCalendarId;
    }

    public String getParentCalendarName() {
        return this.parentCalendarName;
    }

    public void setParentCalendarName(String parentCalendarName) {
        this.parentCalendarName = parentCalendarName;
    }

    public String getCustomEventTypeId() {
        return this.customEventTypeId;
    }

    public void setCustomEventTypeId(String customEventTypeId) {
        this.customEventTypeId = customEventTypeId;
    }

    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}

