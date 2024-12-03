/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAnyElement
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.SearchRestrictionEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="boolean-search-restriction")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class BooleanRestrictionEntity
extends SearchRestrictionEntity {
    @XmlAttribute(name="boolean-logic")
    private final String booleanLogic;
    @XmlElementWrapper(name="restrictions")
    @XmlAnyElement
    private final Collection<SearchRestrictionEntity> restrictions;

    private BooleanRestrictionEntity() {
        this.booleanLogic = null;
        this.restrictions = new ArrayList<SearchRestrictionEntity>();
    }

    public BooleanRestrictionEntity(String booleanLogic, Collection<SearchRestrictionEntity> restrictions) {
        this.booleanLogic = booleanLogic;
        this.restrictions = Collections.unmodifiableCollection(restrictions);
    }

    public Collection<SearchRestrictionEntity> getRestrictions() {
        return this.restrictions;
    }

    public String getBooleanLogic() {
        return this.booleanLogic;
    }
}

