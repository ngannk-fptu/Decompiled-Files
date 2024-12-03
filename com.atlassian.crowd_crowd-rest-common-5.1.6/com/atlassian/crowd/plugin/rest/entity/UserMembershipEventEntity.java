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
import com.atlassian.crowd.plugin.rest.entity.GroupEntityList;
import com.atlassian.crowd.plugin.rest.entity.UserEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="userMembershipEvent")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserMembershipEventEntity
extends AbstractEventEntity {
    @XmlElement(name="childUser")
    private final UserEntity childUser;
    @XmlElement(name="parentGroups")
    private final GroupEntityList parentGroups;

    private UserMembershipEventEntity() {
        super(null);
        this.childUser = null;
        this.parentGroups = null;
    }

    public UserMembershipEventEntity(Operation operation, UserEntity childUser, GroupEntityList parentGroups) {
        super(operation);
        this.childUser = childUser;
        this.parentGroups = parentGroups;
    }
}

