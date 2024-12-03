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
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntityDTO;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntity;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;

public class EventRecurrenceExclusionEntityDTO
implements EventRecurrenceExclusionEntity,
EntityDTO {
    private final int eventId;
    private final int id;
    private final long exclusion;
    private final boolean isAllday;

    public EventRecurrenceExclusionEntityDTO(int id, int eventId, long exclusion, boolean isAllday) {
        this.eventId = eventId;
        this.id = id;
        this.exclusion = exclusion;
        this.isAllday = isAllday;
    }

    @Override
    public EventEntity getEvent() {
        return new EventEntityDTO(this.eventId);
    }

    @Override
    public void setEvent(EventEntity event) {
        throw new UnsupportedOperationException("EventRecurrenceExclusionEntityDTO does not support this operation");
    }

    @Override
    public long getExclusion() {
        return this.exclusion;
    }

    @Override
    public void setExclusion(long date) {
        throw new UnsupportedOperationException("EventRecurrenceExclusionEntityDTO does not support this operation");
    }

    @Override
    public boolean isAllDay() {
        return this.isAllday;
    }

    @Override
    public void setAllDay(boolean allDay) {
        throw new UnsupportedOperationException("EventRecurrenceExclusionEntityDTO does not support this operation");
    }

    public int getID() {
        return this.id;
    }

    public void init() {
        throw new UnsupportedOperationException("EventRecurrenceExclusionEntityDTO does not support this operation");
    }

    public void save() {
        throw new UnsupportedOperationException("EventRecurrenceExclusionEntityDTO does not support this operation");
    }

    public EntityManager getEntityManager() {
        throw new UnsupportedOperationException("EventRecurrenceExclusionEntityDTO does not support this operation");
    }

    public <X extends RawEntity<Integer>> Class<X> getEntityType() {
        throw new UnsupportedOperationException("EventRecurrenceExclusionEntityDTO does not support this operation");
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        throw new UnsupportedOperationException("EventRecurrenceExclusionEntityDTO does not support this operation");
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        throw new UnsupportedOperationException("EventRecurrenceExclusionEntityDTO does not support this operation");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EventRecurrenceExclusionEntityDTO that = (EventRecurrenceExclusionEntityDTO)o;
        return this.eventId == that.eventId && this.exclusion == that.exclusion && this.isAllday == that.isAllday;
    }

    public int hashCode() {
        return Objects.hash(this.eventId, this.exclusion, this.isAllday);
    }
}

