/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue;

import com.hazelcast.collection.impl.queue.QueueDataSerializerHook;
import com.hazelcast.collection.impl.queue.QueueItem;
import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.collection.impl.queue.QueueStoreWrapper;
import com.hazelcast.collection.impl.queue.QueueWaitNotifyKey;
import com.hazelcast.collection.impl.txnqueue.TxQueueItem;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.monitor.impl.LocalQueueStatsImpl;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.util.Clock;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class QueueContainer
implements IdentifiedDataSerializable {
    private final Map<Long, TxQueueItem> txMap = new HashMap<Long, TxQueueItem>();
    private final Map<Long, Data> dataMap = new HashMap<Long, Data>();
    private QueueWaitNotifyKey pollWaitNotifyKey;
    private QueueWaitNotifyKey offerWaitNotifyKey;
    private LinkedList<QueueItem> itemQueue;
    private Map<Long, QueueItem> backupMap;
    private QueueConfig config;
    private QueueStoreWrapper store;
    private NodeEngine nodeEngine;
    private QueueService service;
    private ILogger logger;
    private long idGenerator;
    private String name;
    private long minAge = Long.MAX_VALUE;
    private long maxAge = Long.MIN_VALUE;
    private long totalAge;
    private long totalAgedCount;
    private boolean isEvictionScheduled;
    private long lastIdLoaded;

    public QueueContainer() {
    }

    public QueueContainer(String name) {
        this.name = name;
        this.pollWaitNotifyKey = new QueueWaitNotifyKey(name, "poll");
        this.offerWaitNotifyKey = new QueueWaitNotifyKey(name, "offer");
    }

    public QueueContainer(String name, QueueConfig config, NodeEngine nodeEngine, QueueService service) {
        this(name);
        this.setConfig(config, nodeEngine, service);
    }

    public void init(boolean fromBackup) {
        Set<Long> keys;
        if (!fromBackup && this.store.isEnabled() && (keys = this.store.loadAllKeys()) != null) {
            long maxId = -1L;
            for (Long key : keys) {
                QueueItem item = new QueueItem(this, key, null);
                this.getItemQueue().offer(item);
                maxId = Math.max(maxId, key);
            }
            this.idGenerator = maxId + 1L;
        }
    }

    public QueueStoreWrapper getStore() {
        return this.store;
    }

    public String getName() {
        return this.name;
    }

    public boolean txnCheckReserve(long itemId) {
        if (this.txMap.get(itemId) == null) {
            throw new TransactionException("No reserve for itemId: " + itemId);
        }
        return true;
    }

    public void txnEnsureBackupReserve(long itemId, String transactionId, boolean pollOperation) {
        if (this.txMap.get(itemId) == null) {
            if (pollOperation) {
                this.txnPollBackupReserve(itemId, transactionId);
            } else {
                this.txnOfferBackupReserve(itemId, transactionId);
            }
        }
    }

    public QueueItem txnPollReserve(long reservedOfferId, String transactionId) {
        QueueItem item = this.getItemQueue().peek();
        if (item == null) {
            TxQueueItem txItem = this.txMap.remove(reservedOfferId);
            if (txItem == null) {
                return null;
            }
            item = new QueueItem(this, txItem.getItemId(), txItem.getData());
            return item;
        }
        if (this.store.isEnabled() && item.getData() == null) {
            try {
                this.load(item);
            }
            catch (Exception e) {
                throw new HazelcastException(e);
            }
        }
        this.getItemQueue().poll();
        this.txMap.put(item.getItemId(), new TxQueueItem(item).setPollOperation(true).setTransactionId(transactionId));
        return item;
    }

    public void txnPollBackupReserve(long itemId, String transactionId) {
        QueueItem item = this.getBackupMap().remove(itemId);
        if (item != null) {
            this.txMap.put(itemId, new TxQueueItem(item).setPollOperation(true).setTransactionId(transactionId));
            return;
        }
        if (this.txMap.remove(itemId) == null) {
            this.logger.warning("Poll backup reserve failed, itemId: " + itemId + " is not found");
        }
    }

    public Data txnCommitPoll(long itemId) {
        Data result = this.txnCommitPollBackup(itemId);
        this.scheduleEvictionIfEmpty();
        return result;
    }

    public Data txnCommitPollBackup(long itemId) {
        TxQueueItem item = this.txMap.remove(itemId);
        if (item == null) {
            this.logger.warning("txnCommitPoll operation-> No txn item for itemId: " + itemId);
            return null;
        }
        if (this.store.isEnabled()) {
            try {
                this.store.delete(item.getItemId());
            }
            catch (Exception e) {
                this.logger.severe("Error during store delete: " + item.getItemId(), e);
            }
        }
        return item.getData();
    }

    public boolean txnRollbackPoll(long itemId, boolean backup) {
        TxQueueItem item = this.txMap.remove(itemId);
        if (item == null) {
            return false;
        }
        if (backup) {
            this.getBackupMap().put(itemId, item);
        } else {
            this.addTxItemOrdered(item);
        }
        this.cancelEvictionIfExists();
        return true;
    }

    private void addTxItemOrdered(TxQueueItem txQueueItem) {
        ListIterator<TxQueueItem> iterator = ((List)((Object)this.getItemQueue())).listIterator();
        while (iterator.hasNext()) {
            QueueItem queueItem = (QueueItem)iterator.next();
            if (txQueueItem.itemId >= queueItem.itemId) continue;
            iterator.previous();
            break;
        }
        iterator.add(txQueueItem);
    }

    public long txnOfferReserve(String transactionId) {
        long itemId = this.nextId();
        this.txnOfferReserveInternal(itemId, transactionId);
        return itemId;
    }

    public void txnOfferBackupReserve(long itemId, String transactionId) {
        TxQueueItem o = this.txnOfferReserveInternal(itemId, transactionId);
        if (o != null) {
            this.logger.severe("txnOfferBackupReserve operation-> Item exists already at txMap for itemId: " + itemId);
        }
    }

    private TxQueueItem txnOfferReserveInternal(long itemId, String transactionId) {
        TxQueueItem item = new TxQueueItem(this, itemId, null).setTransactionId(transactionId).setPollOperation(false);
        return this.txMap.put(itemId, item);
    }

    public boolean txnCommitOffer(long itemId, Data data, boolean backup) {
        QueueItem item = this.txMap.remove(itemId);
        if (item == null && !backup) {
            throw new TransactionException("No reserve: " + itemId);
        }
        if (item == null) {
            item = new QueueItem(this, itemId, data);
        }
        item.setData(data);
        if (!backup) {
            this.getItemQueue().offer(item);
            this.cancelEvictionIfExists();
        } else {
            this.getBackupMap().put(itemId, item);
        }
        if (this.store.isEnabled() && !backup) {
            try {
                this.store.store((Long)item.getItemId(), data);
            }
            catch (Exception e) {
                this.logger.warning("Exception during store", e);
            }
        }
        return true;
    }

    public boolean txnRollbackOffer(long itemId) {
        boolean result = this.txnRollbackOfferBackup(itemId);
        this.scheduleEvictionIfEmpty();
        return result;
    }

    public boolean txnRollbackOfferBackup(long itemId) {
        QueueItem item = this.txMap.remove(itemId);
        if (item == null) {
            this.logger.warning("txnRollbackOffer operation-> No txn item for itemId: " + itemId);
            return false;
        }
        return true;
    }

    public QueueItem txnPeek(long offerId, String transactionId) {
        QueueItem item = this.getItemQueue().peek();
        if (item == null) {
            if (offerId == -1L) {
                return null;
            }
            TxQueueItem txItem = this.txMap.get(offerId);
            if (txItem == null) {
                return null;
            }
            item = new QueueItem(this, txItem.getItemId(), txItem.getData());
            return item;
        }
        if (this.store.isEnabled() && item.getData() == null) {
            try {
                this.load(item);
            }
            catch (Exception e) {
                throw new HazelcastException(e);
            }
        }
        return item;
    }

    public long offer(Data data) {
        QueueItem item = new QueueItem(this, this.nextId(), null);
        if (this.store.isEnabled()) {
            try {
                this.store.store((Long)item.getItemId(), data);
            }
            catch (Exception e) {
                throw new HazelcastException(e);
            }
        }
        if (!this.store.isEnabled() || this.store.getMemoryLimit() > this.getItemQueue().size()) {
            item.setData(data);
        }
        this.getItemQueue().offer(item);
        this.cancelEvictionIfExists();
        return item.getItemId();
    }

    public void offerBackup(Data data, long itemId) {
        QueueItem item = new QueueItem(this, itemId, null);
        if (!this.store.isEnabled() || this.store.getMemoryLimit() > this.getItemQueue().size()) {
            item.setData(data);
        }
        this.getBackupMap().put(itemId, item);
    }

    public Map<Long, Data> addAll(Collection<Data> dataList) {
        Map<Long, Data> map = MapUtil.createHashMap(dataList.size());
        ArrayList<QueueItem> list = new ArrayList<QueueItem>(dataList.size());
        for (Data data : dataList) {
            QueueItem item = new QueueItem(this, this.nextId(), null);
            if (!this.store.isEnabled() || this.store.getMemoryLimit() > this.getItemQueue().size()) {
                item.setData(data);
            }
            map.put(item.getItemId(), data);
            list.add(item);
        }
        if (this.store.isEnabled() && !map.isEmpty()) {
            try {
                this.store.storeAll(map);
            }
            catch (Exception e) {
                throw new HazelcastException(e);
            }
        }
        if (!list.isEmpty()) {
            this.getItemQueue().addAll(list);
            this.cancelEvictionIfExists();
        }
        return map;
    }

    public void addAllBackup(Map<Long, Data> dataMap) {
        for (Map.Entry<Long, Data> entry : dataMap.entrySet()) {
            QueueItem item = new QueueItem(this, entry.getKey(), null);
            if (!this.store.isEnabled() || this.store.getMemoryLimit() > this.getItemQueue().size()) {
                item.setData(entry.getValue());
            }
            this.getBackupMap().put(item.getItemId(), item);
        }
    }

    public QueueItem peek() {
        QueueItem item = this.getItemQueue().peek();
        if (item == null) {
            return null;
        }
        if (this.store.isEnabled() && item.getData() == null) {
            try {
                this.load(item);
            }
            catch (Exception e) {
                throw new HazelcastException(e);
            }
        }
        return item;
    }

    public QueueItem poll() {
        QueueItem item = this.peek();
        if (item == null) {
            return null;
        }
        if (this.store.isEnabled()) {
            try {
                this.store.delete(item.getItemId());
            }
            catch (Exception e) {
                throw new HazelcastException(e);
            }
        }
        this.getItemQueue().poll();
        this.age(item, Clock.currentTimeMillis());
        this.scheduleEvictionIfEmpty();
        return item;
    }

    public void pollBackup(long itemId) {
        QueueItem item = this.getBackupMap().remove(itemId);
        if (item != null) {
            this.age(item, Clock.currentTimeMillis());
        }
    }

    public Map<Long, Data> drain(int maxSize) {
        int maxSizeParam = maxSize;
        if (maxSizeParam < 0 || maxSizeParam > this.getItemQueue().size()) {
            maxSizeParam = this.getItemQueue().size();
        }
        Map<Long, Data> map = MapUtil.createLinkedHashMap(maxSizeParam);
        this.mapDrainIterator(maxSizeParam, map);
        if (this.store.isEnabled() && maxSizeParam != 0) {
            try {
                this.store.deleteAll(map.keySet());
            }
            catch (Exception e) {
                throw new HazelcastException(e);
            }
        }
        long current = Clock.currentTimeMillis();
        for (int i = 0; i < maxSizeParam; ++i) {
            QueueItem item = this.getItemQueue().poll();
            this.age(item, current);
        }
        if (maxSizeParam != 0) {
            this.scheduleEvictionIfEmpty();
        }
        return map;
    }

    public void mapDrainIterator(int maxSize, Map<Long, Data> map) {
        Iterator<QueueItem> iterator = this.getItemQueue().iterator();
        for (int i = 0; i < maxSize; ++i) {
            QueueItem item = iterator.next();
            if (this.store.isEnabled() && item.getData() == null) {
                try {
                    this.load(item);
                }
                catch (Exception e) {
                    throw new HazelcastException(e);
                }
            }
            map.put(item.getItemId(), item.getData());
        }
    }

    public void drainFromBackup(Set<Long> itemIdSet) {
        for (Long itemId : itemIdSet) {
            this.pollBackup(itemId);
        }
        this.dataMap.clear();
    }

    public int size() {
        return Math.min(this.config.getMaxSize(), this.getItemQueue().size());
    }

    public int txMapSize() {
        return this.txMap.size();
    }

    public int backupSize() {
        return this.getBackupMap().size();
    }

    public Map<Long, Data> clear() {
        long current = Clock.currentTimeMillis();
        Map<Long, Data> map = MapUtil.createLinkedHashMap(this.getItemQueue().size());
        for (QueueItem item : this.getItemQueue()) {
            map.put(item.getItemId(), item.getData());
            this.age(item, current);
        }
        if (this.store.isEnabled() && !map.isEmpty()) {
            try {
                this.store.deleteAll(map.keySet());
            }
            catch (Exception e) {
                throw new HazelcastException(e);
            }
        }
        this.getItemQueue().clear();
        this.dataMap.clear();
        this.scheduleEvictionIfEmpty();
        return map;
    }

    public void clearBackup(Set<Long> itemIdSet) {
        this.drainFromBackup(itemIdSet);
    }

    public long remove(Data data) {
        Iterator<QueueItem> iterator = this.getItemQueue().iterator();
        while (iterator.hasNext()) {
            QueueItem item = iterator.next();
            if (!data.equals(item.getData())) continue;
            if (this.store.isEnabled()) {
                try {
                    this.store.delete(item.getItemId());
                }
                catch (Exception e) {
                    throw new HazelcastException(e);
                }
            }
            iterator.remove();
            this.age(item, Clock.currentTimeMillis());
            this.scheduleEvictionIfEmpty();
            return item.getItemId();
        }
        return -1L;
    }

    public void removeBackup(long itemId) {
        this.getBackupMap().remove(itemId);
    }

    public boolean contains(Collection<Data> dataSet) {
        for (Data data : dataSet) {
            boolean contains = false;
            for (QueueItem item : this.getItemQueue()) {
                if (item.getData() == null || !item.getData().equals(data)) continue;
                contains = true;
                break;
            }
            if (contains) continue;
            return false;
        }
        return true;
    }

    public List<Data> getAsDataList() {
        ArrayList<Data> dataList = new ArrayList<Data>(this.getItemQueue().size());
        for (QueueItem item : this.getItemQueue()) {
            if (this.store.isEnabled() && item.getData() == null) {
                try {
                    this.load(item);
                }
                catch (Exception e) {
                    throw new HazelcastException(e);
                }
            }
            dataList.add(item.getData());
        }
        return dataList;
    }

    public Map<Long, Data> compareAndRemove(Collection<Data> dataList, boolean retain) {
        LinkedHashMap<Long, Data> map = new LinkedHashMap<Long, Data>();
        for (QueueItem item : this.getItemQueue()) {
            if (item.getData() == null && this.store.isEnabled()) {
                try {
                    this.load(item);
                }
                catch (Exception e) {
                    throw new HazelcastException(e);
                }
            }
            boolean contains = dataList.contains(item.getData());
            if ((!retain || contains) && (retain || !contains)) continue;
            map.put(item.getItemId(), item.getData());
        }
        this.mapIterateAndRemove(map);
        return map;
    }

    public void mapIterateAndRemove(Map<Long, Data> map) {
        if (map.size() <= 0) {
            return;
        }
        if (this.store.isEnabled()) {
            try {
                this.store.deleteAll(map.keySet());
            }
            catch (Exception e) {
                throw new HazelcastException(e);
            }
        }
        Iterator<QueueItem> iterator = this.getItemQueue().iterator();
        while (iterator.hasNext()) {
            QueueItem item = iterator.next();
            if (!map.containsKey(item.getItemId())) continue;
            iterator.remove();
            this.age(item, Clock.currentTimeMillis());
        }
        this.scheduleEvictionIfEmpty();
    }

    public void compareAndRemoveBackup(Set<Long> itemIdSet) {
        this.drainFromBackup(itemIdSet);
    }

    private void load(QueueItem item) throws Exception {
        int bulkLoad = this.store.getBulkLoad();
        bulkLoad = Math.min(this.getItemQueue().size(), bulkLoad);
        if (bulkLoad == 1) {
            item.setData(this.store.load(item.getItemId()));
        } else if (bulkLoad > 1) {
            long maxIdToLoad = -1L;
            Iterator<QueueItem> iterator = this.getItemQueue().iterator();
            Set<Long> keySet = SetUtil.createHashSet(bulkLoad);
            keySet.add(item.getItemId());
            while (keySet.size() < bulkLoad && iterator.hasNext()) {
                long itemId = iterator.next().getItemId();
                if (itemId <= this.lastIdLoaded) continue;
                keySet.add(itemId);
                maxIdToLoad = Math.max(itemId, maxIdToLoad);
            }
            Map<Long, Data> values = this.store.loadAll(keySet);
            this.lastIdLoaded = maxIdToLoad;
            this.dataMap.putAll(values);
            item.setData(this.getDataFromMap(item.getItemId()));
        }
    }

    public boolean hasEnoughCapacity() {
        return this.hasEnoughCapacity(1);
    }

    public boolean hasEnoughCapacity(int delta) {
        return this.getItemQueue().size() + delta <= this.config.getMaxSize();
    }

    public Deque<QueueItem> getItemQueue() {
        if (this.itemQueue == null) {
            this.itemQueue = new LinkedList();
            if (this.backupMap != null && !this.backupMap.isEmpty()) {
                ArrayList<QueueItem> values = new ArrayList<QueueItem>(this.backupMap.values());
                Collections.sort(values);
                this.itemQueue.addAll(values);
                QueueItem lastItem = this.itemQueue.peekLast();
                if (lastItem != null) {
                    this.setId(lastItem.itemId + 100000L);
                }
                this.backupMap.clear();
                this.backupMap = null;
            }
            if (!this.txMap.isEmpty()) {
                long maxItemId = Long.MIN_VALUE;
                for (TxQueueItem item : this.txMap.values()) {
                    maxItemId = Math.max(maxItemId, item.itemId);
                }
                this.setId(maxItemId + 100000L);
            }
        }
        return this.itemQueue;
    }

    public Map<Long, QueueItem> getBackupMap() {
        if (this.backupMap == null) {
            if (this.itemQueue != null) {
                this.backupMap = MapUtil.createHashMap(this.itemQueue.size());
                for (QueueItem item : this.itemQueue) {
                    this.backupMap.put(item.getItemId(), item);
                }
                this.itemQueue.clear();
                this.itemQueue = null;
            } else {
                this.backupMap = new HashMap<Long, QueueItem>();
            }
        }
        return this.backupMap;
    }

    public Data getDataFromMap(long itemId) {
        return this.dataMap.remove(itemId);
    }

    public void setConfig(QueueConfig config, NodeEngine nodeEngine, QueueService service) {
        this.nodeEngine = nodeEngine;
        this.service = service;
        this.logger = nodeEngine.getLogger(QueueContainer.class);
        this.config = new QueueConfig(config);
        QueueStoreConfig storeConfig = config.getQueueStoreConfig();
        SerializationService serializationService = nodeEngine.getSerializationService();
        ClassLoader classLoader = nodeEngine.getConfigClassLoader();
        this.store = QueueStoreWrapper.create(this.name, storeConfig, serializationService, classLoader);
    }

    private long nextId() {
        return ++this.idGenerator;
    }

    public long getCurrentId() {
        return this.idGenerator;
    }

    public QueueWaitNotifyKey getPollWaitNotifyKey() {
        return this.pollWaitNotifyKey;
    }

    public QueueWaitNotifyKey getOfferWaitNotifyKey() {
        return this.offerWaitNotifyKey;
    }

    public QueueConfig getConfig() {
        return this.config;
    }

    private void age(QueueItem item, long currentTime) {
        long elapsed = currentTime - item.getCreationTime();
        if (elapsed <= 0L) {
            return;
        }
        ++this.totalAgedCount;
        this.totalAge += elapsed;
        this.minAge = Math.min(this.minAge, elapsed);
        this.maxAge = Math.max(this.maxAge, elapsed);
    }

    public void setStats(LocalQueueStatsImpl stats) {
        stats.setMinAge(this.minAge);
        stats.setMaxAge(this.maxAge);
        long totalAgedCountVal = Math.max(this.totalAgedCount, 1L);
        stats.setAveAge(this.totalAge / totalAgedCountVal);
    }

    private void scheduleEvictionIfEmpty() {
        int emptyQueueTtl = this.config.getEmptyQueueTtl();
        if (emptyQueueTtl < 0) {
            return;
        }
        if (this.getItemQueue().isEmpty() && this.txMap.isEmpty() && !this.isEvictionScheduled) {
            if (emptyQueueTtl == 0) {
                this.nodeEngine.getProxyService().destroyDistributedObject("hz:impl:queueService", this.name);
            } else {
                this.service.scheduleEviction(this.name, TimeUnit.SECONDS.toMillis(emptyQueueTtl));
                this.isEvictionScheduled = true;
            }
        }
    }

    public void cancelEvictionIfExists() {
        if (this.isEvictionScheduled) {
            this.service.cancelEviction(this.name);
            this.isEvictionScheduled = false;
        }
    }

    public boolean isEvictable() {
        return this.getItemQueue().isEmpty() && this.txMap.isEmpty();
    }

    public void rollbackTransaction(String transactionId) {
        Iterator<TxQueueItem> iterator = this.txMap.values().iterator();
        while (iterator.hasNext()) {
            TxQueueItem item = iterator.next();
            if (!transactionId.equals(item.getTransactionId())) continue;
            iterator.remove();
            if (!item.isPollOperation()) continue;
            this.getItemQueue().offerFirst(item);
            this.cancelEvictionIfExists();
        }
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.getItemQueue().size());
        for (QueueItem queueItem : this.getItemQueue()) {
            out.writeObject(queueItem);
        }
        out.writeInt(this.txMap.size());
        for (TxQueueItem txQueueItem : this.txMap.values()) {
            txQueueItem.writeData(out);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.pollWaitNotifyKey = new QueueWaitNotifyKey(this.name, "poll");
        this.offerWaitNotifyKey = new QueueWaitNotifyKey(this.name, "offer");
        int size = in.readInt();
        for (int j = 0; j < size; ++j) {
            QueueItem item = (QueueItem)in.readObject();
            this.getItemQueue().offer(item);
            this.setId(item.getItemId());
        }
        int txSize = in.readInt();
        for (int j = 0; j < txSize; ++j) {
            TxQueueItem item = new TxQueueItem(this, -1L, null);
            item.readData(in);
            this.txMap.put(item.getItemId(), item);
            this.setId(item.getItemId());
        }
    }

    public void destroy() {
        if (this.itemQueue != null) {
            this.itemQueue.clear();
        }
        if (this.backupMap != null) {
            this.backupMap.clear();
        }
        this.txMap.clear();
        this.dataMap.clear();
    }

    @Override
    public int getFactoryId() {
        return QueueDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 38;
    }

    void setId(long itemId) {
        this.idGenerator = Math.max(itemId + 1L, this.idGenerator);
    }
}

