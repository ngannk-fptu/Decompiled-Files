/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.list;

import com.hazelcast.collection.impl.collection.CollectionContainer;
import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.TxCollectionItem;
import com.hazelcast.config.ListConfig;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.MapUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ListContainer
extends CollectionContainer {
    private static final int INITIAL_CAPACITY = 1000;
    private List<CollectionItem> itemList;
    private ListConfig config;

    public ListContainer() {
    }

    public ListContainer(String name, NodeEngine nodeEngine) {
        super(name, nodeEngine);
    }

    @Override
    public ListConfig getConfig() {
        if (this.config == null) {
            this.config = this.nodeEngine.getConfig().findListConfig(this.name);
        }
        return this.config;
    }

    @Override
    public void rollbackRemove(long itemId) {
        TxCollectionItem txItem = (TxCollectionItem)this.txMap.remove(itemId);
        if (txItem == null) {
            this.logger.warning("Transaction log cannot be found for rolling back 'remove()' operation. Missing log item ID: " + itemId);
            return;
        }
        CollectionItem item = new CollectionItem(itemId, txItem.getValue());
        this.addTxItemOrdered(item);
    }

    private void addTxItemOrdered(CollectionItem item) {
        ListIterator<CollectionItem> iterator = this.getCollection().listIterator();
        while (iterator.hasNext()) {
            CollectionItem collectionItem = (CollectionItem)iterator.next();
            if (item.getItemId() >= collectionItem.getItemId()) continue;
            iterator.previous();
            break;
        }
        iterator.add(item);
    }

    public CollectionItem add(int index, Data value) {
        CollectionItem item = new CollectionItem(this.nextId(), value);
        if (index < 0) {
            return this.getCollection().add(item) ? item : null;
        }
        this.getCollection().add(index, item);
        return item;
    }

    public CollectionItem get(int index) {
        return (CollectionItem)this.getCollection().get(index);
    }

    public CollectionItem set(int index, long itemId, Data value) {
        return this.getCollection().set(index, new CollectionItem(itemId, value));
    }

    public void setBackup(long oldItemId, long itemId, Data value) {
        this.getMap().remove(oldItemId);
        this.getMap().put(itemId, new CollectionItem(itemId, value));
    }

    public CollectionItem remove(int index) {
        return (CollectionItem)this.getCollection().remove(index);
    }

    public int indexOf(boolean last, Data value) {
        Collection list = this.getCollection();
        if (last) {
            int index = list.size();
            ListIterator iterator = list.listIterator(index);
            while (iterator.hasPrevious()) {
                CollectionItem item = (CollectionItem)iterator.previous();
                --index;
                if (!value.equals(item.getValue())) continue;
                return index;
            }
        } else {
            int index = -1;
            for (CollectionItem item : list) {
                ++index;
                if (!value.equals(item.getValue())) continue;
                return index;
            }
        }
        return -1;
    }

    public Map<Long, Data> addAll(int index, List<Data> valueList) {
        int size = valueList.size();
        Map<Long, Data> map = MapUtil.createHashMap(size);
        ArrayList<CollectionItem> list = new ArrayList<CollectionItem>(size);
        for (Data value : valueList) {
            long itemId = this.nextId();
            list.add(new CollectionItem(itemId, value));
            map.put(itemId, value);
        }
        this.getCollection().addAll(index, list);
        return map;
    }

    public List<Data> sub(int from, int to) {
        List list;
        if (from == -1 && to == -1) {
            list = this.getCollection();
        } else if (to == -1) {
            Collection collection = this.getCollection();
            list = collection.subList(from, collection.size());
        } else {
            list = this.getCollection().subList(from, to);
        }
        ArrayList<Data> sub = new ArrayList<Data>(list.size());
        for (CollectionItem item : list) {
            sub.add(item.getValue());
        }
        return sub;
    }

    public List<CollectionItem> getCollection() {
        if (this.itemList == null) {
            if (this.itemMap != null && !this.itemMap.isEmpty()) {
                this.itemList = new ArrayList(this.itemMap.values());
                Collections.sort(this.itemList);
                CollectionItem lastItem = this.itemList.get(this.itemList.size() - 1);
                this.setId(lastItem.getItemId() + 100000L);
                this.itemMap.clear();
            } else {
                this.itemList = new ArrayList<CollectionItem>(1000);
            }
            this.itemMap = null;
        }
        return this.itemList;
    }

    @Override
    public Map<Long, CollectionItem> getMap() {
        if (this.itemMap == null) {
            if (this.itemList != null && !this.itemList.isEmpty()) {
                this.itemMap = MapUtil.createHashMap(this.itemList.size());
                for (CollectionItem item : this.itemList) {
                    this.itemMap.put(item.getItemId(), item);
                }
                this.itemList.clear();
            } else {
                this.itemMap = new HashMap(1000);
            }
            this.itemList = null;
        }
        return this.itemMap;
    }

    @Override
    protected void onDestroy() {
        if (this.itemList != null) {
            this.itemList.clear();
        }
    }

    @Override
    public int getId() {
        return 42;
    }
}

