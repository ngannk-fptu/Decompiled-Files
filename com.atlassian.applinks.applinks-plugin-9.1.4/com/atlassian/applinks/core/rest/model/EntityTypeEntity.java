/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.spi.application.TypeId
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.core.rest.model.adapter.TypeIdAdapter;
import com.atlassian.applinks.spi.application.TypeId;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="entityType")
public class EntityTypeEntity {
    @XmlJavaTypeAdapter(value=TypeIdAdapter.class)
    private TypeId typeId;

    private EntityTypeEntity() {
    }

    public EntityTypeEntity(EntityType type) {
        this.typeId = TypeId.getTypeId((EntityType)type);
    }

    public TypeId getTypeId() {
        return this.typeId;
    }
}

