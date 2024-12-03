/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Serializable;
import java.util.List;
import org.hibernate.CacheMode;
import org.hibernate.LockOptions;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;

public interface MultiIdentifierLoadAccess<T> {
    public MultiIdentifierLoadAccess<T> with(LockOptions var1);

    public MultiIdentifierLoadAccess<T> with(CacheMode var1);

    default public MultiIdentifierLoadAccess<T> with(RootGraph<T> graph) {
        return this.with(graph, GraphSemantic.LOAD);
    }

    public MultiIdentifierLoadAccess<T> with(RootGraph<T> var1, GraphSemantic var2);

    public MultiIdentifierLoadAccess<T> withBatchSize(int var1);

    public MultiIdentifierLoadAccess<T> enableSessionCheck(boolean var1);

    public MultiIdentifierLoadAccess<T> enableReturnOfDeletedEntities(boolean var1);

    public MultiIdentifierLoadAccess<T> enableOrderedReturn(boolean var1);

    public <K extends Serializable> List<T> multiLoad(K ... var1);

    public <K extends Serializable> List<T> multiLoad(List<K> var1);
}

