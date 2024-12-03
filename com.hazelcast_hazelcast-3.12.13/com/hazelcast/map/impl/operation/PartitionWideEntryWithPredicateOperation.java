/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.operation.PartitionWideEntryOperation;
import com.hazelcast.map.impl.operation.PartitionWideEntryWithPredicateBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class PartitionWideEntryWithPredicateOperation
extends PartitionWideEntryOperation {
    private Predicate predicate;

    public PartitionWideEntryWithPredicateOperation() {
    }

    public PartitionWideEntryWithPredicateOperation(String name, EntryProcessor entryProcessor, Predicate predicate) {
        super(name, entryProcessor);
        this.predicate = predicate;
    }

    @Override
    protected Predicate getPredicate() {
        return this.predicate;
    }

    @Override
    public Operation getBackupOperation() {
        EntryBackupProcessor backupProcessor = this.entryProcessor.getBackupProcessor();
        PartitionWideEntryWithPredicateBackupOperation backupOperation = null;
        if (backupProcessor != null) {
            backupOperation = new PartitionWideEntryWithPredicateBackupOperation(this.name, backupProcessor, this.predicate);
        }
        return backupOperation;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", predicate=").append(this.predicate);
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
        return 41;
    }
}

