/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.query.impl.Comparables;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.AbstractIndexAwarePredicate;
import com.hazelcast.query.impl.predicates.PredicateUtils;
import com.hazelcast.query.impl.predicates.RangePredicate;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;

public class BoundedRangePredicate
extends AbstractIndexAwarePredicate
implements RangePredicate {
    private final Comparable from;
    private final boolean fromInclusive;
    private final Comparable to;
    private final boolean toInclusive;

    public BoundedRangePredicate(String attribute, Comparable from, boolean fromInclusive, Comparable to, boolean toInclusive) {
        super(attribute);
        if (PredicateUtils.isNull(from) || PredicateUtils.isNull(to)) {
            throw new IllegalArgumentException("range must be bounded");
        }
        this.from = from;
        this.fromInclusive = fromInclusive;
        this.to = to;
        this.toInclusive = toInclusive;
    }

    @Override
    public Set<QueryableEntry> filter(QueryContext queryContext) {
        Index index = this.matchIndex(queryContext, QueryContext.IndexMatchHint.PREFER_ORDERED);
        if (index == null) {
            return null;
        }
        return index.getRecords(this.from, this.fromInclusive, this.to, this.toInclusive);
    }

    @Override
    protected boolean applyForSingleAttributeValue(Comparable value) {
        Comparable from;
        if (value == null) {
            return false;
        }
        Comparable convertedValue = (Comparable)this.convertEnumValue(value);
        int order = Comparables.compare(convertedValue, from = this.convert(value, this.from));
        if (order < 0 || !this.fromInclusive && order == 0) {
            return false;
        }
        Comparable to = this.convert(value, this.to);
        order = Comparables.compare(convertedValue, to);
        return order < 0 || this.toInclusive && order == 0;
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException("can't be serialized");
    }

    @Override
    public String getAttribute() {
        return this.attributeName;
    }

    @Override
    public Comparable getFrom() {
        return this.from;
    }

    @Override
    public boolean isFromInclusive() {
        return this.fromInclusive;
    }

    @Override
    public Comparable getTo() {
        return this.to;
    }

    @Override
    public boolean isToInclusive() {
        return this.toInclusive;
    }

    public String toString() {
        return this.from + (this.fromInclusive ? " >= " : " > ") + this.attributeName + (this.toInclusive ? " <= " : " < ") + this.to;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        throw new UnsupportedOperationException("can't be serialized");
    }
}

