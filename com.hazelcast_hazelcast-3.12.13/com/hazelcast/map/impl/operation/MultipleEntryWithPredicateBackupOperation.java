/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.impl.operation.MultipleEntryBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.Set;

public class MultipleEntryWithPredicateBackupOperation
extends MultipleEntryBackupOperation {
    private Predicate predicate;

    public MultipleEntryWithPredicateBackupOperation() {
    }

    public MultipleEntryWithPredicateBackupOperation(String name, Set<Data> keys, EntryBackupProcessor backupProcessor, Predicate predicate) {
        super(name, keys, backupProcessor);
        this.predicate = Preconditions.checkNotNull(predicate, "predicate cannot be null");
    }

    @Override
    public Predicate getPredicate() {
        return this.predicate;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.predicate);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.predicate = (Predicate)in.readObject();
    }

    @Override
    public int getId() {
        return 53;
    }
}

