/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.EqualPredicate;
import com.hazelcast.query.impl.predicates.Visitor;
import java.util.Map;
import java.util.Set;

@BinaryInterface
public final class NotEqualPredicate
extends EqualPredicate {
    private static final long serialVersionUID = 1L;

    public NotEqualPredicate() {
    }

    public NotEqualPredicate(String attribute, Comparable value) {
        super(attribute, value);
    }

    @Override
    public Predicate accept(Visitor visitor, Indexes indexes) {
        return visitor.visit(this, indexes);
    }

    @Override
    public boolean apply(Map.Entry entry) {
        return !super.apply(entry);
    }

    @Override
    public boolean isIndexed(QueryContext queryContext) {
        return false;
    }

    @Override
    public Set<QueryableEntry> filter(QueryContext queryContext) {
        return null;
    }

    @Override
    public String toString() {
        return this.attributeName + " != " + this.value;
    }

    @Override
    public Predicate negate() {
        return new EqualPredicate(this.attributeName, this.value);
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof NotEqualPredicate)) {
            return false;
        }
        NotEqualPredicate that = (NotEqualPredicate)o;
        return that.canEqual(this);
    }

    @Override
    public boolean canEqual(Object other) {
        return other instanceof NotEqualPredicate;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

