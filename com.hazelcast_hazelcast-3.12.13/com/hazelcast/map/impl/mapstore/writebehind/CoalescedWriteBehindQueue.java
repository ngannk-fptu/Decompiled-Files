/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.map.impl.mapstore.writebehind.IPredicate;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class CoalescedWriteBehindQueue
implements WriteBehindQueue<DelayedEntry> {
    private Map<Data, DelayedEntry> map = new LinkedHashMap<Data, DelayedEntry>();

    CoalescedWriteBehindQueue() {
    }

    @Override
    public void addFirst(Collection<DelayedEntry> collection) {
        if (CollectionUtil.isEmpty(collection)) {
            return;
        }
        int expectedCapacity = this.map.size() + collection.size();
        Map<Data, DelayedEntry> newMap = MapUtil.createLinkedHashMap(expectedCapacity);
        for (DelayedEntry next : collection) {
            newMap.put((Data)next.getKey(), next);
        }
        newMap.putAll(this.map);
        this.map = newMap;
    }

    @Override
    public void addLast(DelayedEntry delayedEntry) {
        if (delayedEntry == null) {
            return;
        }
        this.calculateStoreTime(delayedEntry);
        Data key = (Data)delayedEntry.getKey();
        this.map.put(key, delayedEntry);
    }

    @Override
    public DelayedEntry peek() {
        Collection<DelayedEntry> values = this.map.values();
        Iterator<DelayedEntry> iterator = values.iterator();
        if (iterator.hasNext()) {
            DelayedEntry value = iterator.next();
            return value;
        }
        return null;
    }

    @Override
    public boolean removeFirstOccurrence(DelayedEntry incoming) {
        Data incomingKey = (Data)incoming.getKey();
        Object incomingValue = incoming.getValue();
        DelayedEntry current = this.map.get(incomingKey);
        if (current == null) {
            return false;
        }
        if (current.getSequence() > incoming.getSequence()) {
            return false;
        }
        Object currentValue = current.getValue();
        if (incomingValue == null && currentValue == null || incomingValue != null && currentValue != null && incomingValue.equals(currentValue)) {
            this.map.remove(incomingKey);
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(DelayedEntry entry) {
        return this.map.containsKey(entry.getKey());
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public int drainTo(Collection<DelayedEntry> collection) {
        Preconditions.checkNotNull(collection, "collection can not be null");
        Collection<DelayedEntry> delayedEntries = this.map.values();
        for (DelayedEntry delayedEntry : delayedEntries) {
            collection.add(delayedEntry);
        }
        this.map.clear();
        return collection.size();
    }

    @Override
    public List<DelayedEntry> asList() {
        Collection<DelayedEntry> values = this.map.values();
        return Collections.unmodifiableList(new ArrayList<DelayedEntry>(values));
    }

    @Override
    public void filter(IPredicate<DelayedEntry> predicate, Collection<DelayedEntry> collection) {
        Collection<DelayedEntry> values = this.map.values();
        for (DelayedEntry e : values) {
            if (!predicate.test(e)) break;
            collection.add(e);
        }
    }

    private void calculateStoreTime(DelayedEntry delayedEntry) {
        Data key = (Data)delayedEntry.getKey();
        DelayedEntry currentEntry = this.map.get(key);
        if (currentEntry != null) {
            long currentStoreTime = currentEntry.getStoreTime();
            delayedEntry.setStoreTime(currentStoreTime);
        }
    }
}

