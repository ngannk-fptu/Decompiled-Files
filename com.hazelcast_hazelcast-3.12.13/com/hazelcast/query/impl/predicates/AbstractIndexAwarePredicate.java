/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.predicates.AbstractPredicate;

@BinaryInterface
public abstract class AbstractIndexAwarePredicate<K, V>
extends AbstractPredicate<K, V>
implements IndexAwarePredicate<K, V> {
    protected AbstractIndexAwarePredicate() {
    }

    protected AbstractIndexAwarePredicate(String attributeName) {
        super(attributeName);
    }

    protected Index getIndex(QueryContext queryContext) {
        return this.matchIndex(queryContext, QueryContext.IndexMatchHint.NONE);
    }

    protected Index matchIndex(QueryContext queryContext, QueryContext.IndexMatchHint matchHint) {
        return queryContext.matchIndex(this.attributeName, matchHint);
    }

    @Override
    public boolean isIndexed(QueryContext queryContext) {
        return this.getIndex(queryContext) != null;
    }
}

