/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.criteria;

import java.io.Serializable;
import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
import org.hibernate.loader.criteria.CriteriaInfoProvider;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.Type;

class ScalarCollectionCriteriaInfoProvider
implements CriteriaInfoProvider {
    private final String role;
    private final QueryableCollection persister;
    private final SessionFactoryHelper helper;

    ScalarCollectionCriteriaInfoProvider(SessionFactoryHelper helper, String role) {
        this.role = role;
        this.helper = helper;
        this.persister = helper.requireQueryableCollection(role);
    }

    @Override
    public String getName() {
        return this.role;
    }

    @Override
    public Serializable[] getSpaces() {
        return this.persister.getCollectionSpaces();
    }

    @Override
    public PropertyMapping getPropertyMapping() {
        return this.helper.getCollectionPropertyMapping(this.role);
    }

    @Override
    public Type getType(String relativePath) {
        return this.getPropertyMapping().toType(relativePath);
    }
}

