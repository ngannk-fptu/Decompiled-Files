/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.VisitablePredicate;
import com.hazelcast.query.impl.Comparables;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.AbstractIndexAwarePredicate;
import com.hazelcast.query.impl.predicates.NegatablePredicate;
import com.hazelcast.query.impl.predicates.NotEqualPredicate;
import com.hazelcast.query.impl.predicates.PredicateUtils;
import com.hazelcast.query.impl.predicates.RangePredicate;
import com.hazelcast.query.impl.predicates.Visitor;
import java.io.IOException;
import java.util.Set;

@BinaryInterface
public class EqualPredicate
extends AbstractIndexAwarePredicate
implements NegatablePredicate,
RangePredicate,
VisitablePredicate {
    private static final long serialVersionUID = 1L;
    protected Comparable value;

    public EqualPredicate() {
    }

    public EqualPredicate(String attribute) {
        super(attribute);
    }

    public EqualPredicate(String attribute, Comparable value) {
        super(attribute);
        this.value = value;
    }

    @Override
    public Predicate accept(Visitor visitor, Indexes indexes) {
        return visitor.visit(this, indexes);
    }

    @Override
    public Set<QueryableEntry> filter(QueryContext queryContext) {
        Index index = this.matchIndex(queryContext, QueryContext.IndexMatchHint.PREFER_UNORDERED);
        if (index == null) {
            return null;
        }
        return index.getRecords(this.value);
    }

    @Override
    protected boolean applyForSingleAttributeValue(Comparable attributeValue) {
        if (attributeValue == null) {
            return PredicateUtils.isNull(this.value);
        }
        this.value = this.convert(attributeValue, this.value);
        attributeValue = (Comparable)this.convertEnumValue(attributeValue);
        return Comparables.equal(attributeValue, this.value);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeObject(this.value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.value = (Comparable)in.readObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof EqualPredicate)) {
            return false;
        }
        EqualPredicate that = (EqualPredicate)o;
        if (!that.canEqual(this)) {
            return false;
        }
        return this.value != null ? this.value.equals(that.value) : that.value == null;
    }

    @Override
    public boolean canEqual(Object other) {
        return other instanceof EqualPredicate;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return this.attributeName + "=" + this.value;
    }

    @Override
    public Predicate negate() {
        return new NotEqualPredicate(this.attributeName, this.value);
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public String getAttribute() {
        return this.attributeName;
    }

    @Override
    public Comparable getFrom() {
        return this.value;
    }

    @Override
    public boolean isFromInclusive() {
        return true;
    }

    @Override
    public Comparable getTo() {
        return this.value;
    }

    @Override
    public boolean isToInclusive() {
        return true;
    }
}

