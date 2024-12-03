/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.EventTypeReminder;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubCalendar
implements Serializable {
    private PersistedSubCalendar parent;
    private String name;
    private String description;
    private String color;
    private String timeZoneId;
    private String sourceLocation;
    private String userName;
    private String password;
    private String spaceKey;
    private String type;
    private String storeKey;
    private String customEventTypeId;
    private Set<String> disableEventTypes;
    private Set<CustomEventType> customEventTypes;
    private Map<String, List<String>> groupRestrictionMap;
    private Map<String, List<String>> userRestrictionMap;
    private Set<EventTypeReminder> eventTypeReminders;
    private long start;
    private long end;
    private long createdDate;
    private long lastUpdateDate;

    public long getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getStoreKey() {
        return this.storeKey;
    }

    public void setStoreKey(String storeKey) {
        this.storeKey = storeKey;
    }

    public Map<String, List<String>> getGroupRestrictionMap() {
        return this.groupRestrictionMap;
    }

    public void setGroupRestrictionMap(Map<String, List<String>> groupRestrictionMap) {
        this.groupRestrictionMap = groupRestrictionMap;
    }

    public Map<String, List<String>> getUserRestrictionMap() {
        return this.userRestrictionMap;
    }

    public void setUserRestrictionMap(Map<String, List<String>> userRestrictionMap) {
        this.userRestrictionMap = userRestrictionMap;
    }

    public PersistedSubCalendar getParent() {
        return this.parent;
    }

    public void setParent(PersistedSubCalendar parent) {
        this.parent = parent;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTimeZoneId() {
        return this.timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getSourceLocation() {
        return this.sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getDisableEventTypes() {
        return this.disableEventTypes;
    }

    public void setDisableEventTypes(Set<String> disableEventTypes) {
        this.disableEventTypes = disableEventTypes;
    }

    public Set<CustomEventType> getCustomEventTypes() {
        return this.customEventTypes;
    }

    public void setCustomEventTypes(Set<CustomEventType> customEventTypes) {
        this.customEventTypes = customEventTypes;
    }

    public String getCustomEventTypeId() {
        return this.customEventTypeId;
    }

    public void setCustomEventTypeId(String customEventTypeId) {
        this.customEventTypeId = customEventTypeId;
    }

    public Set<EventTypeReminder> getEventTypeReminders() {
        return this.eventTypeReminders;
    }

    public void setEventTypeReminders(Set<EventTypeReminder> eventTypeReminders) {
        this.eventTypeReminders = eventTypeReminders;
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

    public boolean isOnSpace() {
        return this.spaceKey != null && !this.spaceKey.trim().equals("");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubCalendar)) {
            return false;
        }
        SubCalendar that = (SubCalendar)o;
        return this.name != null ? this.name.equals(that.name) : that.name == null;
    }

    public int hashCode() {
        return this.name != null ? this.name.hashCode() : 0;
    }
}

