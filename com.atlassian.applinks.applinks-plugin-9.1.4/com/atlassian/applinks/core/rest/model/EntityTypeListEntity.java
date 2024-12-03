/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.EntityTypeEntity;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="entityTypes")
public class EntityTypeListEntity {
    @XmlElement(name="entityTypes")
    private List<EntityTypeEntity> entityTypes;

    private EntityTypeListEntity() {
    }

    public EntityTypeListEntity(List<EntityTypeEntity> entityTypes) {
        this.entityTypes = entityTypes;
    }

    public List<EntityTypeEntity> getTypes() {
        return this.entityTypes;
    }
}

