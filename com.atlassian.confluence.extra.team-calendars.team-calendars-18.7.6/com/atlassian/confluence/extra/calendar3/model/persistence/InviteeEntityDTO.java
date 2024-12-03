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
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;

public class InviteeEntityDTO
implements InviteeEntity,
EntityDTO {
    private String inviteeId;
    private int id;
    private int eventId;

    public InviteeEntityDTO(int id, int eventId, String inviteeId) {
        this.id = id;
        this.eventId = eventId;
        this.inviteeId = inviteeId;
    }

    @Override
    public String getInviteeId() {
        return this.inviteeId;
    }

    @Override
    public void setInviteeId(String inviteeId) {
        throw new UnsupportedOperationException("InviteeEntityDTO does not support this operation");
    }

    public int getID() {
        return this.id;
    }

    public void init() {
        throw new UnsupportedOperationException("InviteeEntityDTO does not support this operation");
    }

    @Override
    public EventEntity getEvent() {
        return new EventEntityDTO(this.eventId);
    }

    @Override
    public void setEvent(EventEntity event) {
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

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        InviteeEntityDTO that = (InviteeEntityDTO)o;
        return this.eventId == that.eventId && this.inviteeId.equals(that.inviteeId);
    }

    public int hashCode() {
        return Objects.hash(this.inviteeId, this.eventId);
    }
}

