/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  org.codehaus.jackson.annotate.JsonSubTypes
 *  org.codehaus.jackson.annotate.JsonSubTypes$Type
 *  org.codehaus.jackson.annotate.JsonTypeInfo
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.plugin.rest.entity.BooleanRestrictionEntity;
import com.atlassian.crowd.plugin.rest.entity.NullRestrictionEntity;
import com.atlassian.crowd.plugin.rest.entity.PropertyRestrictionEntity;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@XmlRootElement(name="search-restriction")
@XmlSeeAlso(value={BooleanRestrictionEntity.class, PropertyRestrictionEntity.class, NullRestrictionEntity.class})
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="restriction-type")
@JsonSubTypes(value={@JsonSubTypes.Type(value=BooleanRestrictionEntity.class, name="boolean-search-restriction"), @JsonSubTypes.Type(value=PropertyRestrictionEntity.class, name="property-search-restriction"), @JsonSubTypes.Type(value=NullRestrictionEntity.class, name="null-search-restriction")})
public abstract class SearchRestrictionEntity {
}

