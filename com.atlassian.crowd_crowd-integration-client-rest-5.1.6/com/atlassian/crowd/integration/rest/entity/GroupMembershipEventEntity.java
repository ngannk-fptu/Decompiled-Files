/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.AbstractEventEntity;
import com.atlassian.crowd.integration.rest.entity.GroupEntity;
import com.atlassian.crowd.integration.rest.entity.GroupEntityList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="groupMembershipEvent")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class GroupMembershipEventEntity
extends AbstractEventEntity {
    @XmlElement(name="group")
    private final GroupEntity group = null;
    @XmlElement(name="parentGroups")
    private final GroupEntityList parentGroups = null;
    @XmlElement(name="childGroups")
    private final GroupEntityList childGroups = null;

    private GroupMembershipEventEntity() {
    }

    public GroupEntity getGroup() {
        return this.group;
    }

    public GroupEntityList getParentGroups() {
        return this.parentGroups;
    }

    public GroupEntityList getChildGroups() {
        return this.childGroups;
    }
}

