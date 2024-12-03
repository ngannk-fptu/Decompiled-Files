/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.impl.operation.EntryOperator;
import com.hazelcast.map.impl.operation.KeyBasedMapOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import java.io.IOException;

public class EntryBackupOperation
extends KeyBasedMapOperation
implements BackupOperation {
    private EntryBackupProcessor entryProcessor;

    public EntryBackupOperation() {
    }

    public EntryBackupOperation(String name, Data dataKey, EntryBackupProcessor entryProcessor) {
        super(name, dataKey);
        this.entryProcessor = entryProcessor;
    }

    @Override
    public void innerBeforeRun() throws Exception {
        super.innerBeforeRun();
        if (this.entryProcessor instanceof HazelcastInstanceAware) {
            HazelcastInstance hazelcastInstance = this.getNodeEngine().getHazelcastInstance();
            ((HazelcastInstanceAware)((Object)this.entryProcessor)).setHazelcastInstance(hazelcastInstance);
        }
    }

    @Override
    public void run() {
        EntryOperator.operator(this, this.entryProcessor).operateOnKey(this.dataKey).doPostOperateOps();
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.entryProcessor = (EntryBackupProcessor)in.readObject();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.entryProcessor);
    }

    @Override
    public int getId() {
        return 19;
    }
}

