/*
 * Decompiled with CFR 0.152.
 */
package mssql.googlecode.concurrentlinkedhashmap;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import mssql.googlecode.concurrentlinkedhashmap.EntryWeigher;
import mssql.googlecode.concurrentlinkedhashmap.EvictionListener;
import mssql.googlecode.concurrentlinkedhashmap.Linked;
import mssql.googlecode.concurrentlinkedhashmap.LinkedDeque;
import mssql.googlecode.concurrentlinkedhashmap.Weigher;
import mssql.googlecode.concurrentlinkedhashmap.Weighers;

public final class ConcurrentLinkedHashMap<K, V>
extends AbstractMap<K, V>
implements ConcurrentMap<K, V>,
Serializable {
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    static final long MAXIMUM_CAPACITY = 9223372034707292160L;
    static final int NUMBER_OF_READ_BUFFERS = ConcurrentLinkedHashMap.ceilingNextPowerOfTwo(NCPU);
    static final int READ_BUFFERS_MASK = NUMBER_OF_READ_BUFFERS - 1;
    static final int READ_BUFFER_THRESHOLD = 32;
    static final int READ_BUFFER_DRAIN_THRESHOLD = 64;
    static final int READ_BUFFER_SIZE = 128;
    static final int READ_BUFFER_INDEX_MASK = 127;
    static final int WRITE_BUFFER_DRAIN_THRESHOLD = 16;
    static final Queue<?> DISCARDING_QUEUE = new DiscardingQueue();
    final ConcurrentMap<K, Node<K, V>> data;
    final int concurrencyLevel;
    final long[] readBufferReadCount;
    final LinkedDeque<Node<K, V>> evictionDeque;
    final AtomicLong weightedSize;
    final AtomicLong capacity;
    final Lock evictionLock;
    final Queue<Runnable> writeBuffer;
    final AtomicLong[] readBufferWriteCount;
    final AtomicLong[] readBufferDrainAtWriteCount;
    final AtomicReference<Node<K, V>>[][] readBuffers;
    final AtomicReference<DrainStatus> drainStatus;
    final EntryWeigher<? super K, ? super V> weigher;
    final Queue<Node<K, V>> pendingNotifications;
    final EvictionListener<K, V> listener;
    transient Set<K> keySet;
    transient Collection<V> values;
    transient Set<Map.Entry<K, V>> entrySet;
    static final long serialVersionUID = 1L;

    static int ceilingNextPowerOfTwo(int x) {
        return 1 << 32 - Integer.numberOfLeadingZeros(x - 1);
    }

    private ConcurrentLinkedHashMap(Builder<K, V> builder) {
        this.concurrencyLevel = builder.concurrencyLevel;
        this.capacity = new AtomicLong(Math.min(builder.capacity, 9223372034707292160L));
        this.data = new ConcurrentHashMap<K, Node<K, V>>(builder.initialCapacity, 0.75f, this.concurrencyLevel);
        this.weigher = builder.weigher;
        this.evictionLock = new ReentrantLock();
        this.weightedSize = new AtomicLong();
        this.evictionDeque = new LinkedDeque();
        this.writeBuffer = new ConcurrentLinkedQueue<Runnable>();
        this.drainStatus = new AtomicReference<DrainStatus>(DrainStatus.IDLE);
        this.readBufferReadCount = new long[NUMBER_OF_READ_BUFFERS];
        this.readBufferWriteCount = new AtomicLong[NUMBER_OF_READ_BUFFERS];
        this.readBufferDrainAtWriteCount = new AtomicLong[NUMBER_OF_READ_BUFFERS];
        this.readBuffers = new AtomicReference[NUMBER_OF_READ_BUFFERS][128];
        for (int i = 0; i < NUMBER_OF_READ_BUFFERS; ++i) {
            this.readBufferWriteCount[i] = new AtomicLong();
            this.readBufferDrainAtWriteCount[i] = new AtomicLong();
            this.readBuffers[i] = new AtomicReference[128];
            for (int j = 0; j < 128; ++j) {
                this.readBuffers[i][j] = new AtomicReference();
            }
        }
        this.listener = builder.listener;
        this.pendingNotifications = this.listener == DiscardingListener.INSTANCE ? DISCARDING_QUEUE : new ConcurrentLinkedQueue();
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
        ConcurrentLinkedHashMap.checkArgument(capacity >= 0L);
        this.evictionLock.lock();
        try {
            this.capacity.lazySet(Math.min(capacity, 9223372034707292160L));
            this.drainBuffers();
            this.evict();
        }
        finally {
            this.evictionLock.unlock();
        }
        this.notifyListener();
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
            if (this.data.remove(node.key, node)) {
                this.pendingNotifications.add(node);
            }
            this.makeDead(node);
        }
    }

    void afterRead(Node<K, V> node) {
        int bufferIndex = ConcurrentLinkedHashMap.readBufferIndex();
        long writeCount = this.recordRead(bufferIndex, node);
        this.drainOnReadIfNeeded(bufferIndex, writeCount);
        this.notifyListener();
    }

    static int readBufferIndex() {
        return (int)Thread.currentThread().getId() & READ_BUFFERS_MASK;
    }

    long recordRead(int bufferIndex, Node<K, V> node) {
        AtomicLong counter = this.readBufferWriteCount[bufferIndex];
        long writeCount = counter.get();
        counter.lazySet(writeCount + 1L);
        int index = (int)(writeCount & 0x7FL);
        this.readBuffers[bufferIndex][index].lazySet(node);
        return writeCount;
    }

    void drainOnReadIfNeeded(int bufferIndex, long writeCount) {
        long pending = writeCount - this.readBufferDrainAtWriteCount[bufferIndex].get();
        boolean delayable = pending < 32L;
        DrainStatus status = this.drainStatus.get();
        if (status.shouldDrainBuffers(delayable)) {
            this.tryToDrainBuffers();
        }
    }

    void afterWrite(Runnable task) {
        this.writeBuffer.add(task);
        this.drainStatus.lazySet(DrainStatus.REQUIRED);
        this.tryToDrainBuffers();
        this.notifyListener();
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
        AtomicReference<Node<K, V>> slot;
        Node<K, V> node;
        long writeCount = this.readBufferWriteCount[bufferIndex].get();
        for (int i = 0; i < 64 && (node = (slot = this.readBuffers[bufferIndex][index = (int)(this.readBufferReadCount[bufferIndex] & 0x7FL)]).get()) != null; ++i) {
            slot.lazySet(null);
            this.applyRead(node);
            int n = bufferIndex;
            this.readBufferReadCount[n] = this.readBufferReadCount[n] + 1L;
        }
        this.readBufferDrainAtWriteCount[bufferIndex].lazySet(writeCount);
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

    void notifyListener() {
        Node<K, V> node;
        while ((node = this.pendingNotifications.poll()) != null) {
            this.listener.onEviction(node.key, node.getValue());
        }
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public int size() {
        return this.data.size();
    }

    public long weightedSize() {
        return Math.max(0L, this.weightedSize.get());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
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
            AtomicReference<Node<K, V>>[][] atomicReferenceArray = this.readBuffers;
            int n = atomicReferenceArray.length;
            for (int i = 0; i < n; ++i) {
                AtomicReference<Node<K, V>>[] buffer;
                for (AtomicReference<Node<K, Object>> atomicReference : buffer = atomicReferenceArray[i]) {
                    atomicReference.lazySet(null);
                }
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
        ConcurrentLinkedHashMap.checkNotNull(value);
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

    public V getQuietly(Object key) {
        Node node = (Node)this.data.get(key);
        return node == null ? null : (V)node.getValue();
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
        ConcurrentLinkedHashMap.checkNotNull(key);
        ConcurrentLinkedHashMap.checkNotNull(value);
        int weight = this.weigher.weightOf(key, value);
        WeightedValue<V> weightedValue = new WeightedValue<V>(value, weight);
        Node<K, V> node = new Node<K, V>(key, weightedValue);
        block0: while (true) {
            if ((prior = this.data.putIfAbsent(node.key, node)) == null) {
                this.afterWrite(new AddTask(node, weight));
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
        int weightedDifference = weight - oldWeightedValue.weight;
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
        ConcurrentLinkedHashMap.checkNotNull(key);
        ConcurrentLinkedHashMap.checkNotNull(value);
        int weight = this.weigher.weightOf(key, value);
        WeightedValue<V> weightedValue = new WeightedValue<V>(value, weight);
        Node node = (Node)this.data.get(key);
        if (node == null) {
            return null;
        }
        do {
            if ((oldWeightedValue = (WeightedValue)node.get()).isAlive()) continue;
            return null;
        } while (!node.compareAndSet(oldWeightedValue, weightedValue));
        int weightedDifference = weight - oldWeightedValue.weight;
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
        ConcurrentLinkedHashMap.checkNotNull(key);
        ConcurrentLinkedHashMap.checkNotNull(oldValue);
        ConcurrentLinkedHashMap.checkNotNull(newValue);
        int weight = this.weigher.weightOf(key, newValue);
        WeightedValue<V> newWeightedValue = new WeightedValue<V>(newValue, weight);
        Node node = (Node)this.data.get(key);
        if (node == null) {
            return false;
        }
        do {
            if ((weightedValue = (WeightedValue)node.get()).isAlive() && weightedValue.contains(oldValue)) continue;
            return false;
        } while (!node.compareAndSet(weightedValue, newWeightedValue));
        int weightedDifference = weight - weightedValue.weight;
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

    public Set<K> ascendingKeySet() {
        return this.ascendingKeySetWithLimit(Integer.MAX_VALUE);
    }

    public Set<K> ascendingKeySetWithLimit(int limit) {
        return this.orderedKeySet(true, limit);
    }

    public Set<K> descendingKeySet() {
        return this.descendingKeySetWithLimit(Integer.MAX_VALUE);
    }

    public Set<K> descendingKeySetWithLimit(int limit) {
        return this.orderedKeySet(false, limit);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Set<K> orderedKeySet(boolean ascending, int limit) {
        ConcurrentLinkedHashMap.checkArgument(limit >= 0);
        this.evictionLock.lock();
        try {
            Iterator<Node<K, V>> iterator;
            this.drainBuffers();
            int initialCapacity = this.weigher == Weighers.entrySingleton() ? Math.min(limit, (int)this.weightedSize()) : 16;
            LinkedHashSet keys = new LinkedHashSet(initialCapacity);
            Iterator<Node<K, V>> iterator2 = iterator = ascending ? this.evictionDeque.iterator() : this.evictionDeque.descendingIterator();
            while (iterator.hasNext() && limit > keys.size()) {
                keys.add(iterator.next().key);
            }
            Set set = Collections.unmodifiableSet(keys);
            return set;
        }
        finally {
            this.evictionLock.unlock();
        }
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

    public Map<K, V> ascendingMap() {
        return this.ascendingMapWithLimit(Integer.MAX_VALUE);
    }

    public Map<K, V> ascendingMapWithLimit(int limit) {
        return this.orderedMap(true, limit);
    }

    public Map<K, V> descendingMap() {
        return this.descendingMapWithLimit(Integer.MAX_VALUE);
    }

    public Map<K, V> descendingMapWithLimit(int limit) {
        return this.orderedMap(false, limit);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Map<K, V> orderedMap(boolean ascending, int limit) {
        ConcurrentLinkedHashMap.checkArgument(limit >= 0);
        this.evictionLock.lock();
        try {
            Iterator<Node<K, V>> iterator;
            this.drainBuffers();
            int initialCapacity = this.weigher == Weighers.entrySingleton() ? Math.min(limit, (int)this.weightedSize()) : 16;
            LinkedHashMap map = new LinkedHashMap(initialCapacity);
            Iterator<Node<K, V>> iterator2 = iterator = ascending ? this.evictionDeque.iterator() : this.evictionDeque.descendingIterator();
            while (iterator.hasNext() && limit > map.size()) {
                Node<K, V> node = iterator.next();
                map.put(node.key, node.getValue());
            }
            Map map2 = Collections.unmodifiableMap(map);
            return map2;
        }
        finally {
            this.evictionLock.unlock();
        }
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
        EvictionListener<K, V> listener;
        EntryWeigher<? super K, ? super V> weigher = Weighers.entrySingleton();
        int concurrencyLevel = 16;
        int initialCapacity = 16;
        long capacity = -1L;

        public Builder() {
            this.listener = DiscardingListener.INSTANCE;
        }

        public Builder<K, V> initialCapacity(int initialCapacity) {
            ConcurrentLinkedHashMap.checkArgument(initialCapacity >= 0);
            this.initialCapacity = initialCapacity;
            return this;
        }

        public Builder<K, V> maximumWeightedCapacity(long capacity) {
            ConcurrentLinkedHashMap.checkArgument(capacity >= 0L);
            this.capacity = capacity;
            return this;
        }

        public Builder<K, V> concurrencyLevel(int concurrencyLevel) {
            ConcurrentLinkedHashMap.checkArgument(concurrencyLevel > 0);
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }

        public Builder<K, V> listener(EvictionListener<K, V> listener) {
            ConcurrentLinkedHashMap.checkNotNull(listener);
            this.listener = listener;
            return this;
        }

        public Builder<K, V> weigher(Weigher<? super V> weigher) {
            this.weigher = weigher == Weighers.singleton() ? Weighers.entrySingleton() : new BoundedEntryWeigher(Weighers.asEntryWeigher(weigher));
            return this;
        }

        public Builder<K, V> weigher(EntryWeigher<? super K, ? super V> weigher) {
            this.weigher = weigher == Weighers.entrySingleton() ? Weighers.entrySingleton() : new BoundedEntryWeigher<K, V>(weigher);
            return this;
        }

        public ConcurrentLinkedHashMap<K, V> build() {
            ConcurrentLinkedHashMap.checkState(this.capacity >= 0L);
            return new ConcurrentLinkedHashMap(this);
        }
    }

    static final class SerializationProxy<K, V>
    implements Serializable {
        final EntryWeigher<? super K, ? super V> weigher;
        final EvictionListener<K, V> listener;
        final int concurrencyLevel;
        final Map<K, V> data;
        final long capacity;
        static final long serialVersionUID = 1L;

        SerializationProxy(ConcurrentLinkedHashMap<K, V> map) {
            this.concurrencyLevel = map.concurrencyLevel;
            this.data = new HashMap<K, V>(map);
            this.capacity = map.capacity.get();
            this.listener = map.listener;
            this.weigher = map.weigher;
        }

        Object readResolve() {
            ConcurrentLinkedHashMap<K, V> map = new Builder().concurrencyLevel(this.concurrencyLevel).maximumWeightedCapacity(this.capacity).listener(this.listener).weigher(this.weigher).build();
            map.putAll(this.data);
            return map;
        }
    }

    static enum DiscardingListener implements EvictionListener<Object, Object>
    {
        INSTANCE;


        @Override
        public void onEviction(Object key, Object value) {
        }
    }

    static final class DiscardingQueue
    extends AbstractQueue<Object> {
        DiscardingQueue() {
        }

        @Override
        public boolean add(Object e) {
            return true;
        }

        @Override
        public boolean offer(Object e) {
            return true;
        }

        @Override
        public Object poll() {
            return null;
        }

        @Override
        public Object peek() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Iterator<Object> iterator() {
            return Collections.emptyList().iterator();
        }
    }

    static final class BoundedEntryWeigher<K, V>
    implements EntryWeigher<K, V>,
    Serializable {
        static final long serialVersionUID = 1L;
        final EntryWeigher<? super K, ? super V> weigher;

        BoundedEntryWeigher(EntryWeigher<? super K, ? super V> weigher) {
            ConcurrentLinkedHashMap.checkNotNull(weigher);
            this.weigher = weigher;
        }

        @Override
        public int weightOf(K key, V value) {
            int weight = this.weigher.weightOf(key, value);
            ConcurrentLinkedHashMap.checkArgument(weight >= 1);
            return weight;
        }

        Object writeReplace() {
            return this.weigher;
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
            ConcurrentLinkedHashMap.this.put(this.getKey(), value);
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
            this.iterator = ConcurrentLinkedHashMap.this.data.values().iterator();
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
            ConcurrentLinkedHashMap.checkState(this.current != null);
            ConcurrentLinkedHashMap.this.remove(this.current.key);
            this.current = null;
        }
    }

    final class EntrySet
    extends AbstractSet<Map.Entry<K, V>> {
        final ConcurrentLinkedHashMap<K, V> map;

        EntrySet() {
            this.map = ConcurrentLinkedHashMap.this;
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
            return this.map.putIfAbsent(entry.getKey(), entry.getValue()) == null;
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
            this.iterator = ConcurrentLinkedHashMap.this.data.values().iterator();
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
            ConcurrentLinkedHashMap.checkState(this.current != null);
            ConcurrentLinkedHashMap.this.remove(this.current.key);
            this.current = null;
        }
    }

    final class Values
    extends AbstractCollection<V> {
        Values() {
        }

        @Override
        public int size() {
            return ConcurrentLinkedHashMap.this.size();
        }

        @Override
        public void clear() {
            ConcurrentLinkedHashMap.this.clear();
        }

        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public boolean contains(Object o) {
            return ConcurrentLinkedHashMap.this.containsValue(o);
        }
    }

    final class KeyIterator
    implements Iterator<K> {
        final Iterator<K> iterator;
        K current;

        KeyIterator() {
            this.iterator = ConcurrentLinkedHashMap.this.data.keySet().iterator();
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
            ConcurrentLinkedHashMap.checkState(this.current != null);
            ConcurrentLinkedHashMap.this.remove(this.current);
            this.current = null;
        }
    }

    final class KeySet
    extends AbstractSet<K> {
        final ConcurrentLinkedHashMap<K, V> map;

        KeySet() {
            this.map = ConcurrentLinkedHashMap.this;
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
            return ConcurrentLinkedHashMap.this.containsKey(obj);
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

        boolean isRetired() {
            return this.weight < 0;
        }

        boolean isDead() {
            return this.weight == 0;
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

        public UpdateTask(Node<K, V> node, int weightDifference) {
            this.weightDifference = weightDifference;
            this.node = node;
        }

        @Override
        public void run() {
            ConcurrentLinkedHashMap.this.weightedSize.lazySet(ConcurrentLinkedHashMap.this.weightedSize.get() + (long)this.weightDifference);
            ConcurrentLinkedHashMap.this.applyRead(this.node);
            ConcurrentLinkedHashMap.this.evict();
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
            ConcurrentLinkedHashMap.this.evictionDeque.remove(this.node);
            ConcurrentLinkedHashMap.this.makeDead(this.node);
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
            ConcurrentLinkedHashMap.this.weightedSize.lazySet(ConcurrentLinkedHashMap.this.weightedSize.get() + (long)this.weight);
            if (((WeightedValue)this.node.get()).isAlive()) {
                ConcurrentLinkedHashMap.this.evictionDeque.add(this.node);
                ConcurrentLinkedHashMap.this.evict();
            }
        }
    }
}

