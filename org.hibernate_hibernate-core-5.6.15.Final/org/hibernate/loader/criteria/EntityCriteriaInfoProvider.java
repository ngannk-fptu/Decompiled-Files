/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.criteria;

import java.io.Serializable;
import org.hibernate.loader.criteria.CriteriaInfoProvider;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.type.Type;

class EntityCriteriaInfoProvider
implements CriteriaInfoProvider {
    private final Queryable persister;

    EntityCriteriaInfoProvider(Queryable persister) {
        this.persister = persister;
    }

    @Override
    public String getName() {
        return this.persister.getEntityName();
    }

    @Override
    public Serializable[] getSpaces() {
        return this.persister.getQuerySpaces();
    }

    @Override
    public PropertyMapping getPropertyMapping() {
        return this.persister;
    }

    @Override
    public Type getType(String relativePath) {
        return this.persister.toType(relativePath);
    }
}

