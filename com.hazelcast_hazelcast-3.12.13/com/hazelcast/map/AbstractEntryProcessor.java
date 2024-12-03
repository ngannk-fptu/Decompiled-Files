/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.nio.serialization.SerializableByConvention;
import java.util.Map;

public abstract class AbstractEntryProcessor<K, V>
implements EntryProcessor<K, V> {
    private final EntryBackupProcessor<K, V> entryBackupProcessor;

    public AbstractEntryProcessor() {
        this(true);
    }

    public AbstractEntryProcessor(boolean applyOnBackup) {
        this.entryBackupProcessor = applyOnBackup ? new EntryBackupProcessorImpl() : null;
    }

    @Override
    public final EntryBackupProcessor<K, V> getBackupProcessor() {
        return this.entryBackupProcessor;
    }

    @SerializableByConvention(value=SerializableByConvention.Reason.PUBLIC_API)
    private class EntryBackupProcessorImpl
    implements EntryBackupProcessor<K, V>,
    HazelcastInstanceAware {
        static final long serialVersionUID = -5081502753526394129L;

        private EntryBackupProcessorImpl() {
        }

        @Override
        public void processBackup(Map.Entry<K, V> entry) {
            AbstractEntryProcessor.this.process(entry);
        }

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            AbstractEntryProcessor outer = AbstractEntryProcessor.this;
            if (outer instanceof HazelcastInstanceAware) {
                ((HazelcastInstanceAware)((Object)outer)).setHazelcastInstance(hazelcastInstance);
            }
        }
    }
}

