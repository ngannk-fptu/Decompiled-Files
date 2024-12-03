/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.util.internal;

import com.fasterxml.jackson.databind.util.internal.Linked;
import com.fasterxml.jackson.databind.util.internal.LinkedDeque;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class PrivateMaxEntriesMap<K, V>
extends AbstractMap<K, V>
implements ConcurrentMap<K, V>,
Serializable {
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    static final long MAXIMUM_CAPACITY = 9223372034707292160L;
    static final int NUMBER_OF_READ_BUFFERS = Math.min(4, PrivateMaxEntriesMap.ceilingNextPowerOfTwo(NCPU));
    static final int READ_BUFFERS_MASK = NUMBER_OF_READ_BUFFERS - 1;
    static final int READ_BUFFER_THRESHOLD = 4;
    static final int READ_BUFFER_DRAIN_THRESHOLD = 8;
    static final int READ_BUFFER_SIZE = 16;
    static final int READ_BUFFER_INDEX_MASK = 15;
    static final int WRITE_BUFFER_DRAIN_THRESHOLD = 16;
    final ConcurrentMap<K, Node<K, V>> data;
    final int concurrencyLevel;
    final long[] readBufferReadCount;
    final LinkedDeque<Node<K, V>> evictionDeque;
    final AtomicLong weightedSize;
    final AtomicLong capacity;
    final Lock evictionLock;
    final Queue<Runnable> writeBuffer;
    final AtomicLongArray readBufferWriteCount;
    final AtomicLongArray readBufferDrainAtWriteCount;
    final AtomicReferenceArray<Node<K, V>> readBuffers;
    final AtomicReference<DrainStatus> drainStatus;
    transient Set<K> keySet;
    transient Collection<V> values;
    transient Set<Map.Entry<K, V>> entrySet;
    static final long serialVersionUID = 1L;

    static int ceilingNextPowerOfTwo(int x) {
        return 1 << 32 - Integer.numberOfLeadingZeros(x - 1);
    }

    private static int readBufferIndex(int bufferIndex, int entryIndex) {
        return 16 * bufferIndex + entryIndex;
    }

    PrivateMaxEntriesMap(Builder<K, V> builder) {
        this.concurrencyLevel = builder.concurrencyLevel;
        this.capacity = new AtomicLong(Math.min(builder.capacity, 9223372034707292160L));
        this.data = new ConcurrentHashMap<K, Node<K, V>>(builder.initialCapacity, 0.75f, this.concurrencyLevel);
        this.evictionLock = new ReentrantLock();
        this.weightedSize = new AtomicLong();
        this.evictionDeque = new LinkedDeque();
        this.writeBuffer = new ConcurrentLinkedQueue<Runnable>();
        this.drainStatus = new AtomicReference<DrainStatus>(DrainStatus.IDLE);
        this.readBufferReadCount = new long[NUMBER_OF_READ_BUFFERS];
        this.readBufferWriteCount = new AtomicLongArray(NUMBER_OF_READ_BUFFERS);
        this.readBufferDrainAtWriteCount = new AtomicLongArray(NUMBER_OF_READ_BUFFERS);
        this.readBuffers = new AtomicReferenceArray(NUMBER_OF_READ_BUFFERS * 16);
    }

    static void checkNotNull(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    public long capacity() {
        return this.capacity.get();
    }

    public void setCapacity(long capacity) {
        PrivateMaxEntriesMap.checkArgument(capacity >= 0L);
        this.evictionLock.lock();
        try {
            this.capacity.lazySet(Math.min(capacity, 9223372034707292160L));
            this.drainBuffers();
            this.evict();
        }
        finally {
            this.evictionLock.unlock();
        }
    }

    boolean hasOverflowed() {
        return this.weightedSize.get() > this.capacity.get();
    }

    void evict() {
        while (this.hasOverflowed()) {
            Node node = (Node)this.evictionDeque.poll();
            if (node == null) {
                return;
            }
            this.data.remove(node.key, node);
            this.makeDead(node);
        }
    }

    void afterRead(Node<K, V> node) {
        int bufferIndex = PrivateMaxEntriesMap.readBufferIndex();
        long writeCount = this.recordRead(bufferIndex, node);
        this.drainOnReadIfNeeded(bufferIndex, writeCount);
    }

    static int readBufferIndex() {
        return (int)Thread.currentThread().getId() & READ_BUFFERS_MASK;
    }

    long recordRead(int bufferIndex, Node<K, V> node) {
        long writeCount = this.readBufferWriteCount.get(bufferIndex);
        this.readBufferWriteCount.lazySet(bufferIndex, writeCount + 1L);
        int index = (int)(writeCount & 0xFL);
        this.readBuffers.lazySet(PrivateMaxEntriesMap.readBufferIndex(bufferIndex, index), node);
        return writeCount;
    }

    void drainOnReadIfNeeded(int bufferIndex, long writeCount) {
        long pending = writeCount - this.readBufferDrainAtWriteCount.get(bufferIndex);
        boolean delayable = pending < 4L;
        DrainStatus status = this.drainStatus.get();
        if (status.shouldDrainBuffers(delayable)) {
            this.tryToDrainBuffers();
        }
    }

    void afterWrite(Runnable task) {
        this.writeBuffer.add(task);
        this.drainStatus.lazySet(DrainStatus.REQUIRED);
        this.tryToDrainBuffers();
    }

    void tryToDrainBuffers() {
        if (this.evictionLock.tryLock()) {
            try {
                this.drainStatus.lazySet(DrainStatus.PROCESSING);
                this.drainBuffers();
            }
            finally {
                this.drainStatus.compareAndSet(DrainStatus.PROCESSING, DrainStatus.IDLE);
                this.evictionLock.unlock();
            }
        }
    }

    void drainBuffers() {
        this.drainReadBuffers();
        this.drainWriteBuffer();
    }

    void drainReadBuffers() {
        int start = (int)Thread.currentThread().getId();
        int end = start + NUMBER_OF_READ_BUFFERS;
        for (int i = start; i < end; ++i) {
            this.drainReadBuffer(i & READ_BUFFERS_MASK);
        }
    }

    void drainReadBuffer(int bufferIndex) {
        int index;
        int arrayIndex;
        Node<K, V> node;
        long writeCount = this.readBufferWriteCount.get(bufferIndex);
        for (int i = 0; i < 8 && (node = this.readBuffers.get(arrayIndex = PrivateMaxEntriesMap.readBufferIndex(bufferIndex, index = (int)(this.readBufferReadCount[bufferIndex] & 0xFL)))) != null; ++i) {
            this.readBuffers.lazySet(arrayIndex, null);
            this.applyRead(node);
            int n = bufferIndex;
            this.readBufferReadCount[n] = this.readBufferReadCount[n] + 1L;
        }
        this.readBufferDrainAtWriteCount.lazySet(bufferIndex, writeCount);
    }

    void applyRead(Node<K, V> node) {
        if (this.evictionDeque.contains(node)) {
            this.evictionDeque.moveToBack(node);
        }
    }

    void drainWriteBuffer() {
        Runnable task;
        for (int i = 0; i < 16 && (task = this.writeBuffer.poll()) != null; ++i) {
            task.run();
        }
    }

    boolean tryToRetire(Node<K, V> node, WeightedValue<V> expect) {
        if (expect.isAlive()) {
            WeightedValue retired = new WeightedValue(expect.value, -expect.weight);
            return node.compareAndSet(expect, retired);
        }
        return false;
    }

    void makeRetired(Node<K, V> node) {
        WeightedValue retired;
        WeightedValue current;
        do {
            if ((current = (WeightedValue)node.get()).isAlive()) continue;
            return;
        } while (!node.compareAndSet(current, retired = new WeightedValue(current.value, -current.weight)));
    }

    void makeDead(Node<K, V> node) {
        WeightedValue dead;
        WeightedValue current;
        while (!node.compareAndSet(current = (WeightedValue)node.get(), dead = new WeightedValue(current.value, 0))) {
        }
        this.weightedSize.lazySet(this.weightedSize.get() - (long)Math.abs(current.weight));
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public void clear() {
        this.evictionLock.lock();
        try {
            Runnable task;
            Node node;
            while ((node = (Node)this.evictionDeque.poll()) != null) {
                this.data.remove(node.key, node);
                this.makeDead(node);
            }
            for (int i = 0; i < this.readBuffers.length(); ++i) {
                this.readBuffers.lazySet(i, null);
            }
            while ((task = this.writeBuffer.poll()) != null) {
                task.run();
            }
        }
        finally {
            this.evictionLock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return this.data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        PrivateMaxEntriesMap.checkNotNull(value);
        for (Node node : this.data.values()) {
            if (!node.getValue().equals(value)) continue;
            return true;
        }
        return false;
    }

    @Override
    public V get(Object key) {
        Node node = (Node)this.data.get(key);
        if (node == null) {
            return null;
        }
        this.afterRead(node);
        return node.getValue();
    }

    @Override
    public V put(K key, V value) {
        return this.put(key, value, false);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return this.put(key, value, true);
    }

    V put(K key, V value, boolean onlyIfAbsent) {
        WeightedValue oldWeightedValue;
        Node<K, WeightedValue<V>> prior;
        PrivateMaxEntriesMap.checkNotNull(key);
        PrivateMaxEntriesMap.checkNotNull(value);
        boolean weight = true;
        WeightedValue<V> weightedValue = new WeightedValue<V>(value, 1);
        Node<K, V> node = new Node<K, V>(key, weightedValue);
        block0: while (true) {
            if ((prior = this.data.putIfAbsent(node.key, node)) == null) {
                this.afterWrite(new AddTask(node, 1));
                return null;
            }
            if (onlyIfAbsent) {
                this.afterRead(prior);
                return prior.getValue();
            }
            do {
                if (!(oldWeightedValue = (WeightedValue)prior.get()).isAlive()) continue block0;
            } while (!prior.compareAndSet(oldWeightedValue, weightedValue));
            break;
        }
        int weightedDifference = 1 - oldWeightedValue.weight;
        if (weightedDifference == 0) {
            this.afterRead(prior);
        } else {
            this.afterWrite(new UpdateTask(prior, weightedDifference));
        }
        return oldWeightedValue.value;
    }

    @Override
    public V remove(Object key) {
        Node node = (Node)this.data.remove(key);
        if (node == null) {
            return null;
        }
        this.makeRetired(node);
        this.afterWrite(new RemovalTask(node));
        return node.getValue();
    }

    @Override
    public boolean remove(Object key, Object value) {
        Node node = (Node)this.data.get(key);
        if (node == null || value == null) {
            return false;
        }
        WeightedValue weightedValue = (WeightedValue)node.get();
        while (weightedValue.contains(value)) {
            if (this.tryToRetire(node, weightedValue)) {
                if (!this.data.remove(key, node)) break;
                this.afterWrite(new RemovalTask(node));
                return true;
            }
            weightedValue = (WeightedValue)node.get();
            if (weightedValue.isAlive()) continue;
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        WeightedValue oldWeightedValue;
        PrivateMaxEntriesMap.checkNotNull(key);
        PrivateMaxEntriesMap.checkNotNull(value);
        boolean weight = true;
        WeightedValue<V> weightedValue = new WeightedValue<V>(value, 1);
        Node node = (Node)this.data.get(key);
        if (node == null) {
            return null;
        }
        do {
            if ((oldWeightedValue = (WeightedValue)node.get()).isAlive()) continue;
            return null;
        } while (!node.compareAndSet(oldWeightedValue, weightedValue));
        int weightedDifference = 1 - oldWeightedValue.weight;
        if (weightedDifference == 0) {
            this.afterRead(node);
        } else {
            this.afterWrite(new UpdateTask(node, weightedDifference));
        }
        return oldWeightedValue.value;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        WeightedValue weightedValue;
        PrivateMaxEntriesMap.checkNotNull(key);
        PrivateMaxEntriesMap.checkNotNull(oldValue);
        PrivateMaxEntriesMap.checkNotNull(newValue);
        boolean weight = true;
        WeightedValue<V> newWeightedValue = new WeightedValue<V>(newValue, 1);
        Node node = (Node)this.data.get(key);
        if (node == null) {
            return false;
        }
        do {
            if ((weightedValue = (WeightedValue)node.get()).isAlive() && weightedValue.contains(oldValue)) continue;
            return false;
        } while (!node.compareAndSet(weightedValue, newWeightedValue));
        int weightedDifference = 1 - weightedValue.weight;
        if (weightedDifference == 0) {
            this.afterRead(node);
        } else {
            this.afterWrite(new UpdateTask(node, weightedDifference));
        }
        return true;
    }

    @Override
    public Set<K> keySet() {
        KeySet ks = this.keySet;
        return ks == null ? (this.keySet = new KeySet()) : ks;
    }

    @Override
    public Collection<V> values() {
        Values vs = this.values;
        return vs == null ? (this.values = new Values()) : vs;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySet es = this.entrySet;
        return es == null ? (this.entrySet = new EntrySet()) : es;
    }

    Object writeReplace() {
        return new SerializationProxy(this);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    public static final class Builder<K, V> {
        static final int DEFAULT_CONCURRENCY_LEVEL = 16;
        static final int DEFAULT_INITIAL_CAPACITY = 16;
        int concurrencyLevel = 16;
        int initialCapacity = 16;
        long capacity = -1L;

        public Builder<K, V> initialCapacity(int initialCapacity) {
            PrivateMaxEntriesMap.checkArgument(initialCapacity >= 0);
            this.initialCapacity = initialCapacity;
            return this;
        }

        public Builder<K, V> maximumCapacity(long capacity) {
            PrivateMaxEntriesMap.checkArgument(capacity >= 0L);
            this.capacity = capacity;
            return this;
        }

        public Builder<K, V> concurrencyLevel(int concurrencyLevel) {
            PrivateMaxEntriesMap.checkArgument(concurrencyLevel > 0);
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }

        public PrivateMaxEntriesMap<K, V> build() {
            PrivateMaxEntriesMap.checkState(this.capacity >= 0L);
            return new PrivateMaxEntriesMap(this);
        }
    }

    static final class SerializationProxy<K, V>
    implements Serializable {
        final int concurrencyLevel;
        final Map<K, V> data;
        final long capacity;
        static final long serialVersionUID = 1L;

        SerializationProxy(PrivateMaxEntriesMap<K, V> map) {
            this.concurrencyLevel = map.concurrencyLevel;
            this.data = new HashMap<K, V>(map);
            this.capacity = map.capacity.get();
        }

        Object readResolve() {
            PrivateMaxEntriesMap<K, V> map = new Builder().maximumCapacity(this.capacity).build();
            map.putAll(this.data);
            return map;
        }
    }

    final class WriteThroughEntry
    extends AbstractMap.SimpleEntry<K, V> {
        static final long serialVersionUID = 1L;

        WriteThroughEntry(Node<K, V> node) {
            super(node.key, node.getValue());
        }

        @Override
        public V setValue(V value) {
            PrivateMaxEntriesMap.this.put(this.getKey(), value);
            return super.setValue(value);
        }

        Object writeReplace() {
            return new AbstractMap.SimpleEntry(this);
        }
    }

    final class EntryIterator
    implements Iterator<Map.Entry<K, V>> {
        final Iterator<Node<K, V>> iterator;
        Node<K, V> current;

        EntryIterator() {
            this.iterator = PrivateMaxEntriesMap.this.data.values().iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            this.current = this.iterator.next();
            return new WriteThroughEntry(this.current);
        }

        @Override
        public void remove() {
            PrivateMaxEntriesMap.checkState(this.current != null);
            PrivateMaxEntriesMap.this.remove(this.current.key);
            this.current = null;
        }
    }

    final class EntrySet
    extends AbstractSet<Map.Entry<K, V>> {
        final PrivateMaxEntriesMap<K, V> map;

        EntrySet() {
            this.map = PrivateMaxEntriesMap.this;
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
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public boolean contains(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry)obj;
            Node node = (Node)this.map.data.get(entry.getKey());
            return node != null && node.getValue().equals(entry.getValue());
        }

        @Override
        public boolean add(Map.Entry<K, V> entry) {
            throw new UnsupportedOperationException("ConcurrentLinkedHashMap does not allow add to be called on entrySet()");
        }

        @Override
        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry)obj;
            return this.map.remove(entry.getKey(), entry.getValue());
        }
    }

    final class ValueIterator
    implements Iterator<V> {
        final Iterator<Node<K, V>> iterator;
        Node<K, V> current;

        ValueIterator() {
            this.iterator = PrivateMaxEntriesMap.this.data.values().iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public V next() {
            this.current = this.iterator.next();
            return this.current.getValue();
        }

        @Override
        public void remove() {
            PrivateMaxEntriesMap.checkState(this.current != null);
            PrivateMaxEntriesMap.this.remove(this.current.key);
            this.current = null;
        }
    }

    final class Values
    extends AbstractCollection<V> {
        Values() {
        }

        @Override
        public int size() {
            return PrivateMaxEntriesMap.this.size();
        }

        @Override
        public void clear() {
            PrivateMaxEntriesMap.this.clear();
        }

        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public boolean contains(Object o) {
            return PrivateMaxEntriesMap.this.containsValue(o);
        }
    }

    final class KeyIterator
    implements Iterator<K> {
        final Iterator<K> iterator;
        K current;

        KeyIterator() {
            this.iterator = PrivateMaxEntriesMap.this.data.keySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public K next() {
            this.current = this.iterator.next();
            return this.current;
        }

        @Override
        public void remove() {
            PrivateMaxEntriesMap.checkState(this.current != null);
            PrivateMaxEntriesMap.this.remove(this.current);
            this.current = null;
        }
    }

    final class KeySet
    extends AbstractSet<K> {
        final PrivateMaxEntriesMap<K, V> map;

        KeySet() {
            this.map = PrivateMaxEntriesMap.this;
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
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public boolean contains(Object obj) {
            return PrivateMaxEntriesMap.this.containsKey(obj);
        }

        @Override
        public boolean remove(Object obj) {
            return this.map.remove(obj) != null;
        }

        @Override
        public Object[] toArray() {
            return this.map.data.keySet().toArray();
        }

        @Override
        public <T> T[] toArray(T[] array) {
            return this.map.data.keySet().toArray(array);
        }
    }

    static final class Node<K, V>
    extends AtomicReference<WeightedValue<V>>
    implements Linked<Node<K, V>> {
        final K key;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, WeightedValue<V> weightedValue) {
            super(weightedValue);
            this.key = key;
        }

        @Override
        public Node<K, V> getPrevious() {
            return this.prev;
        }

        @Override
        public void setPrevious(Node<K, V> prev) {
            this.prev = prev;
        }

        @Override
        public Node<K, V> getNext() {
            return this.next;
        }

        @Override
        public void setNext(Node<K, V> next) {
            this.next = next;
        }

        V getValue() {
            return ((WeightedValue)this.get()).value;
        }
    }

    static final class WeightedValue<V> {
        final int weight;
        final V value;

        WeightedValue(V value, int weight) {
            this.weight = weight;
            this.value = value;
        }

        boolean contains(Object o) {
            return o == this.value || this.value.equals(o);
        }

        boolean isAlive() {
            return this.weight > 0;
        }
    }

    static enum DrainStatus {
        IDLE{

            @Override
            boolean shouldDrainBuffers(boolean delayable) {
                return !delayable;
            }
        }
        ,
        REQUIRED{

            @Override
            boolean shouldDrainBuffers(boolean delayable) {
                return true;
            }
        }
        ,
        PROCESSING{

            @Override
            boolean shouldDrainBuffers(boolean delayable) {
                return false;
            }
        };


        abstract boolean shouldDrainBuffers(boolean var1);
    }

    final class UpdateTask
    implements Runnable {
        final int weightDifference;
        final Node<K, V> node;

        UpdateTask(Node<K, V> node, int weightDifference) {
            this.weightDifference = weightDifference;
            this.node = node;
        }

        @Override
        public void run() {
            PrivateMaxEntriesMap.this.weightedSize.lazySet(PrivateMaxEntriesMap.this.weightedSize.get() + (long)this.weightDifference);
            PrivateMaxEntriesMap.this.applyRead(this.node);
            PrivateMaxEntriesMap.this.evict();
        }
    }

    final class RemovalTask
    implements Runnable {
        final Node<K, V> node;

        RemovalTask(Node<K, V> node) {
            this.node = node;
        }

        @Override
        public void run() {
            PrivateMaxEntriesMap.this.evictionDeque.remove(this.node);
            PrivateMaxEntriesMap.this.makeDead(this.node);
        }
    }

    final class AddTask
    implements Runnable {
        final Node<K, V> node;
        final int weight;

        AddTask(Node<K, V> node, int weight) {
            this.weight = weight;
            this.node = node;
        }

        @Override
        public void run() {
            PrivateMaxEntriesMap.this.weightedSize.lazySet(PrivateMaxEntriesMap.this.weightedSize.get() + (long)this.weight);
            if (((WeightedValue)this.node.get()).isAlive()) {
                PrivateMaxEntriesMap.this.evictionDeque.add(this.node);
                PrivateMaxEntriesMap.this.evict();
            }
        }
    }
}

