/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.spaces;

import org.hibernate.QueryException;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public class CompositePropertyMapping
implements PropertyMapping {
    private final CompositeType compositeType;
    private final PropertyMapping parentPropertyMapping;
    private final String parentPropertyName;

    public CompositePropertyMapping(CompositeType compositeType, PropertyMapping parentPropertyMapping, String parentPropertyName) {
        this.compositeType = compositeType;
        this.parentPropertyMapping = parentPropertyMapping;
        this.parentPropertyName = parentPropertyName;
    }

    @Override
    public Type toType(String propertyName) throws QueryException {
        return this.parentPropertyMapping.toType(this.toParentPropertyPath(propertyName));
    }

    protected String toParentPropertyPath(String propertyName) {
        this.checkIncomingPropertyName(propertyName);
        return this.resolveParentPropertyPath(propertyName);
    }

    protected void checkIncomingPropertyName(String propertyName) {
        if (propertyName == null) {
            throw new NullPointerException("Provided property name cannot be null");
        }
    }

    protected String resolveParentPropertyPath(String propertyName) {
        if (StringHelper.isEmpty(this.parentPropertyName)) {
            return propertyName;
        }
        return this.parentPropertyName + '.' + propertyName;
    }

    @Override
    public String[] toColumns(String alias, String propertyName) throws QueryException {
        return this.parentPropertyMapping.toColumns(alias, this.toParentPropertyPath(propertyName));
    }

    @Override
    public String[] toColumns(String propertyName) throws QueryException, UnsupportedOperationException {
        return this.parentPropertyMapping.toColumns(this.toParentPropertyPath(propertyName));
    }

    @Override
    public CompositeType getType() {
        return this.compositeType;
    }
}

