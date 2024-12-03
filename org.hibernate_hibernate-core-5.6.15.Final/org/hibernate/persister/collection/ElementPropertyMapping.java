/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.collection;

import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.Type;

public class ElementPropertyMapping
implements PropertyMapping {
    private final String[] elementColumns;
    private final Type type;

    public ElementPropertyMapping(String[] elementColumns, Type type) throws MappingException {
        this.elementColumns = elementColumns;
        this.type = type;
    }

    @Override
    public Type toType(String propertyName) throws QueryException {
        if (propertyName == null || "id".equals(propertyName)) {
            return this.type;
        }
        throw new QueryException("cannot dereference scalar collection element: " + propertyName);
    }

    @Override
    public String[] toColumns(String alias, String propertyName) throws QueryException {
        if (propertyName == null || "id".equals(propertyName)) {
            return StringHelper.qualify(alias, this.elementColumns);
        }
        throw new QueryException("cannot dereference scalar collection element: " + propertyName);
    }

    @Override
    public String[] toColumns(String propertyName) throws QueryException, UnsupportedOperationException {
        throw new UnsupportedOperationException("References to collections must be define a SQL alias");
    }

    @Override
    public Type getType() {
        return this.type;
    }
}

