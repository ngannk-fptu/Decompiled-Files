/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.impl.operation.PartitionWideEntryBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.query.Predicate;
import java.io.IOException;

public class PartitionWideEntryWithPredicateBackupOperation
extends PartitionWideEntryBackupOperation {
    private Predicate predicate;

    public PartitionWideEntryWithPredicateBackupOperation() {
    }

    public PartitionWideEntryWithPredicateBackupOperation(String name, EntryBackupProcessor entryProcessor, Predicate predicate) {
        super(name, entryProcessor);
        this.predicate = predicate;
    }

    @Override
    protected Predicate getPredicate() {
        return this.predicate;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.predicate = (Predicate)in.readObject();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.predicate);
    }

    @Override
    public int getId() {
        return 42;
    }
}

