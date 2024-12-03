/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.EntryEventFilter;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.QueryableEntry;
import java.io.IOException;
import java.util.Map;

public class QueryEventFilter
extends EntryEventFilter {
    private Predicate predicate;

    public QueryEventFilter() {
    }

    public QueryEventFilter(boolean includeValue, Data key, Predicate predicate) {
        super(includeValue, key);
        this.predicate = predicate;
    }

    public Object getPredicate() {
        return this.predicate;
    }

    @Override
    public boolean eval(Object arg) {
        QueryableEntry entry = (QueryableEntry)arg;
        Data keyData = entry.getKeyData();
        return (this.key == null || this.key.equals(keyData)) && this.predicate.apply((Map.Entry)arg);
    }

    @Override
    public int getId() {
        return 102;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeObject(this.predicate);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.predicate = (Predicate)in.readObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        QueryEventFilter that = (QueryEventFilter)o;
        if (!super.equals(o)) {
            return false;
        }
        return this.predicate.equals(that.predicate);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.predicate.hashCode();
    }

    @Override
    public String toString() {
        return "QueryEventFilter{predicate=" + this.predicate + '}';
    }
}

