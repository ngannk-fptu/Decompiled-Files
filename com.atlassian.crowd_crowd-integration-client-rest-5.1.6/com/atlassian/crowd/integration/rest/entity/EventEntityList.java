/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.AbstractEventEntity;
import com.atlassian.crowd.integration.rest.entity.GroupEventEntity;
import com.atlassian.crowd.integration.rest.entity.GroupMembershipEventEntity;
import com.atlassian.crowd.integration.rest.entity.UserEventEntity;
import com.atlassian.crowd.integration.rest.entity.UserMembershipEventEntity;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="events")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class EventEntityList {
    @XmlAttribute(name="newEventToken")
    private final String newEventToken;
    @XmlAttribute(name="incrementalSynchronisationAvailable")
    private final Boolean incrementalSynchronisationAvailable = null;
    @XmlElements(value={@XmlElement(name="userEvent", type=UserEventEntity.class), @XmlElement(name="groupEvent", type=GroupEventEntity.class), @XmlElement(name="userMembershipEvent", type=UserMembershipEventEntity.class), @XmlElement(name="groupMembershipEvent", type=GroupMembershipEventEntity.class)})
    private final List<AbstractEventEntity> events = null;

    private EventEntityList() {
        this.newEventToken = null;
    }

    public String getNewEventToken() {
        return this.newEventToken;
    }

    public Boolean isIncrementalSynchronisationAvailable() {
        return this.incrementalSynchronisationAvailable;
    }

    public List<AbstractEventEntity> getEvents() {
        return this.events;
    }
}

