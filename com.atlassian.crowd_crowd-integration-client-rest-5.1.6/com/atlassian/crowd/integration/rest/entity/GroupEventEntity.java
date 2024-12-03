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
import com.atlassian.crowd.integration.rest.entity.GroupEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="groupEvent")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class GroupEventEntity
extends AbstractAttributeEventEntity {
    @XmlElement(name="group")
    private final GroupEntity group = null;

    private GroupEventEntity() {
    }

    public GroupEntity getGroup() {
        return this.group;
    }
}

