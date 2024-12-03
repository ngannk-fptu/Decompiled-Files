/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.map.impl.mapstore.MapStoreContext;
import com.hazelcast.map.impl.mapstore.writebehind.AbstractWriteBehindProcessor;
import com.hazelcast.map.impl.mapstore.writebehind.StoreEvent;
import com.hazelcast.map.impl.mapstore.writebehind.StoreListener;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.MapUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

class DefaultWriteBehindProcessor
extends AbstractWriteBehindProcessor<DelayedEntry> {
    private static final Comparator<DelayedEntry> DELAYED_ENTRY_COMPARATOR = new Comparator<DelayedEntry>(){

        @Override
        public int compare(DelayedEntry o1, DelayedEntry o2) {
            long s2;
            long s1 = o1.getStoreTime();
            return s1 < (s2 = o2.getStoreTime()) ? -1 : (s1 == s2 ? 0 : 1);
        }
    };
    private static final int RETRY_TIMES_OF_A_FAILED_STORE_OPERATION = 3;
    private static final int RETRY_STORE_AFTER_WAIT_SECONDS = 1;
    private final List<StoreListener> storeListeners = new ArrayList<StoreListener>(2);

    DefaultWriteBehindProcessor(MapStoreContext mapStoreContext) {
        super(mapStoreContext);
    }

    @Override
    public Map<Integer, List<DelayedEntry>> process(List<DelayedEntry> delayedEntries) {
        this.sort(delayedEntries);
        Map<Integer, List<DelayedEntry>> failMap = this.writeBatchSize > 1 ? this.doStoreUsingBatchSize(delayedEntries) : this.processInternal(delayedEntries);
        return failMap;
    }

    private Map<Integer, List<DelayedEntry>> processInternal(List<DelayedEntry> delayedEntries) {
        if (delayedEntries == null || delayedEntries.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<Integer, List<DelayedEntry>> failuresByPartition = new HashMap<Integer, List<DelayedEntry>>();
        ArrayList<DelayedEntry> entriesToProcess = new ArrayList<DelayedEntry>();
        AbstractWriteBehindProcessor.StoreOperationType operationType = null;
        for (DelayedEntry entry : delayedEntries) {
            AbstractWriteBehindProcessor.StoreOperationType previousOperationType = operationType;
            operationType = entry.getValue() == null ? AbstractWriteBehindProcessor.StoreOperationType.DELETE : AbstractWriteBehindProcessor.StoreOperationType.WRITE;
            if (previousOperationType != null && !previousOperationType.equals((Object)operationType)) {
                List<DelayedEntry> failures = this.callHandler(entriesToProcess, previousOperationType);
                this.addFailsTo(failuresByPartition, failures);
                entriesToProcess.clear();
            }
            entriesToProcess.add(entry);
        }
        List<DelayedEntry> failures = this.callHandler(entriesToProcess, operationType);
        this.addFailsTo(failuresByPartition, failures);
        entriesToProcess.clear();
        return failuresByPartition;
    }

    private void addFailsTo(Map<Integer, List<DelayedEntry>> failsPerPartition, List<DelayedEntry> fails) {
        if (fails == null || fails.isEmpty()) {
            return;
        }
        for (DelayedEntry entry : fails) {
            int partitionId = entry.getPartitionId();
            List<DelayedEntry> delayedEntriesPerPartition = failsPerPartition.get(partitionId);
            if (delayedEntriesPerPartition == null) {
                delayedEntriesPerPartition = new ArrayList<DelayedEntry>();
                failsPerPartition.put(partitionId, delayedEntriesPerPartition);
            }
            delayedEntriesPerPartition.add(entry);
        }
    }

    private List<DelayedEntry> callHandler(Collection<DelayedEntry> delayedEntries, AbstractWriteBehindProcessor.StoreOperationType operationType) {
        int size = delayedEntries.size();
        if (size == 0) {
            return Collections.emptyList();
        }
        if (size == 1 || !this.writeCoalescing) {
            return this.processEntriesOneByOne(delayedEntries, operationType);
        }
        DelayedEntry[] delayedEntriesArray = delayedEntries.toArray(new DelayedEntry[0]);
        Map batchMap = this.prepareBatchMap(delayedEntriesArray);
        if (batchMap.size() == 1) {
            DelayedEntry delayedEntry = delayedEntriesArray[delayedEntriesArray.length - 1];
            return this.callSingleStoreWithListeners(delayedEntry, operationType);
        }
        List<DelayedEntry> failedEntryList = this.callBatchStoreWithListeners(batchMap, operationType);
        ArrayList<DelayedEntry> failedTries = new ArrayList<DelayedEntry>();
        for (DelayedEntry entry : failedEntryList) {
            List<DelayedEntry> tmpFails = this.callSingleStoreWithListeners(entry, operationType);
            failedTries.addAll(tmpFails);
        }
        return failedTries;
    }

    private List<DelayedEntry> processEntriesOneByOne(Collection<DelayedEntry> delayedEntries, AbstractWriteBehindProcessor.StoreOperationType operationType) {
        List<DelayedEntry> totalFailures = null;
        for (DelayedEntry delayedEntry : delayedEntries) {
            List<DelayedEntry> failures = this.callSingleStoreWithListeners(delayedEntry, operationType);
            if (!CollectionUtil.isNotEmpty(failures)) continue;
            if (totalFailures == null) {
                totalFailures = failures;
                continue;
            }
            totalFailures.addAll(failures);
        }
        return totalFailures == null ? Collections.EMPTY_LIST : totalFailures;
    }

    private Map prepareBatchMap(DelayedEntry[] delayedEntries) {
        int length = delayedEntries.length;
        Map batchMap = MapUtil.createHashMap(length);
        for (int i = length - 1; i >= 0; --i) {
            DelayedEntry delayedEntry = delayedEntries[i];
            Object key = delayedEntry.getKey();
            if (batchMap.containsKey(key)) continue;
            batchMap.put(key, delayedEntry);
        }
        return batchMap;
    }

    private List<DelayedEntry> callSingleStoreWithListeners(final DelayedEntry entry, final AbstractWriteBehindProcessor.StoreOperationType operationType) {
        return this.retryCall(new RetryTask<DelayedEntry>(){

            @Override
            public boolean run() throws Exception {
                DefaultWriteBehindProcessor.this.callBeforeStoreListeners(entry);
                Object key = DefaultWriteBehindProcessor.this.toObject(entry.getKey());
                Object value = DefaultWriteBehindProcessor.this.toObject(entry.getValue());
                boolean result = operationType.processSingle(key, value, DefaultWriteBehindProcessor.this.mapStore);
                DefaultWriteBehindProcessor.this.callAfterStoreListeners(entry);
                return result;
            }

            @Override
            public List<DelayedEntry> failureList() {
                ArrayList<DelayedEntry> failedDelayedEntries = new ArrayList<DelayedEntry>(1);
                failedDelayedEntries.add(entry);
                return failedDelayedEntries;
            }
        });
    }

    private Map convertToObject(Map<Object, DelayedEntry> batchMap) {
        Map<Object, Object> map = MapUtil.createHashMap(batchMap.size());
        for (DelayedEntry entry : batchMap.values()) {
            Object key = this.toObject(entry.getKey());
            Object value = this.toObject(entry.getValue());
            map.put(key, value);
        }
        return map;
    }

    private List<DelayedEntry> callBatchStoreWithListeners(final Map<Object, DelayedEntry> batchMap, final AbstractWriteBehindProcessor.StoreOperationType operationType) {
        return this.retryCall(new RetryTask<DelayedEntry>(){
            private List<DelayedEntry> failedDelayedEntries = Collections.emptyList();

            @Override
            public boolean run() throws Exception {
                boolean result;
                DefaultWriteBehindProcessor.this.callBeforeStoreListeners(batchMap.values());
                Map map = DefaultWriteBehindProcessor.this.convertToObject(batchMap);
                try {
                    result = operationType.processBatch(map, DefaultWriteBehindProcessor.this.mapStore);
                }
                catch (Exception ex) {
                    Iterator keys = batchMap.keySet().iterator();
                    while (keys.hasNext()) {
                        if (map.containsKey(DefaultWriteBehindProcessor.this.toObject(keys.next()))) continue;
                        keys.remove();
                    }
                    throw ex;
                }
                DefaultWriteBehindProcessor.this.callAfterStoreListeners(batchMap.values());
                return result;
            }

            @Override
            public List<DelayedEntry> failureList() {
                this.failedDelayedEntries = new ArrayList<DelayedEntry>(batchMap.values().size());
                this.failedDelayedEntries.addAll(batchMap.values());
                return this.failedDelayedEntries;
            }
        });
    }

    private void callBeforeStoreListeners(DelayedEntry entry) {
        for (StoreListener listener : this.storeListeners) {
            listener.beforeStore(StoreEvent.createStoreEvent(entry));
        }
    }

    private void callAfterStoreListeners(DelayedEntry entry) {
        for (StoreListener listener : this.storeListeners) {
            listener.afterStore(StoreEvent.createStoreEvent(entry));
        }
    }

    @Override
    public void callBeforeStoreListeners(Collection<DelayedEntry> entries) {
        for (DelayedEntry entry : entries) {
            this.callBeforeStoreListeners(entry);
        }
    }

    @Override
    public void addStoreListener(StoreListener listeners) {
        this.storeListeners.add(listeners);
    }

    @Override
    public void flush(WriteBehindQueue queue) {
        int size = queue.size();
        if (size == 0) {
            return;
        }
        ArrayList<DelayedEntry> delayedEntries = new ArrayList<DelayedEntry>(size);
        queue.drainTo(delayedEntries);
        this.flushInternal(delayedEntries);
    }

    @Override
    public void flush(DelayedEntry entry) {
        List<DelayedEntry> entries = Collections.singletonList(entry);
        this.flushInternal(entries);
    }

    private void flushInternal(List<DelayedEntry> delayedEntries) {
        this.sort(delayedEntries);
        Map<Integer, List<DelayedEntry>> failedStoreOpPerPartition = this.process(delayedEntries);
        if (failedStoreOpPerPartition.size() > 0) {
            this.printErrorLog(failedStoreOpPerPartition);
        }
    }

    private void printErrorLog(Map<Integer, List<DelayedEntry>> failsPerPartition) {
        int size = 0;
        Collection<List<DelayedEntry>> values = failsPerPartition.values();
        for (Collection collection : values) {
            size += collection.size();
        }
        String logMessage = String.format("Map store flush operation can not be done for %d entries", size);
        this.logger.severe(logMessage);
    }

    @Override
    public void callAfterStoreListeners(Collection<DelayedEntry> entries) {
        for (DelayedEntry entry : entries) {
            this.callAfterStoreListeners(entry);
        }
    }

    private Map<Integer, List<DelayedEntry>> doStoreUsingBatchSize(List<DelayedEntry> sortedDelayedEntries) {
        List<DelayedEntry> delayedEntryList;
        HashMap<Integer, List<DelayedEntry>> failsPerPartition = new HashMap<Integer, List<DelayedEntry>>();
        int page = 0;
        while ((delayedEntryList = this.getBatchChunk(sortedDelayedEntries, this.writeBatchSize, page++)) != null) {
            Map<Integer, List<DelayedEntry>> fails = this.processInternal(delayedEntryList);
            Set<Map.Entry<Integer, List<DelayedEntry>>> entries = fails.entrySet();
            for (Map.Entry<Integer, List<DelayedEntry>> entry : entries) {
                this.addFailsTo(failsPerPartition, entry.getValue());
            }
        }
        return failsPerPartition;
    }

    private List<DelayedEntry> retryCall(RetryTask task) {
        int k;
        boolean result = false;
        Exception exception = null;
        for (k = 0; k < 3; ++k) {
            try {
                result = task.run();
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
            catch (Exception ex) {
                exception = ex;
            }
            if (result) break;
            this.sleepSeconds(1L);
        }
        if (k > 0 && !result) {
            List<DelayedEntry> failureList = task.failureList();
            this.logger.severe("Number of entries which could not be stored is = [" + failureList.size() + "], Hazelcast will indefinitely retry to store them", exception);
            return failureList;
        }
        return Collections.emptyList();
    }

    private void sort(List<DelayedEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return;
        }
        Collections.sort(entries, DELAYED_ENTRY_COMPARATOR);
    }

    private void sleepSeconds(long secs) {
        try {
            TimeUnit.SECONDS.sleep(secs);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static interface RetryTask<T> {
        public boolean run() throws Exception;

        public List<T> failureList();
    }
}

