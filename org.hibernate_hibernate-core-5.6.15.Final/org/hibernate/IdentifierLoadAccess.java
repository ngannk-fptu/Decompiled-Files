/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import java.util.Optional;
import org.hibernate.CacheMode;
import org.hibernate.LockOptions;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;

public interface IdentifierLoadAccess<T> {
    public IdentifierLoadAccess<T> with(LockOptions var1);

    public IdentifierLoadAccess<T> with(CacheMode var1);

    default public IdentifierLoadAccess<T> with(RootGraph<T> graph) {
        return this.with(graph, GraphSemantic.LOAD);
    }

    public IdentifierLoadAccess<T> with(RootGraph<T> var1, GraphSemantic var2);

    public T getReference(Serializable var1);

    public T load(Serializable var1);

    public Optional<T> loadOptional(Serializable var1);
}

