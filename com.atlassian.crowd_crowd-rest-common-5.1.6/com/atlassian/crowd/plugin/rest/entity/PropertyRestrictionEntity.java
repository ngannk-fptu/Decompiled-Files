/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.plugin.rest.entity.PropertyEntity;
import com.atlassian.crowd.plugin.rest.entity.SearchRestrictionEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="property-search-restriction")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class PropertyRestrictionEntity
extends SearchRestrictionEntity {
    @XmlElement(name="property")
    private final PropertyEntity property;
    @XmlElement(name="match-mode")
    private final String matchMode;
    @XmlElement(name="value")
    private final String value;

    private PropertyRestrictionEntity() {
        this.property = null;
        this.matchMode = null;
        this.value = null;
    }

    public PropertyRestrictionEntity(PropertyEntity property, String matchMode, String value) {
        this.property = property;
        this.matchMode = matchMode;
        this.value = value;
    }

    public PropertyEntity getProperty() {
        return this.property;
    }

    public String getMatchMode() {
        return this.matchMode;
    }

    public String getValue() {
        return this.value;
    }
}

