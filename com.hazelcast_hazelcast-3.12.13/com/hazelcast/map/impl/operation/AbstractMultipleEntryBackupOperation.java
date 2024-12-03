/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.query.Predicate;

abstract class AbstractMultipleEntryBackupOperation
extends MapOperation
implements Versioned {
    EntryBackupProcessor backupProcessor;

    public AbstractMultipleEntryBackupOperation() {
    }

    public AbstractMultipleEntryBackupOperation(String name, EntryBackupProcessor backupProcessor) {
        super(name);
        this.backupProcessor = backupProcessor;
    }

    protected Predicate getPredicate() {
        return null;
    }
}

