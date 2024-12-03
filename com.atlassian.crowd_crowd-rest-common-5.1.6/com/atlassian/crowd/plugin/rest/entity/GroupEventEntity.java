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
import com.atlassian.crowd.plugin.rest.entity.GroupEntity;
import com.atlassian.crowd.plugin.rest.entity.MultiValuedAttributeEntityList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="groupEvent")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class GroupEventEntity
extends AbstractAttributeEventEntity {
    @XmlElement(name="group")
    private final GroupEntity group;

    private GroupEventEntity() {
        super(null, null, null);
        this.group = null;
    }

    public GroupEventEntity(Operation operation, GroupEntity group, MultiValuedAttributeEntityList storedAttributes, MultiValuedAttributeEntityList deletedAttributes) {
        super(operation, storedAttributes, deletedAttributes);
        this.group = group;
    }
}

