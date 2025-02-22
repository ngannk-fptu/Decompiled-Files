/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.set;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.config.SetConfig;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetContainer
extends CollectionContainer {
    private static final int INITIAL_CAPACITY = 1000;
    private Set<CollectionItem> itemSet;
    private SetConfig config;

    public SetContainer() {
    }

    public SetContainer(String name, NodeEngine nodeEngine) {
        super(name, nodeEngine);
    }

    @Override
    public SetConfig getConfig() {
        if (this.config == null) {
            this.config = this.nodeEngine.getConfig().findSetConfig(this.name);
        }
        return this.config;
    }

    @Override
    public Map<Long, Data> addAll(List<Data> valueList) {
        int size = valueList.size();
        Map<Long, Data> map = MapUtil.createHashMap(size);
        ArrayList<CollectionItem> list = new ArrayList<CollectionItem>(size);
        for (Data value : valueList) {
            long itemId = this.nextId();
            CollectionItem item = new CollectionItem(itemId, value);
            if (this.getCollection().contains(item)) continue;
            list.add(item);
            map.put(itemId, value);
        }
        this.getCollection().addAll(list);
        return map;
    }

    public Set<CollectionItem> getCollection() {
        if (this.itemSet == null) {
            if (this.itemMap != null && !this.itemMap.isEmpty()) {
                this.itemSet = SetUtil.createHashSet(this.itemMap.size());
                long maxItemId = Long.MIN_VALUE;
                for (CollectionItem collectionItem : this.itemMap.values()) {
                    if (collectionItem.getItemId() > maxItemId) {
                        maxItemId = collectionItem.getItemId();
                    }
                    this.itemSet.add(collectionItem);
                }
                this.setId(maxItemId + 100000L);
                this.itemMap.clear();
            } else {
                this.itemSet = new HashSet<CollectionItem>(1000);
            }
            this.itemMap = null;
        }
        return this.itemSet;
    }

    @Override
    public Map<Long, CollectionItem> getMap() {
        if (this.itemMap == null) {
            if (this.itemSet != null && !this.itemSet.isEmpty()) {
                this.itemMap = MapUtil.createHashMap(this.itemSet.size());
                for (CollectionItem item : this.itemSet) {
                    this.itemMap.put(item.getItemId(), item);
                }
                this.itemSet.clear();
            } else {
                this.itemMap = new HashMap(1000);
            }
            this.itemSet = null;
        }
        return this.itemMap;
    }

    @Override
    protected void onDestroy() {
        if (this.itemSet != null) {
            this.itemSet.clear();
        }
    }

    @Override
    public int getId() {
        return 41;
    }
}

