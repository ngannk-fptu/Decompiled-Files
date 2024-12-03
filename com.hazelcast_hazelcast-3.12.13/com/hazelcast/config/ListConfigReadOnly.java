/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.ItemListenerConfigReadOnly;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.MergePolicyConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListConfigReadOnly
extends ListConfig {
    public ListConfigReadOnly(ListConfig config) {
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
    public ListConfig setName(String name) {
        throw new UnsupportedOperationException("This config is read-only list: " + this.getName());
    }

    @Override
    public ListConfig setItemListenerConfigs(List<ItemListenerConfig> listenerConfigs) {
        throw new UnsupportedOperationException("This config is read-only list: " + this.getName());
    }

    @Override
    public ListConfig setBackupCount(int backupCount) {
        throw new UnsupportedOperationException("This config is read-only list: " + this.getName());
    }

    @Override
    public ListConfig setAsyncBackupCount(int asyncBackupCount) {
        throw new UnsupportedOperationException("This config is read-only list: " + this.getName());
    }

    @Override
    public ListConfig setMaxSize(int maxSize) {
        throw new UnsupportedOperationException("This config is read-only list: " + this.getName());
    }

    @Override
    public ListConfig setStatisticsEnabled(boolean statisticsEnabled) {
        throw new UnsupportedOperationException("This config is read-only list: " + this.getName());
    }

    @Override
    public ListConfig setMergePolicyConfig(MergePolicyConfig mergePolicyConfig) {
        throw new UnsupportedOperationException("This config is read-only set: " + this.getName());
    }

    @Override
    public void addItemListenerConfig(ItemListenerConfig itemListenerConfig) {
        throw new UnsupportedOperationException("This config is read-only list: " + this.getName());
    }

    @Override
    public ListConfig setQuorumName(String quorumName) {
        throw new UnsupportedOperationException("This config is read-only list: " + this.getName());
    }
}

