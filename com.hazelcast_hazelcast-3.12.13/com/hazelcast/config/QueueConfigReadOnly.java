/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.ItemListenerConfigReadOnly;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QueueStoreConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueConfigReadOnly
extends QueueConfig {
    QueueConfigReadOnly(QueueConfig config) {
        super(config);
    }

    @Override
    public List<ItemListenerConfig> getItemListenerConfigs() {
        List<ItemListenerConfig> itemListenerConfigs = super.getItemListenerConfigs();
        ArrayList<ItemListenerConfigReadOnly> readOnlyItemListenerConfigs = new ArrayList<ItemListenerConfigReadOnly>(itemListenerConfigs.size());
        for (ItemListenerConfig itemListenerConfig : itemListenerConfigs) {
            readOnlyItemListenerConfigs.add(itemListenerConfig.getAsReadOnly());
        }
        return Collections.unmodifiableList(readOnlyItemListenerConfigs);
    }

    @Override
    public QueueStoreConfig getQueueStoreConfig() {
        QueueStoreConfig queueStoreConfig = super.getQueueStoreConfig();
        if (queueStoreConfig == null) {
            return null;
        }
        return queueStoreConfig.getAsReadOnly();
    }

    @Override
    public QueueConfig setName(String name) {
        throw new UnsupportedOperationException("This config is read-only queue: " + this.getName());
    }

    @Override
    public QueueConfig setEmptyQueueTtl(int emptyQueueTtl) {
        throw new UnsupportedOperationException("This config is read-only queue: " + this.getName());
    }

    @Override
    public QueueConfig setMaxSize(int maxSize) {
        throw new UnsupportedOperationException("This config is read-only queue: " + this.getName());
    }

    @Override
    public QueueConfig setBackupCount(int backupCount) {
        throw new UnsupportedOperationException("This config is read-only queue: " + this.getName());
    }

    @Override
    public QueueConfig setAsyncBackupCount(int asyncBackupCount) {
        throw new UnsupportedOperationException("This config is read-only queue: " + this.getName());
    }

    @Override
    public QueueConfig setQueueStoreConfig(QueueStoreConfig queueStoreConfig) {
        throw new UnsupportedOperationException("This config is read-only queue: " + this.getName());
    }

    @Override
    public QueueConfig setStatisticsEnabled(boolean statisticsEnabled) {
        throw new UnsupportedOperationException("This config is read-only queue: " + this.getName());
    }

    @Override
    public QueueConfig addItemListenerConfig(ItemListenerConfig listenerConfig) {
        throw new UnsupportedOperationException("This config is read-only queue: " + this.getName());
    }

    @Override
    public QueueConfig setItemListenerConfigs(List<ItemListenerConfig> listenerConfigs) {
        throw new UnsupportedOperationException("This config is read-only queue: " + this.getName());
    }

    @Override
    public QueueConfig setQuorumName(String quorumName) {
        throw new UnsupportedOperationException("This config is read-only queue: " + this.getName());
    }

    @Override
    public QueueConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        throw new UnsupportedOperationException("This config is read-only queue: " + this.getName());
    }
}

