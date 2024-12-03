/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonSubTypes
 *  org.codehaus.jackson.annotate.JsonSubTypes$Type
 *  org.codehaus.jackson.annotate.JsonTypeInfo
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.plugin.rest.entity.NullRestrictionEntity;
import com.atlassian.crowd.plugin.rest.entity.PropertyRestrictionEntity;
import com.atlassian.crowd.plugin.rest.entity.SearchRestrictionEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@XmlRootElement(name="boolean-search-restriction")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class BooleanRestrictionEntity
extends SearchRestrictionEntity {
    @XmlAttribute(name="boolean-logic")
    private final String booleanLogic;
    @XmlElementWrapper(name="restrictions")
    @XmlElements(value={@XmlElement(name="boolean-search-restriction", type=BooleanRestrictionEntity.class), @XmlElement(name="property-search-restriction", type=PropertyRestrictionEntity.class), @XmlElement(name="null-search-restriction", type=NullRestrictionEntity.class)})
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="restriction-type")
    @JsonSubTypes(value={@JsonSubTypes.Type(value=BooleanRestrictionEntity.class, name="boolean-search-restriction"), @JsonSubTypes.Type(value=PropertyRestrictionEntity.class, name="property-search-restriction"), @JsonSubTypes.Type(value=NullRestrictionEntity.class, name="null-search-restriction")})
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

