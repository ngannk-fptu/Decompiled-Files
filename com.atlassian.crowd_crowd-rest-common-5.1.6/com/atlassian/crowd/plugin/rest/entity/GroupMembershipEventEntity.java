/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.event.Operation
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.model.event.Operation;
import com.atlassian.crowd.plugin.rest.entity.AbstractEventEntity;
import com.atlassian.crowd.plugin.rest.entity.GroupEntity;
import com.atlassian.crowd.plugin.rest.entity.GroupEntityList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="groupMembershipEvent")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class GroupMembershipEventEntity
extends AbstractEventEntity {
    @XmlElement(name="group")
    private final GroupEntity group;
    @XmlElement(name="parentGroups")
    private final GroupEntityList parentGroups;
    @XmlElement(name="childGroups")
    private final GroupEntityList childGroups;

    private GroupMembershipEventEntity() {
        super(null);
        this.group = null;
        this.parentGroups = null;
        this.childGroups = null;
    }

    public GroupMembershipEventEntity(Operation operation, GroupEntity childGroup, GroupEntityList parentGroups, GroupEntityList childGroups) {
        super(operation);
        this.group = childGroup;
        this.parentGroups = parentGroups;
        this.childGroups = childGroups;
    }
}

