/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.CompositeValue;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@SuppressFBWarnings(value={"SE_BAD_FIELD"})
public class CompositeEqualPredicate
implements Predicate,
IndexAwarePredicate {
    final String indexName;
    final String[] components;
    final CompositeValue value;
    private volatile Predicate fallbackPredicate;

    public CompositeEqualPredicate(InternalIndex index, CompositeValue value) {
        this.indexName = index.getName();
        this.components = index.getComponents();
        this.value = value;
    }

    CompositeEqualPredicate(String indexName, String[] components, CompositeValue value) {
        this.indexName = indexName;
        this.components = components;
        this.value = value;
    }

    @Override
    public boolean apply(Map.Entry entry) {
        if (this.fallbackPredicate == null) {
            this.generateFallbackPredicate();
        }
        return this.fallbackPredicate.apply(entry);
    }

    public Set<QueryableEntry> filter(QueryContext queryContext) {
        Index index = queryContext.matchIndex(this.indexName, QueryContext.IndexMatchHint.EXACT_NAME);
        if (index == null) {
            return null;
        }
        return index.getRecords(this.value);
    }

    public String toString() {
        return Arrays.toString(this.components) + " = " + this.value;
    }

    @Override
    public boolean isIndexed(QueryContext queryContext) {
        return true;
    }

    private void generateFallbackPredicate() {
        Comparable[] values = this.value.getComponents();
        Predicate[] predicates = new Predicate[this.components.length];
        for (int i = 0; i < this.components.length; ++i) {
            predicates[i] = new EqualPredicate(this.components[i], values[i]);
        }
        this.fallbackPredicate = new AndPredicate(predicates);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        throw new UnsupportedOperationException("can't be serialized");
    }
}

