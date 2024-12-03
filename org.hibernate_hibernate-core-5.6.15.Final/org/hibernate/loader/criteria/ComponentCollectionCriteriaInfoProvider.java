/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.criteria;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.loader.criteria.CriteriaInfoProvider;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

class ComponentCollectionCriteriaInfoProvider
implements CriteriaInfoProvider {
    private final QueryableCollection persister;
    private final Map<String, Type> subTypes = new HashMap<String, Type>();

    ComponentCollectionCriteriaInfoProvider(QueryableCollection persister) {
        this.persister = persister;
        if (!persister.getElementType().isComponentType()) {
            throw new IllegalArgumentException("persister for role " + persister.getRole() + " is not a collection-of-component");
        }
        CompositeType componentType = (CompositeType)persister.getElementType();
        String[] names = componentType.getPropertyNames();
        Type[] types = componentType.getSubtypes();
        for (int i = 0; i < names.length; ++i) {
            this.subTypes.put(names[i], types[i]);
        }
    }

    @Override
    public String getName() {
        return this.persister.getRole();
    }

    @Override
    public Serializable[] getSpaces() {
        return this.persister.getCollectionSpaces();
    }

    @Override
    public PropertyMapping getPropertyMapping() {
        return this.persister;
    }

    @Override
    public Type getType(String relativePath) {
        if (relativePath.indexOf(46) >= 0) {
            throw new IllegalArgumentException("dotted paths not handled (yet?!) for collection-of-component");
        }
        Type type = this.subTypes.get(relativePath);
        if (type == null) {
            throw new IllegalArgumentException("property " + relativePath + " not found in component of collection " + this.getName());
        }
        return type;
    }
}

