/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.operation.MultipleEntryOperation;
import com.hazelcast.map.impl.operation.MultipleEntryWithPredicateBackupOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.Set;

public class MultipleEntryWithPredicateOperation
extends MultipleEntryOperation {
    private Predicate predicate;

    public MultipleEntryWithPredicateOperation() {
    }

    public MultipleEntryWithPredicateOperation(String name, Set<Data> keys, EntryProcessor entryProcessor, Predicate predicate) {
        super(name, keys, entryProcessor);
        this.predicate = Preconditions.checkNotNull(predicate, "predicate cannot be null");
    }

    @Override
    public Predicate getPredicate() {
        return this.predicate;
    }

    @Override
    public Operation getBackupOperation() {
        EntryBackupProcessor backupProcessor = this.entryProcessor.getBackupProcessor();
        return new MultipleEntryWithPredicateBackupOperation(this.name, this.keys, backupProcessor, this.predicate);
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
        return 54;
    }
}

