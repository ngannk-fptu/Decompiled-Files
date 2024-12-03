/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.AbstractEventEntity;
import com.atlassian.crowd.integration.rest.entity.MultiValuedAttributeEntityList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
public abstract class AbstractAttributeEventEntity
extends AbstractEventEntity {
    @XmlElement(name="storedAttributes")
    private MultiValuedAttributeEntityList storedAttributes = null;
    @XmlElement(name="deletedAttributes")
    private MultiValuedAttributeEntityList deletedAttributes = null;

    protected AbstractAttributeEventEntity() {
    }

    public MultiValuedAttributeEntityList getStoredAttributes() {
        return this.storedAttributes;
    }

    public MultiValuedAttributeEntityList getDeletedAttributes() {
        return this.deletedAttributes;
    }
}

