/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.map.EntryBackupProcessor
 *  com.hazelcast.map.EntryProcessor
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  com.hazelcast.nio.serialization.DataSerializable
 */
package com.atlassian.hazelcast;

import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;
import java.util.Map;

public abstract class BaseEntryProcessor<K, V>
implements EntryProcessor<K, V> {
    private final EntryBackupProcessor<K, V> backupProcessor;

    protected BaseEntryProcessor() {
        this(true);
    }

    protected BaseEntryProcessor(boolean applyOnBackup) {
        this.backupProcessor = applyOnBackup ? new DefaultEntryBackupProcessor(this) : null;
    }

    public EntryBackupProcessor<K, V> getBackupProcessor() {
        return this.backupProcessor;
    }

    static class DefaultEntryBackupProcessor<K, V>
    implements EntryBackupProcessor<K, V>,
    DataSerializable {
        private EntryProcessor<K, V> processor;

        DefaultEntryBackupProcessor() {
        }

        DefaultEntryBackupProcessor(EntryProcessor<K, V> processor) {
            this.processor = processor;
        }

        public void processBackup(Map.Entry<K, V> entry) {
            this.processor.process(entry);
        }

        public void readData(ObjectDataInput in) throws IOException {
            this.processor = (EntryProcessor)in.readObject();
        }

        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeObject(this.processor);
        }
    }
}

