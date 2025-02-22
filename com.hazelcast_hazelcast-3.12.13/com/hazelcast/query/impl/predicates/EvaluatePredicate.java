/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Set;

public final class EvaluatePredicate
implements Predicate,
IndexAwarePredicate {
    private final Predicate predicate;
    private final String indexName;

    public EvaluatePredicate(Predicate predicate, String indexName) {
        this.predicate = predicate;
        this.indexName = indexName;
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    public String getIndexName() {
        return this.indexName;
    }

    @Override
    public boolean apply(Map.Entry mapEntry) {
        return this.predicate.apply(mapEntry);
    }

    public Set<QueryableEntry> filter(QueryContext queryContext) {
        Index index = queryContext.matchIndex(this.indexName, QueryContext.IndexMatchHint.EXACT_NAME);
        if (index == null) {
            return null;
        }
        return index.evaluate(this.predicate);
    }

    @Override
    public boolean isIndexed(QueryContext queryContext) {
        return true;
    }

    public String toString() {
        return "eval(" + this.predicate.toString() + ")";
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        throw new UnsupportedOperationException("can't be serialized");
    }
}

