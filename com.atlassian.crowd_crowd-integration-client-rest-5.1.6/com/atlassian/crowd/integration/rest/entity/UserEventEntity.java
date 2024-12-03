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

import com.atlassian.crowd.integration.rest.entity.AbstractAttributeEventEntity;
import com.atlassian.crowd.integration.rest.entity.UserEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="userEvent")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserEventEntity
extends AbstractAttributeEventEntity {
    @XmlElement(name="user")
    private final UserEntity user = null;

    private UserEventEntity() {
    }

    public UserEntity getUser() {
        return this.user;
    }
}

