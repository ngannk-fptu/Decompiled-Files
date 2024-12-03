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
import com.hazelcast.query.impl.AndResultSet;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.CompoundPredicate;
import com.hazelcast.query.impl.predicates.NegatablePredicate;
import com.hazelcast.query.impl.predicates.NotPredicate;
import com.hazelcast.query.impl.predicates.OrPredicate;
import com.hazelcast.query.impl.predicates.PredicateUtils;
import com.hazelcast.query.impl.predicates.Visitor;
import com.hazelcast.query.impl.predicates.VisitorUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@BinaryInterface
public final class AndPredicate
implements IndexAwarePredicate,
IdentifiedDataSerializable,
VisitablePredicate,
NegatablePredicate,
CompoundPredicate {
    private static final long serialVersionUID = 1L;
    protected Predicate[] predicates;

    public AndPredicate() {
    }

    public AndPredicate(Predicate ... predicates) {
        this.predicates = predicates;
    }

    @Override
    public Predicate accept(Visitor visitor, Indexes indexes) {
        Predicate[] result = VisitorUtils.acceptVisitor(this.predicates, visitor, indexes);
        if (result != this.predicates) {
            AndPredicate newPredicate = new AndPredicate(result);
            return visitor.visit(newPredicate, indexes);
        }
        return visitor.visit(this, indexes);
    }

    public Set<QueryableEntry> filter(QueryContext queryContext) {
        Set<QueryableEntry> smallestResultSet = null;
        List otherResultSets = null;
        List unindexedPredicates = null;
        for (Predicate predicate : this.predicates) {
            if (AndPredicate.isIndexedPredicate(predicate, queryContext)) {
                int ownedPartitionsCount = queryContext.getOwnedPartitionCount();
                queryContext.setOwnedPartitionCount(-1);
                Set<QueryableEntry> currentResultSet = ((IndexAwarePredicate)predicate).filter(queryContext);
                queryContext.setOwnedPartitionCount(ownedPartitionsCount);
                if (smallestResultSet == null) {
                    smallestResultSet = currentResultSet;
                    continue;
                }
                if (PredicateUtils.estimatedSizeOf(currentResultSet) < PredicateUtils.estimatedSizeOf(smallestResultSet)) {
                    otherResultSets = AndPredicate.initOrGetListOf(otherResultSets);
                    otherResultSets.add(smallestResultSet);
                    smallestResultSet = currentResultSet;
                    continue;
                }
                otherResultSets = AndPredicate.initOrGetListOf(otherResultSets);
                otherResultSets.add(currentResultSet);
                continue;
            }
            unindexedPredicates = AndPredicate.initOrGetListOf(unindexedPredicates);
            unindexedPredicates.add(predicate);
        }
        if (smallestResultSet == null) {
            return null;
        }
        return new AndResultSet(smallestResultSet, otherResultSets, unindexedPredicates);
    }

    private static boolean isIndexedPredicate(Predicate predicate, QueryContext queryContext) {
        return predicate instanceof IndexAwarePredicate && ((IndexAwarePredicate)predicate).isIndexed(queryContext);
    }

    private static <T> List<T> initOrGetListOf(List<T> list) {
        if (list == null) {
            list = new LinkedList<T>();
        }
        return list;
    }

    @Override
    public boolean isIndexed(QueryContext queryContext) {
        for (Predicate predicate : this.predicates) {
            IndexAwarePredicate iap;
            if (!(predicate instanceof IndexAwarePredicate) || !(iap = (IndexAwarePredicate)predicate).isIndexed(queryContext)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean apply(Map.Entry mapEntry) {
        for (Predicate predicate : this.predicates) {
            if (predicate.apply(mapEntry)) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        int size = this.predicates.length;
        for (int i = 0; i < size; ++i) {
            if (i > 0) {
                sb.append(" AND ");
            }
            sb.append(this.predicates[i]);
        }
        sb.append(')');
        return sb.toString();
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

    @Override
    public Predicate negate() {
        int size = this.predicates.length;
        Predicate[] inners = new Predicate[size];
        for (int i = 0; i < size; ++i) {
            Predicate original = this.predicates[i];
            Predicate negated = original instanceof NegatablePredicate ? ((NegatablePredicate)((Object)original)).negate() : new NotPredicate(original);
            inners[i] = negated;
        }
        OrPredicate orPredicate = new OrPredicate(inners);
        return orPredicate;
    }

    @Override
    public int getFactoryId() {
        return -32;
    }

    @Override
    public int getId() {
        return 1;
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
            throw new IllegalStateException("Cannot reset predicates in an AndPredicate after they have been already set.");
        }
        this.predicates = predicates;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AndPredicate that = (AndPredicate)o;
        return Arrays.equals(this.predicates, that.predicates);
    }

    public int hashCode() {
        return Arrays.hashCode(this.predicates);
    }
}

