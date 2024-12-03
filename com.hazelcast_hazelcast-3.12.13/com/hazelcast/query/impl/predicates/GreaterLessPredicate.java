/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Comparables;
import com.hazelcast.query.impl.Comparison;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.AbstractIndexAwarePredicate;
import com.hazelcast.query.impl.predicates.NegatablePredicate;
import com.hazelcast.query.impl.predicates.RangePredicate;
import java.io.IOException;
import java.util.Set;

@BinaryInterface
public final class GreaterLessPredicate
extends AbstractIndexAwarePredicate
implements NegatablePredicate,
RangePredicate {
    private static final long serialVersionUID = 1L;
    protected Comparable value;
    boolean equal;
    boolean less;

    public GreaterLessPredicate() {
    }

    public GreaterLessPredicate(String attribute, Comparable value, boolean equal, boolean less) {
        super(attribute);
        if (value == null) {
            throw new NullPointerException("Arguments can't be null");
        }
        this.value = value;
        this.equal = equal;
        this.less = less;
    }

    @Override
    protected boolean applyForSingleAttributeValue(Comparable attributeValue) {
        if (attributeValue == null) {
            return false;
        }
        Comparable givenValue = this.convert(attributeValue, this.value);
        attributeValue = (Comparable)this.convertEnumValue(attributeValue);
        int result = Comparables.compare(attributeValue, givenValue);
        return this.equal && result == 0 || (this.less ? result < 0 : result > 0);
    }

    @Override
    public Set<QueryableEntry> filter(QueryContext queryContext) {
        Index index = this.matchIndex(queryContext, QueryContext.IndexMatchHint.PREFER_ORDERED);
        if (index == null) {
            return null;
        }
        Comparison comparison = this.less ? (this.equal ? Comparison.LESS_OR_EQUAL : Comparison.LESS) : (this.equal ? Comparison.GREATER_OR_EQUAL : Comparison.GREATER);
        return index.getRecords(comparison, this.value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.value = (Comparable)in.readObject();
        this.equal = in.readBoolean();
        this.less = in.readBoolean();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeObject(this.value);
        out.writeBoolean(this.equal);
        out.writeBoolean(this.less);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.attributeName);
        sb.append(this.less ? "<" : ">");
        if (this.equal) {
            sb.append("=");
        }
        sb.append(this.value);
        return sb.toString();
    }

    @Override
    public Predicate negate() {
        return new GreaterLessPredicate(this.attributeName, this.value, !this.equal, !this.less);
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof GreaterLessPredicate)) {
            return false;
        }
        GreaterLessPredicate that = (GreaterLessPredicate)o;
        if (!that.canEqual(this)) {
            return false;
        }
        if (this.equal != that.equal) {
            return false;
        }
        if (this.less != that.less) {
            return false;
        }
        return this.value != null ? this.value.equals(that.value) : that.value == null;
    }

    @Override
    public boolean canEqual(Object other) {
        return other instanceof GreaterLessPredicate;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        result = 31 * result + (this.equal ? 1 : 0);
        result = 31 * result + (this.less ? 1 : 0);
        return result;
    }

    @Override
    public String getAttribute() {
        return this.attributeName;
    }

    @Override
    public Comparable getFrom() {
        return this.less ? null : this.value;
    }

    @Override
    public boolean isFromInclusive() {
        return !this.less && this.equal;
    }

    @Override
    public Comparable getTo() {
        return this.less ? this.value : null;
    }

    @Override
    public boolean isToInclusive() {
        return this.less && this.equal;
    }
}

