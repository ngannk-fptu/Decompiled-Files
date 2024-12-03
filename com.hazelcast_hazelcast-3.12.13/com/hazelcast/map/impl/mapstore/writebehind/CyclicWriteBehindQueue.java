/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.map.impl.mapstore.writebehind.IPredicate;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import com.hazelcast.map.impl.mapstore.writebehind.entry.DelayedEntry;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.util.MutableInteger;
import com.hazelcast.util.Preconditions;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class CyclicWriteBehindQueue
implements WriteBehindQueue<DelayedEntry> {
    private final Deque<DelayedEntry> deque = new ArrayDeque<DelayedEntry>();
    private final Map<Data, MutableInteger> index = new HashMap<Data, MutableInteger>();

    @Override
    public void addFirst(Collection<DelayedEntry> collection) {
        for (DelayedEntry entry : collection) {
            this.deque.addFirst(entry);
        }
        this.addCountIndex(collection);
    }

    @Override
    public void addLast(DelayedEntry entry) {
        this.deque.addLast(entry);
        this.addCountIndex(entry);
    }

    @Override
    public DelayedEntry peek() {
        return this.deque.peek();
    }

    @Override
    public boolean removeFirstOccurrence(DelayedEntry entry) {
        DelayedEntry removedEntry = this.deque.pollFirst();
        if (removedEntry == null) {
            return false;
        }
        this.decreaseCountIndex(entry);
        return true;
    }

    @Override
    public boolean contains(DelayedEntry entry) {
        Data key = (Data)entry.getKey();
        return this.index.containsKey(key);
    }

    @Override
    public int size() {
        return this.deque.size();
    }

    @Override
    public void clear() {
        this.deque.clear();
        this.resetCountIndex();
    }

    @Override
    public int drainTo(Collection<DelayedEntry> collection) {
        Preconditions.checkNotNull(collection, "collection can not be null");
        Iterator<DelayedEntry> iterator = this.deque.iterator();
        while (iterator.hasNext()) {
            DelayedEntry e = iterator.next();
            collection.add(e);
            iterator.remove();
        }
        this.resetCountIndex();
        return collection.size();
    }

    @Override
    public List<DelayedEntry> asList() {
        return Collections.unmodifiableList(new ArrayList<DelayedEntry>(this.deque));
    }

    @Override
    public void filter(IPredicate<DelayedEntry> predicate, Collection<DelayedEntry> collection) {
        for (DelayedEntry e : this.deque) {
            if (!predicate.test(e)) break;
            collection.add(e);
        }
    }

    private void addCountIndex(DelayedEntry entry) {
        Map<Data, MutableInteger> index = this.index;
        Data key = (Data)entry.getKey();
        MutableInteger count = index.get(key);
        if (count == null) {
            count = new MutableInteger();
        }
        ++count.value;
        index.put(key, count);
    }

    private void addCountIndex(Collection<DelayedEntry> collection) {
        for (DelayedEntry entry : collection) {
            this.addCountIndex(entry);
        }
    }

    private void decreaseCountIndex(DelayedEntry entry) {
        Map<Data, MutableInteger> index = this.index;
        Data key = (Data)entry.getKey();
        MutableInteger count = index.get(key);
        if (count == null) {
            return;
        }
        --count.value;
        if (count.value == 0) {
            index.remove(key);
        } else {
            index.put(key, count);
        }
    }

    private void resetCountIndex() {
        this.index.clear();
    }
}

