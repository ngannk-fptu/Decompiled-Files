/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.util.Collection;
import org.hibernate.MappingException;

public interface SynchronizeableQuery<T> {
    public Collection<String> getSynchronizedQuerySpaces();

    public SynchronizeableQuery<T> addSynchronizedQuerySpace(String var1);

    default public SynchronizeableQuery<T> addSynchronizedQuerySpace(String ... querySpaces) {
        if (querySpaces != null) {
            for (int i = 0; i < querySpaces.length; ++i) {
                this.addSynchronizedQuerySpace(querySpaces[i]);
            }
        }
        return this;
    }

    default public SynchronizeableQuery<T> addSynchronizedTable(String tableExpression) {
        return this.addSynchronizedQuerySpace(tableExpression);
    }

    default public SynchronizeableQuery<T> addSynchronizedTable(String ... tableExpressions) {
        return this.addSynchronizedQuerySpace(tableExpressions);
    }

    public SynchronizeableQuery<T> addSynchronizedEntityName(String var1) throws MappingException;

    default public SynchronizeableQuery<T> addSynchronizedEntityName(String ... entityNames) throws MappingException {
        if (entityNames != null) {
            for (int i = 0; i < entityNames.length; ++i) {
                this.addSynchronizedEntityName(entityNames[i]);
            }
        }
        return this;
    }

    public SynchronizeableQuery<T> addSynchronizedEntityClass(Class var1) throws MappingException;

    default public SynchronizeableQuery<T> addSynchronizedEntityClass(Class<?> ... entityClasses) throws MappingException {
        if (entityClasses != null) {
            for (int i = 0; i < entityClasses.length; ++i) {
                this.addSynchronizedEntityClass((Class)entityClasses[i]);
            }
        }
        return this;
    }
}

