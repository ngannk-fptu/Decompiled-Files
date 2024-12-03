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
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.VisitablePredicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.OrResultSet;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.AndPredicate;
import com.hazelcast.query.impl.predicates.CompoundPredicate;
import com.hazelcast.query.impl.predicates.NegatablePredicate;
import com.hazelcast.query.impl.predicates.NotPredicate;
import com.hazelcast.query.impl.predicates.Visitor;
import com.hazelcast.query.impl.predicates.VisitorUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

@BinaryInterface
public final class OrPredicate
implements IndexAwarePredicate,
VisitablePredicate,
NegatablePredicate,
IdentifiedDataSerializable,
CompoundPredicate {
    private static final long serialVersionUID = 1L;
    protected Predicate[] predicates;

    public OrPredicate() {
    }

    public OrPredicate(Predicate ... predicates) {
        this.predicates = predicates;
    }

    public Set<QueryableEntry> filter(QueryContext queryContext) {
        LinkedList<Set<QueryableEntry>> indexedResults = new LinkedList<Set<QueryableEntry>>();
        for (Predicate predicate : this.predicates) {
            if (!(predicate instanceof IndexAwarePredicate)) continue;
            IndexAwarePredicate iap = (IndexAwarePredicate)predicate;
            if (iap.isIndexed(queryContext)) {
                int ownedPartitionsCount = queryContext.getOwnedPartitionCount();
                queryContext.setOwnedPartitionCount(-1);
                Set s = iap.filter(queryContext);
                queryContext.setOwnedPartitionCount(ownedPartitionsCount);
                if (s == null) continue;
                indexedResults.add(s);
                continue;
            }
            return null;
        }
        return indexedResults.isEmpty() ? null : new OrResultSet(indexedResults);
    }

    @Override
    public boolean isIndexed(QueryContext queryContext) {
        for (Predicate predicate : this.predicates) {
            if (predicate instanceof IndexAwarePredicate) {
                IndexAwarePredicate iap = (IndexAwarePredicate)predicate;
                if (iap.isIndexed(queryContext)) continue;
                return false;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean apply(Map.Entry mapEntry) {
        for (Predicate predicate : this.predicates) {
            if (!predicate.apply(mapEntry)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.predicates.length);
        for (Predicate predicate : this.predicates) {
            out.writeObject(predicate);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.predicates = new Predicate[size];
        for (int i = 0; i < size; ++i) {
            this.predicates[i] = (Predicate)in.readObject();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        int size = this.predicates.length;
        for (int i = 0; i < size; ++i) {
            if (i > 0) {
                sb.append(" OR ");
            }
            sb.append(this.predicates[i]);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Predicate accept(Visitor visitor, Indexes indexes) {
        Predicate[] result = VisitorUtils.acceptVisitor(this.predicates, visitor, indexes);
        if (result != this.predicates) {
            return visitor.visit(new OrPredicate(result), indexes);
        }
        return visitor.visit(this, indexes);
    }

    @Override
    public Predicate negate() {
        int size = this.predicates.length;
        Predicate[] inners = new Predicate[size];
        for (int i = 0; i < size; ++i) {
            Predicate original = this.predicates[i];
            Predicate negated = original instanceof NegatablePredicate ? ((NegatablePredicate)((Object)original)).negate() : new NotPredicate(original);
            inners[i] = negated;
        }
        AndPredicate andPredicate = new AndPredicate(inners);
        return andPredicate;
    }

    @Override
    public int getFactoryId() {
        return -32;
    }

    @Override
    public int getId() {
        return 11;
    }

    @Override
    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public <K, V> Predicate<K, V>[] getPredicates() {
        return this.predicates;
    }

    @Override
    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public <K, V> void setPredicates(Predicate<K, V>[] predicates) {
        if (this.predicates != null) {
            throw new IllegalStateException("Cannot reset predicates in an OrPredicate after they have been already set.");
        }
        this.predicates = predicates;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof OrPredicate)) {
            return false;
        }
        OrPredicate that = (OrPredicate)o;
        return Arrays.equals(this.predicates, that.predicates);
    }

    public int hashCode() {
        return Arrays.hashCode(this.predicates);
    }
}

