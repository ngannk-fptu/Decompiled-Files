/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.CompositeValue;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.BoundedRangePredicate;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.query.impl.predicates.GreaterLessPredicate;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@SuppressFBWarnings(value={"SE_BAD_FIELD"})
public class CompositeRangePredicate
implements Predicate,
IndexAwarePredicate {
    final String indexName;
    final String[] components;
    final CompositeValue from;
    final boolean fromInclusive;
    final CompositeValue to;
    final boolean toInclusive;
    private final int prefixLength;
    private volatile Predicate fallbackPredicate;

    public CompositeRangePredicate(InternalIndex index, CompositeValue from, boolean fromInclusive, CompositeValue to, boolean toInclusive, int prefixLength) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("range must be bounded");
        }
        this.indexName = index.getName();
        this.components = index.getComponents();
        this.from = from;
        this.fromInclusive = fromInclusive;
        this.to = to;
        this.toInclusive = toInclusive;
        this.prefixLength = prefixLength;
    }

    CompositeRangePredicate(String indexName, String[] components, CompositeValue from, boolean fromInclusive, CompositeValue to, boolean toInclusive, int prefixLength) {
        this.indexName = indexName;
        this.components = components;
        this.from = from;
        this.fromInclusive = fromInclusive;
        this.to = to;
        this.toInclusive = toInclusive;
        this.prefixLength = prefixLength;
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
        return index.getRecords(this.from, this.fromInclusive, this.to, this.toInclusive);
    }

    @Override
    public boolean isIndexed(QueryContext queryContext) {
        return true;
    }

    public String toString() {
        return Arrays.toString(this.components) + " in " + (this.fromInclusive ? "[" : "(") + this.from + ", " + this.to + (this.toInclusive ? "]" : ")");
    }

    private void generateFallbackPredicate() {
        Comparable[] fromValues = this.from.getComponents();
        Comparable[] toValues = this.to.getComponents();
        Comparable comparisonFrom = fromValues[this.prefixLength];
        Comparable comparisonTo = toValues[this.prefixLength];
        boolean hasComparison = CompositeRangePredicate.isFinite(comparisonFrom) || CompositeRangePredicate.isFinite(comparisonTo);
        Predicate[] predicates = new Predicate[hasComparison ? this.prefixLength + 1 : this.prefixLength];
        for (int i = 0; i < this.prefixLength; ++i) {
            assert (fromValues[i] == toValues[i]);
            predicates[i] = new EqualPredicate(this.components[i], fromValues[i]);
        }
        if (hasComparison) {
            boolean comparisonToInclusive;
            String comparisonComponent = this.components[this.prefixLength];
            boolean comparisonFromInclusive = this.fromInclusive || this.prefixLength < this.components.length - 1 && fromValues[this.prefixLength + 1] == CompositeValue.NEGATIVE_INFINITY;
            boolean bl = comparisonToInclusive = this.toInclusive || this.prefixLength < this.components.length - 1 && toValues[this.prefixLength + 1] == CompositeValue.POSITIVE_INFINITY;
            predicates[this.prefixLength] = CompositeRangePredicate.isFinite(comparisonFrom) && CompositeRangePredicate.isFinite(comparisonTo) ? new BoundedRangePredicate(comparisonComponent, comparisonFrom, comparisonFromInclusive, comparisonTo, comparisonToInclusive) : (CompositeRangePredicate.isFinite(comparisonFrom) ? new GreaterLessPredicate(comparisonComponent, comparisonFrom, comparisonFromInclusive, false) : new GreaterLessPredicate(comparisonComponent, comparisonTo, comparisonToInclusive, true));
        }
        this.fallbackPredicate = new AndPredicate(predicates);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        throw new UnsupportedOperationException("can't be serialized");
    }

    private static boolean isFinite(Comparable value) {
        return value != AbstractIndex.NULL && value != CompositeValue.NEGATIVE_INFINITY && value != CompositeValue.POSITIVE_INFINITY;
    }
}

