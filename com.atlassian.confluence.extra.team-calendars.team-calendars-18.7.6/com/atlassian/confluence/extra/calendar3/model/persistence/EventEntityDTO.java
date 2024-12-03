/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.EntityManager
 *  net.java.ao.RawEntity
 */
package com.atlassian.confluence.extra.calendar3.model.persistence;

import com.atlassian.confluence.extra.calendar3.model.persistence.EntityDTO;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import java.beans.PropertyChangeListener;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;

public class EventEntityDTO
implements EventEntity,
EntityDTO {
    private int eventId;

    public EventEntityDTO(int eventId) {
        this.eventId = eventId;
    }

    @Override
    public String getVeventUid() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setVeventUid(String vEventUid) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public SubCalendarEntity getSubCalendar() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setSubCalendar(SubCalendarEntity subCalendarEntity) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public long getStart() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setStart(long start) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public long getEnd() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setEnd(long end) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public long getUtcStart() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setUtcStart(long start) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public long getUtcEnd() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setUtcEnd(long end) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public boolean isAllDay() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setAllDay(boolean allDay) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public String getSummary() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setSummary(String summary) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setDescription(String description) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public String getLocation() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setLocation(String location) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public String getUrl() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setUrl(String url) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public String getOrganiser() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setOrganiser(String organiser) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public String getRecurrenceRule() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setRecurrenceRule(String rRule) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public Long getRecurrenceIdTimestamp() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setRecurrenceIdTimestamp(Long timestamp) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public InviteeEntity[] getInvitees() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public EventRecurrenceExclusionEntity[] getExclusions() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public long getCreated() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setCreated(long timestamp) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public long getLastModified() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setLastModified(long timestamp) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public int getSequence() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setSequence(int seq) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public ReminderSettingEntity getReminderSetting() {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    @Override
    public void setReminderSetting(ReminderSettingEntity event) {
        throw new UnsupportedOperationException("EventEntityDTO does not support this operation");
    }

    public int getID() {
        return this.eventId;
    }

    public void init() {
        throw new UnsupportedOperationException("InviteeEntityDTO does not support this operation");
    }

    public void save() {
        throw new UnsupportedOperationException("InviteeEntityDTO does not support this operation");
    }

    public EntityManager getEntityManager() {
        throw new UnsupportedOperationException("InviteeEntityDTO does not support this operation");
    }

    public <X extends RawEntity<Integer>> Class<X> getEntityType() {
        throw new UnsupportedOperationException("InviteeEntityDTO does not support this operation");
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        throw new UnsupportedOperationException("InviteeEntityDTO does not support this operation");
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        throw new UnsupportedOperationException("InviteeEntityDTO does not support this operation");
    }
}

