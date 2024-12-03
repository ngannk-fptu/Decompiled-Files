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
import com.hazelcast.query.impl.predicates.RangePredicate;
import com.hazelcast.query.impl.predicates.Visitor;
import java.io.IOException;
import java.util.Set;

@BinaryInterface
public class BetweenPredicate
extends AbstractIndexAwarePredicate
implements VisitablePredicate,
RangePredicate {
    private static final long serialVersionUID = 1L;
    Comparable to;
    Comparable from;

    public BetweenPredicate() {
    }

    public BetweenPredicate(String attribute, Comparable from, Comparable to) {
        super(attribute);
        if (from == null || to == null) {
            throw new NullPointerException("Arguments can't be null");
        }
        this.from = from;
        this.to = to;
    }

    @Override
    protected boolean applyForSingleAttributeValue(Comparable attributeValue) {
        if (attributeValue == null) {
            return false;
        }
        Comparable fromConvertedValue = this.convert(attributeValue, this.from);
        Comparable toConvertedValue = this.convert(attributeValue, this.to);
        if (fromConvertedValue == null || toConvertedValue == null) {
            return false;
        }
        return Comparables.compare(attributeValue = (Comparable)this.convertEnumValue(attributeValue), fromConvertedValue) >= 0 && Comparables.compare(attributeValue, toConvertedValue) <= 0;
    }

    @Override
    public Set<QueryableEntry> filter(QueryContext queryContext) {
        Index index = this.matchIndex(queryContext, QueryContext.IndexMatchHint.PREFER_ORDERED);
        if (index == null) {
            return null;
        }
        return index.getRecords(this.from, true, this.to, true);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeObject(this.to);
        out.writeObject(this.from);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.to = (Comparable)in.readObject();
        this.from = (Comparable)in.readObject();
    }

    public String toString() {
        return this.attributeName + " BETWEEN " + this.from + " AND " + this.to;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof BetweenPredicate)) {
            return false;
        }
        BetweenPredicate that = (BetweenPredicate)o;
        if (!that.canEqual(this)) {
            return false;
        }
        if (this.to != null ? !this.to.equals(that.to) : that.to != null) {
            return false;
        }
        return this.from != null ? this.from.equals(that.from) : that.from == null;
    }

    @Override
    public boolean canEqual(Object other) {
        return other instanceof BetweenPredicate;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.to != null ? this.to.hashCode() : 0);
        result = 31 * result + (this.from != null ? this.from.hashCode() : 0);
        return result;
    }

    @Override
    public Predicate accept(Visitor visitor, Indexes indexes) {
        return visitor.visit(this, indexes);
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
        return true;
    }

    @Override
    public Comparable getTo() {
        return this.to;
    }

    @Override
    public boolean isToInclusive() {
        return true;
    }
}

