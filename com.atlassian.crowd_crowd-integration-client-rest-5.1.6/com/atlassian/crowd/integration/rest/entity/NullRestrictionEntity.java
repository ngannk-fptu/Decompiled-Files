/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.integration.rest.entity.SearchRestrictionEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="null-search-restriction")
@XmlAccessorType(value=XmlAccessType.FIELD)
public final class NullRestrictionEntity
extends SearchRestrictionEntity {
    public static final NullRestrictionEntity INSTANCE = new NullRestrictionEntity();

    private NullRestrictionEntity() {
    }
}

