/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlSeeAlso
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.BooleanRestrictionEntity;
import com.atlassian.crowd.integration.rest.entity.NullRestrictionEntity;
import com.atlassian.crowd.integration.rest.entity.PropertyRestrictionEntity;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name="search-restriction")
@XmlSeeAlso(value={BooleanRestrictionEntity.class, PropertyRestrictionEntity.class, NullRestrictionEntity.class})
public abstract class SearchRestrictionEntity {
}

