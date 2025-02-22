/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@BinaryInterface
public class PredicateBuilder
implements IndexAwarePredicate,
DataSerializable {
    List<Predicate> lsPredicates = new ArrayList<Predicate>();
    private String attribute;

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public boolean apply(Map.Entry mapEntry) {
        return this.lsPredicates.get(0).apply(mapEntry);
    }

    public EntryObject getEntryObject() {
        return new EntryObject(this);
    }

    public PredicateBuilder and(Predicate predicate) {
        if (predicate != this) {
            throw new QueryException("Illegal and statement expected: " + PredicateBuilder.class.getSimpleName() + ", found: " + (predicate == null ? "null" : predicate.getClass().getSimpleName()));
        }
        int index = this.lsPredicates.size() - 2;
        Predicate first = this.lsPredicates.remove(index);
        Predicate second = this.lsPredicates.remove(index);
        this.lsPredicates.add(Predicates.and(first, second));
        return this;
    }

    public PredicateBuilder or(Predicate predicate) {
        if (predicate != this) {
            throw new RuntimeException("Illegal or statement expected: " + PredicateBuilder.class.getSimpleName() + ", found: " + (predicate == null ? "null" : predicate.getClass().getSimpleName()));
        }
        int index = this.lsPredicates.size() - 2;
        Predicate first = this.lsPredicates.remove(index);
        Predicate second = this.lsPredicates.remove(index);
        this.lsPredicates.add(Predicates.or(first, second));
        return this;
    }

    public Set<QueryableEntry> filter(QueryContext queryContext) {
        Predicate p = this.lsPredicates.get(0);
        if (p instanceof IndexAwarePredicate) {
            return ((IndexAwarePredicate)p).filter(queryContext);
        }
        return null;
    }

    @Override
    public boolean isIndexed(QueryContext queryContext) {
        Predicate p = this.lsPredicates.get(0);
        if (p instanceof IndexAwarePredicate) {
            return ((IndexAwarePredicate)p).isIndexed(queryContext);
        }
        return false;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attribute);
        out.writeInt(this.lsPredicates.size());
        for (Predicate predicate : this.lsPredicates) {
            out.writeObject(predicate);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attribute = in.readUTF();
        int size = in.readInt();
        this.lsPredicates = new ArrayList<Predicate>(size);
        for (int i = 0; i < size; ++i) {
            this.lsPredicates.add((Predicate)in.readObject());
        }
    }

    public String toString() {
        return "PredicateBuilder{\n" + (this.lsPredicates.size() == 0 ? "" : (Serializable)this.lsPredicates.get(0)) + "\n}";
    }
}

