/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.collection;

import com.hazelcast.collection.impl.collection.CollectionDataSerializerHook;
import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.TxCollectionItem;
import com.hazelcast.config.CollectionConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CollectionContainer
implements IdentifiedDataSerializable {
    public static final int INVALID_ITEM_ID = -1;
    public static final int ID_PROMOTION_OFFSET = 100000;
    protected final Map<Long, TxCollectionItem> txMap = new HashMap<Long, TxCollectionItem>();
    protected String name;
    protected NodeEngine nodeEngine;
    protected ILogger logger;
    protected Map<Long, CollectionItem> itemMap;
    private long idGenerator;

    protected CollectionContainer() {
    }

    protected CollectionContainer(String name, NodeEngine nodeEngine) {
        this.name = name;
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(this.getClass());
    }

    public void init(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(this.getClass());
    }

    public String getName() {
        return this.name;
    }

    public abstract CollectionConfig getConfig();

    public abstract Collection<CollectionItem> getCollection();

    public abstract Map<Long, CollectionItem> getMap();

    public long add(Data value) {
        CollectionItem item = new CollectionItem(this.nextId(), value);
        if (this.getCollection().add(item)) {
            return item.getItemId();
        }
        return -1L;
    }

    public void addBackup(long itemId, Data value) {
        CollectionItem item = new CollectionItem(itemId, value);
        this.getMap().put(itemId, item);
    }

    public CollectionItem remove(Data value) {
        Iterator<CollectionItem> iterator = this.getCollection().iterator();
        while (iterator.hasNext()) {
            CollectionItem item = iterator.next();
            if (!value.equals(item.getValue())) continue;
            iterator.remove();
            return item;
        }
        return null;
    }

    public void removeBackup(long itemId) {
        this.getMap().remove(itemId);
    }

    public int size() {
        return this.getCollection().size();
    }

    public Map<Long, Data> clear(boolean returnValues) {
        Map<Long, Data> itemIdMap = null;
        Collection<CollectionItem> collection = this.getCollection();
        if (returnValues) {
            itemIdMap = MapUtil.createHashMap(collection.size());
            for (CollectionItem item : collection) {
                itemIdMap.put(item.getItemId(), item.getValue());
            }
        }
        collection.clear();
        this.txMap.clear();
        return itemIdMap;
    }

    public void clearBackup(Set<Long> itemIdSet) {
        for (Long itemId : itemIdSet) {
            this.removeBackup(itemId);
        }
    }

    public boolean contains(Set<Data> valueSet) {
        Collection<CollectionItem> collection = this.getCollection();
        CollectionItem collectionItem = new CollectionItem(-1L, null);
        for (Data value : valueSet) {
            collectionItem.setValue(value);
            if (collection.contains(collectionItem)) continue;
            return false;
        }
        return true;
    }

    public Map<Long, Data> addAll(List<Data> valueList) {
        int size = valueList.size();
        Map<Long, Data> map = MapUtil.createHashMap(size);
        ArrayList<CollectionItem> list = new ArrayList<CollectionItem>(size);
        for (Data value : valueList) {
            long itemId = this.nextId();
            list.add(new CollectionItem(itemId, value));
            map.put(itemId, value);
        }
        this.getCollection().addAll(list);
        return map;
    }

    public void addAllBackup(Map<Long, Data> valueMap) {
        Map<Long, CollectionItem> map = MapUtil.createHashMap(valueMap.size());
        for (Map.Entry<Long, Data> entry : valueMap.entrySet()) {
            long itemId = entry.getKey();
            map.put(itemId, new CollectionItem(itemId, entry.getValue()));
        }
        this.getMap().putAll(map);
    }

    public Map<Long, Data> compareAndRemove(boolean retain, Set<Data> valueSet) {
        HashMap<Long, Data> itemIdMap = new HashMap<Long, Data>();
        Iterator<CollectionItem> iterator = this.getCollection().iterator();
        while (iterator.hasNext()) {
            CollectionItem item = iterator.next();
            boolean contains = valueSet.contains(item.getValue());
            if ((!contains || retain) && (contains || !retain)) continue;
            itemIdMap.put(item.getItemId(), item.getValue());
            iterator.remove();
        }
        return itemIdMap;
    }

    public List<Data> getAll() {
        ArrayList<Data> sub = new ArrayList<Data>(this.size());
        for (CollectionItem item : this.getCollection()) {
            sub.add(item.getValue());
        }
        return sub;
    }

    public boolean hasEnoughCapacity(int delta) {
        return this.size() + delta <= this.getConfig().getMaxSize();
    }

    public Long reserveAdd(String transactionId, Data value) {
        if (value != null && this.getCollection().contains(new CollectionItem(-1L, value))) {
            return null;
        }
        long itemId = this.nextId();
        this.txMap.put(itemId, new TxCollectionItem(itemId, null, transactionId, false));
        return itemId;
    }

    public void reserveAddBackup(long itemId, String transactionId) {
        TxCollectionItem item = new TxCollectionItem(itemId, null, transactionId, false);
        TxCollectionItem o = this.txMap.put(itemId, item);
        if (o != null) {
            this.logger.severe("Transaction reservation item already exists on the backup member. Reservation item ID: " + itemId);
        }
    }

    public CollectionItem reserveRemove(long reservedItemId, Data value, String transactionId) {
        Iterator<CollectionItem> iterator = this.getCollection().iterator();
        while (iterator.hasNext()) {
            CollectionItem item = iterator.next();
            if (!value.equals(item.getValue())) continue;
            iterator.remove();
            this.txMap.put(item.getItemId(), new TxCollectionItem(item).setTransactionId(transactionId).setRemoveOperation(true));
            return item;
        }
        if (reservedItemId != -1L) {
            return this.txMap.remove(reservedItemId);
        }
        return null;
    }

    public void reserveRemoveBackup(long itemId, String transactionId) {
        CollectionItem item = this.getMap().remove(itemId);
        if (item == null) {
            throw new TransactionException("Transaction reservation failed on backup member. Reservation item ID: " + itemId);
        }
        this.txMap.put(itemId, new TxCollectionItem(item).setTransactionId(transactionId).setRemoveOperation(true));
    }

    public void ensureReserve(long itemId) {
        if (this.txMap.get(itemId) == null) {
            throw new TransactionException("Transaction reservation cannot be found for reservation item ID: " + itemId);
        }
    }

    public void rollbackAdd(long itemId) {
        if (this.txMap.remove(itemId) == null) {
            this.logger.warning("Transaction log cannot be found for rolling back 'add()' operation. Missing log item ID: " + itemId);
        }
    }

    public void rollbackAddBackup(long itemId) {
        if (this.txMap.remove(itemId) == null) {
            this.logger.warning("Transaction log cannot be found for rolling back 'add()' operation on backup member. Missing log item ID: " + itemId);
        }
    }

    public void rollbackRemove(long itemId) {
        TxCollectionItem txItem = this.txMap.remove(itemId);
        if (txItem == null) {
            this.logger.warning("Transaction log cannot be found for rolling back 'remove()' operation. Missing log item ID: " + itemId);
        } else {
            CollectionItem item = new CollectionItem(itemId, txItem.value);
            this.getCollection().add(item);
        }
    }

    public void rollbackRemoveBackup(long itemId) {
        TxCollectionItem item = this.txMap.remove(itemId);
        if (item == null) {
            this.logger.warning("Transaction log cannot be found for rolling back 'remove()' operation on backup member. Missing log item ID: " + itemId);
        }
    }

    public void commitAdd(long itemId, Data value) {
        TxCollectionItem txItem = this.txMap.remove(itemId);
        if (txItem == null) {
            throw new TransactionException("Transaction log cannot be found for committing 'add()' operation. Missing log item ID: " + itemId);
        }
        CollectionItem item = new CollectionItem(itemId, value);
        this.getCollection().add(item);
    }

    public void commitAddBackup(long itemId, Data value) {
        this.txMap.remove(itemId);
        CollectionItem item = new CollectionItem(itemId, value);
        this.getMap().put(itemId, item);
    }

    public CollectionItem commitRemove(long itemId) {
        CollectionItem item = this.txMap.remove(itemId);
        if (item == null) {
            this.logger.warning("Transaction log cannot be found for committing 'remove()' operation. Missing log item ID: " + itemId);
        }
        return item;
    }

    public void commitRemoveBackup(long itemId) {
        if (this.txMap.remove(itemId) == null) {
            this.logger.warning("Transaction log cannot be found for committing 'remove()' operation on backup member. Missing log item ID:" + itemId);
        }
    }

    public void rollbackTransaction(String transactionId) {
        Iterator<TxCollectionItem> iterator = this.txMap.values().iterator();
        while (iterator.hasNext()) {
            TxCollectionItem txItem = iterator.next();
            if (!transactionId.equals(txItem.getTransactionId())) continue;
            iterator.remove();
            if (!txItem.isRemoveOperation()) continue;
            CollectionItem item = new CollectionItem(txItem.itemId, txItem.value);
            this.getCollection().add(item);
        }
    }

    public long nextId() {
        return ++this.idGenerator;
    }

    public long getCurrentId() {
        return this.idGenerator;
    }

    protected void setId(long itemId) {
        this.idGenerator = Math.max(itemId + 1L, this.idGenerator);
    }

    public void destroy() {
        this.onDestroy();
        if (this.itemMap != null) {
            this.itemMap.clear();
        }
        this.txMap.clear();
    }

    protected abstract void onDestroy();

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        Collection<CollectionItem> collection = this.getCollection();
        out.writeInt(collection.size());
        for (CollectionItem item : collection) {
            item.writeData(out);
        }
        out.writeInt(this.txMap.size());
        for (TxCollectionItem txCollectionItem : this.txMap.values()) {
            txCollectionItem.writeData(out);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        int collectionSize = in.readInt();
        Collection<CollectionItem> collection = this.getCollection();
        for (int i = 0; i < collectionSize; ++i) {
            CollectionItem item = new CollectionItem();
            item.readData(in);
            collection.add(item);
            this.setId(item.getItemId());
        }
        int txMapSize = in.readInt();
        for (int i = 0; i < txMapSize; ++i) {
            TxCollectionItem txCollectionItem = new TxCollectionItem();
            txCollectionItem.readData(in);
            this.txMap.put(txCollectionItem.getItemId(), txCollectionItem);
            this.setId(txCollectionItem.itemId);
        }
    }

    @Override
    public int getFactoryId() {
        return CollectionDataSerializerHook.F_ID;
    }
}

