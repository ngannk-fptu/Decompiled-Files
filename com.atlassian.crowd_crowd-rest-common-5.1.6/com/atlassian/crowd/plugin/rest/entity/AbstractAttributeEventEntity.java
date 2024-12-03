/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.event.Operation
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.model.event.Operation;
import com.atlassian.crowd.plugin.rest.entity.AbstractEventEntity;
import com.atlassian.crowd.plugin.rest.entity.MultiValuedAttributeEntityList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
public abstract class AbstractAttributeEventEntity
extends AbstractEventEntity {
    @XmlElement(name="storedAttributes")
    private MultiValuedAttributeEntityList storedAttributes;
    @XmlElement(name="deletedAttributes")
    private MultiValuedAttributeEntityList deletedAttributes;

    private AbstractAttributeEventEntity() {
        super(null);
        this.storedAttributes = null;
        this.deletedAttributes = null;
    }

    protected AbstractAttributeEventEntity(Operation operation, MultiValuedAttributeEntityList storedAttributes, MultiValuedAttributeEntityList deletedAttributes) {
        super(operation);
        this.storedAttributes = storedAttributes;
        this.deletedAttributes = deletedAttributes;
    }
}

