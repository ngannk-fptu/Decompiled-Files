/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.AbstractEventEntity;
import com.atlassian.crowd.integration.rest.entity.GroupEntityList;
import com.atlassian.crowd.integration.rest.entity.UserEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="userMembershipEvent")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserMembershipEventEntity
extends AbstractEventEntity {
    @XmlElement(name="childUser")
    private final UserEntity childUser = null;
    @XmlElement(name="parentGroups")
    private final GroupEntityList parentGroups = null;
    @XmlAttribute
    private final Boolean absolute = null;

    private UserMembershipEventEntity() {
    }

    public UserEntity getChildUser() {
        return this.childUser;
    }

    public GroupEntityList getParentGroups() {
        return this.parentGroups;
    }

    public Boolean getAbsolute() {
        return this.absolute;
    }
}

