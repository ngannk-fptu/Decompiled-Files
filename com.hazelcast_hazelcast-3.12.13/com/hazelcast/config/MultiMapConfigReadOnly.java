/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.EntryListenerConfigReadOnly;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.MultiMapConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiMapConfigReadOnly
extends MultiMapConfig {
    public MultiMapConfigReadOnly(MultiMapConfig defConfig) {
        super(defConfig);
    }

    @Override
    public List<EntryListenerConfig> getEntryListenerConfigs() {
        List<EntryListenerConfig> listenerConfigs = super.getEntryListenerConfigs();
        ArrayList<EntryListenerConfigReadOnly> readOnlyListenerConfigs = new ArrayList<EntryListenerConfigReadOnly>(listenerConfigs.size());
        for (EntryListenerConfig listenerConfig : listenerConfigs) {
            readOnlyListenerConfigs.add(listenerConfig.getAsReadOnly());
        }
        return Collections.unmodifiableList(readOnlyListenerConfigs);
    }

    @Override
    public MultiMapConfig setName(String name) {
        throw new UnsupportedOperationException("This config is read-only multimap: " + this.getName());
    }

    @Override
    public MultiMapConfig setValueCollectionType(String valueCollectionType) {
        throw new UnsupportedOperationException("This config is read-only multimap: " + this.getName());
    }

    @Override
    public MultiMapConfig setValueCollectionType(MultiMapConfig.ValueCollectionType valueCollectionType) {
        throw new UnsupportedOperationException("This config is read-only multimap: " + this.getName());
    }

    @Override
    public MultiMapConfig addEntryListenerConfig(EntryListenerConfig listenerConfig) {
        throw new UnsupportedOperationException("This config is read-only multimap: " + this.getName());
    }

    @Override
    public MultiMapConfig setEntryListenerConfigs(List<EntryListenerConfig> listenerConfigs) {
        throw new UnsupportedOperationException("This config is read-only multimap: " + this.getName());
    }

    @Override
    public MultiMapConfig setBinary(boolean binary) {
        throw new UnsupportedOperationException("This config is read-only multimap: " + this.getName());
    }

    @Override
    public MultiMapConfig setSyncBackupCount(int syncBackupCount) {
        throw new UnsupportedOperationException("This config is read-only multimap: " + this.getName());
    }

    @Override
    public MultiMapConfig setBackupCount(int backupCount) {
        throw new UnsupportedOperationException("This config is read-only multimap: " + this.getName());
    }

    @Override
    public MultiMapConfig setAsyncBackupCount(int asyncBackupCount) {
        throw new UnsupportedOperationException("This config is read-only multimap: " + this.getName());
    }

    @Override
    public MultiMapConfig setStatisticsEnabled(boolean statisticsEnabled) {
        throw new UnsupportedOperationException("This config is read-only multimap: " + this.getName());
    }

    @Override
    public MultiMapConfig setQuorumName(String quorumName) {
        throw new UnsupportedOperationException("This config is read-only multimap: " + this.getName());
    }

    @Override
    public MultiMapConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        throw new UnsupportedOperationException("This config is read-only multimap: " + this.getName());
    }
}

