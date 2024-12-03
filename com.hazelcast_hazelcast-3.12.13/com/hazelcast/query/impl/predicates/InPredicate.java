/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
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
import com.hazelcast.query.impl.predicates.PredicateUtils;
import com.hazelcast.query.impl.predicates.Visitor;
import com.hazelcast.util.SetUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

@BinaryInterface
public class InPredicate
extends AbstractIndexAwarePredicate
implements VisitablePredicate {
    private static final long serialVersionUID = 1L;
    Comparable[] values;
    private volatile transient Set<Comparable> convertedInValues;
    private volatile transient Boolean valuesContainNull;

    public InPredicate() {
    }

    public InPredicate(String attribute, Comparable ... values) {
        super(attribute);
        if (values == null) {
            throw new NullPointerException("Array can't be null");
        }
        this.values = values;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public Comparable[] getValues() {
        return this.values;
    }

    @Override
    public Predicate accept(Visitor visitor, Indexes indexes) {
        return visitor.visit(this, indexes);
    }

    @Override
    protected boolean applyForSingleAttributeValue(Comparable attributeValue) {
        Set<Comparable<Object>> set = this.convertedInValues;
        if (attributeValue == null && set == null) {
            Boolean valuesContainNull = this.valuesContainNull;
            if (valuesContainNull != null) {
                return valuesContainNull;
            }
            for (Comparable value : this.values) {
                if (!PredicateUtils.isNull(value)) continue;
                this.valuesContainNull = true;
                return true;
            }
            this.valuesContainNull = false;
            return false;
        }
        attributeValue = (Comparable)this.convertEnumValue(attributeValue);
        if (set == null) {
            set = SetUtil.createHashSet(this.values.length);
            for (Comparable value : this.values) {
                Comparable converted = this.convert(attributeValue, value);
                if (PredicateUtils.isNull(converted)) {
                    converted = null;
                }
                set.add(Comparables.canonicalizeForHashLookup(converted));
            }
            this.convertedInValues = set;
        }
        return set.contains(Comparables.canonicalizeForHashLookup(attributeValue));
    }

    @Override
    public Set<QueryableEntry> filter(QueryContext queryContext) {
        Index index = this.matchIndex(queryContext, QueryContext.IndexMatchHint.PREFER_UNORDERED);
        if (index != null) {
            return index.getRecords(this.values);
        }
        return null;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeInt(this.values.length);
        for (Comparable value : this.values) {
            out.writeObject(value);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        int len = in.readInt();
        this.values = new Comparable[len];
        for (int i = 0; i < len; ++i) {
            this.values[i] = (Comparable)in.readObject();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.attributeName);
        sb.append(" IN (");
        for (int i = 0; i < this.values.length; ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(this.values[i]);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof InPredicate)) {
            return false;
        }
        InPredicate that = (InPredicate)o;
        if (!that.canEqual(this)) {
            return false;
        }
        return Arrays.equals(this.values, that.values);
    }

    @Override
    public boolean canEqual(Object other) {
        return other instanceof InPredicate;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(this.values);
        return result;
    }
}

