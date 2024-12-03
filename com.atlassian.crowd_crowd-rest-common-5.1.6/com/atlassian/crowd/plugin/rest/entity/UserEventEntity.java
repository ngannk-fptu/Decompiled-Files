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
import com.atlassian.crowd.plugin.rest.entity.AbstractAttributeEventEntity;
import com.atlassian.crowd.plugin.rest.entity.MultiValuedAttributeEntityList;
import com.atlassian.crowd.plugin.rest.entity.UserEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="userEvent")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserEventEntity
extends AbstractAttributeEventEntity {
    @XmlElement(name="user")
    private final UserEntity user;

    private UserEventEntity() {
        super(null, null, null);
        this.user = null;
    }

    public UserEventEntity(Operation operation, UserEntity user, MultiValuedAttributeEntityList storedAttributes, MultiValuedAttributeEntityList deletedAttributes) {
        super(operation, storedAttributes, deletedAttributes);
        this.user = user;
    }
}

