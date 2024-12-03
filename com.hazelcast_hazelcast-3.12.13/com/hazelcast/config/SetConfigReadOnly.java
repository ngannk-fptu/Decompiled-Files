/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.ItemListenerConfigReadOnly;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.config.SetConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetConfigReadOnly
extends SetConfig {
    public SetConfigReadOnly(SetConfig config) {
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
    public SetConfig setName(String name) {
        throw new UnsupportedOperationException("This config is read-only set: " + this.getName());
    }

    @Override
    public SetConfig setItemListenerConfigs(List<ItemListenerConfig> listenerConfigs) {
        throw new UnsupportedOperationException("This config is read-only set: " + this.getName());
    }

    @Override
    public SetConfig setBackupCount(int backupCount) {
        throw new UnsupportedOperationException("This config is read-only set: " + this.getName());
    }

    @Override
    public SetConfig setAsyncBackupCount(int asyncBackupCount) {
        throw new UnsupportedOperationException("This config is read-only set: " + this.getName());
    }

    @Override
    public SetConfig setMaxSize(int maxSize) {
        throw new UnsupportedOperationException("This config is read-only set: " + this.getName());
    }

    @Override
    public SetConfig setStatisticsEnabled(boolean statisticsEnabled) {
        throw new UnsupportedOperationException("This config is read-only set: " + this.getName());
    }

    @Override
    public SetConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        throw new UnsupportedOperationException("This config is read-only set: " + this.getName());
    }

    @Override
    public void addItemListenerConfig(ItemListenerConfig itemListenerConfig) {
        throw new UnsupportedOperationException("This config is read-only set: " + this.getName());
    }

    @Override
    public SetConfig setQuorumName(String quorumName) {
        throw new UnsupportedOperationException("This config is read-only set: " + this.getName());
    }
}

