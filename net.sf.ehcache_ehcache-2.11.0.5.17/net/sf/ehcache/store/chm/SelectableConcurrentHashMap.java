/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.chm;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.statistics.StatisticBuilder;
import org.terracotta.statistics.observer.OperationObserver;

public class SelectableConcurrentHashMap {
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int MAXIMUM_CAPACITY = 0x40000000;
    private static final int MAX_SEGMENTS = 65536;
    private static final int RETRIES_BEFORE_LOCK = 2;
    private final int segmentMask;
    private final int segmentShift;
    private final Segment[] segments;
    private final Random rndm = new Random();
    private final PoolAccessor poolAccessor;
    private volatile long maxSize;
    private final RegisteredEventListeners cacheEventNotificationService;
    private Set<Object> keySet;
    private Set<Map.Entry<Object, Element>> entrySet;
    private Collection<Element> values;
    private final OperationObserver<CacheOperationOutcomes.EvictionOutcome> evictionObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.EvictionOutcome.class).named("eviction")).of(this)).build();

    public SelectableConcurrentHashMap(PoolAccessor poolAccessor, int concurrency, long maximumSize, RegisteredEventListeners cacheEventNotificationService) {
        this(poolAccessor, 16, 0.75f, concurrency, maximumSize, cacheEventNotificationService);
    }

    public SelectableConcurrentHashMap(PoolAccessor poolAccessor, int initialCapacity, float loadFactor, int concurrency, long maximumSize, RegisteredEventListeners cacheEventNotificationService) {
        int cap;
        int c;
        int ssize;
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrency <= 0) {
            throw new IllegalArgumentException();
        }
        if (concurrency > 65536) {
            concurrency = 65536;
        }
        int sshift = 0;
        for (ssize = 1; ssize < concurrency; ssize <<= 1) {
            ++sshift;
        }
        this.segmentShift = 32 - sshift;
        this.segmentMask = ssize - 1;
        this.segments = new Segment[ssize];
        if (initialCapacity > 0x40000000) {
            initialCapacity = 0x40000000;
        }
        if ((c = initialCapacity / ssize) * ssize < initialCapacity) {
            ++c;
        }
        for (cap = 1; cap < c; cap <<= 1) {
        }
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = this.createSegment(cap, loadFactor);
        }
        this.poolAccessor = poolAccessor;
        this.maxSize = maximumSize;
        this.cacheEventNotificationService = cacheEventNotificationService;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public Element[] getRandomValues(int size, Object keyHint) {
        ArrayList<Element> sampled = new ArrayList<Element>(size * 2);
        int randomHash = this.rndm.nextInt();
        int segmentStart = keyHint == null ? randomHash >>> this.segmentShift & this.segmentMask : SelectableConcurrentHashMap.hash(keyHint.hashCode()) >>> this.segmentShift & this.segmentMask;
        int segmentIndex = segmentStart;
        do {
            int tableStart;
            HashEntry[] table = this.segments[segmentIndex].table;
            int tableIndex = tableStart = randomHash & table.length - 1;
            do {
                HashEntry e = table[tableIndex];
                while (e != null) {
                    Element value = e.value;
                    if (value != null) {
                        sampled.add(value);
                    }
                    e = e.next;
                }
                if (sampled.size() < size) continue;
                return sampled.toArray(new Element[sampled.size()]);
            } while ((tableIndex = tableIndex + 1 & table.length - 1) != tableStart);
        } while ((segmentIndex = segmentIndex + 1 & this.segmentMask) != segmentStart);
        return sampled.toArray(new Element[sampled.size()]);
    }

    public Object storedObject(Element e) {
        return new HashEntry(null, 0, null, e, 0L);
    }

    public int quickSize() {
        Segment[] segments = this.segments;
        long sum = 0L;
        for (Segment seg : segments) {
            sum += (long)seg.count;
        }
        if (sum > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)sum;
    }

    public boolean isEmpty() {
        int i;
        Segment[] segments = this.segments;
        int[] mc = new int[segments.length];
        int mcsum = 0;
        for (i = 0; i < segments.length; ++i) {
            if (segments[i].count != 0) {
                return false;
            }
            mc[i] = segments[i].modCount;
            mcsum += mc[i];
        }
        if (mcsum != 0) {
            for (i = 0; i < segments.length; ++i) {
                if (segments[i].count == 0 && mc[i] == segments[i].modCount) continue;
                return false;
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int size() {
        int i;
        Segment[] segments = this.segments;
        for (int k = 0; k < 2; ++k) {
            int i2;
            int[] mc = new int[segments.length];
            long check = 0L;
            long sum = 0L;
            int mcsum = 0;
            for (i2 = 0; i2 < segments.length; ++i2) {
                sum += (long)segments[i2].count;
                mc[i2] = segments[i2].modCount;
                mcsum += mc[i2];
            }
            if (mcsum != 0) {
                for (i2 = 0; i2 < segments.length; ++i2) {
                    check += (long)segments[i2].count;
                    if (mc[i2] == segments[i2].modCount) continue;
                    check = -1L;
                    break;
                }
            }
            if (check != sum) continue;
            if (sum > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            return (int)sum;
        }
        long sum = 0L;
        for (i = 0; i < segments.length; ++i) {
            segments[i].readLock().lock();
        }
        try {
            for (i = 0; i < segments.length; ++i) {
                sum += (long)segments[i].count;
            }
        }
        finally {
            for (i = 0; i < segments.length; ++i) {
                segments[i].readLock().unlock();
            }
        }
        if (sum > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)sum;
    }

    public ReentrantReadWriteLock lockFor(Object key) {
        int hash = SelectableConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash);
    }

    public ReentrantReadWriteLock[] locks() {
        return this.segments;
    }

    public Element get(Object key) {
        int hash = SelectableConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash).get(key, hash);
    }

    public boolean containsKey(Object key) {
        int hash = SelectableConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash).containsKey(key, hash);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsValue(Object value) {
        int i;
        if (value == null) {
            throw new NullPointerException();
        }
        Segment[] segments = this.segments;
        int[] mc = new int[segments.length];
        for (int k = 0; k < 2; ++k) {
            boolean sum = false;
            int mcsum = 0;
            for (int i2 = 0; i2 < segments.length; ++i2) {
                int c = segments[i2].count;
                mc[i2] = segments[i2].modCount;
                mcsum += mc[i2];
                if (!segments[i2].containsValue(value)) continue;
                return true;
            }
            boolean cleanSweep = true;
            if (mcsum != 0) {
                for (int i3 = 0; i3 < segments.length; ++i3) {
                    int c = segments[i3].count;
                    if (mc[i3] == segments[i3].modCount) continue;
                    cleanSweep = false;
                    break;
                }
            }
            if (!cleanSweep) continue;
            return false;
        }
        for (i = 0; i < segments.length; ++i) {
            segments[i].readLock().lock();
        }
        try {
            for (i = 0; i < segments.length; ++i) {
                if (!segments[i].containsValue(value)) continue;
                boolean bl = true;
                return bl;
            }
        }
        finally {
            for (int i4 = 0; i4 < segments.length; ++i4) {
                segments[i4].readLock().unlock();
            }
        }
        return false;
    }

    public Element put(Object key, Element element, long sizeOf) {
        int hash = SelectableConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash).put(key, hash, element, sizeOf, false, true);
    }

    public Element putIfAbsent(Object key, Element element, long sizeOf) {
        int hash = SelectableConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash).put(key, hash, element, sizeOf, true, true);
    }

    public Element remove(Object key) {
        int hash = SelectableConcurrentHashMap.hash(key.hashCode());
        return this.segmentFor(hash).remove(key, hash, null);
    }

    public boolean remove(Object key, Object value) {
        int hash = SelectableConcurrentHashMap.hash(key.hashCode());
        if (value == null) {
            return false;
        }
        return this.segmentFor(hash).remove(key, hash, value) != null;
    }

    public void clear() {
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i].clear();
        }
    }

    public Set<Object> keySet() {
        KeySet ks = this.keySet;
        return ks != null ? ks : (this.keySet = new KeySet());
    }

    public Collection<Element> values() {
        Values vs = this.values;
        return vs != null ? vs : (this.values = new Values());
    }

    public Set<Map.Entry<Object, Element>> entrySet() {
        EntrySet es = this.entrySet;
        return es != null ? es : (this.entrySet = new EntrySet());
    }

    protected Segment createSegment(int initialCapacity, float lf) {
        return new Segment(initialCapacity, lf);
    }

    public boolean evict() {
        return this.getRandomSegment().evict();
    }

    private Segment getRandomSegment() {
        int randomHash = this.rndm.nextInt();
        return this.segments[randomHash >>> this.segmentShift & this.segmentMask];
    }

    public void recalculateSize(Object key) {
        int hash = SelectableConcurrentHashMap.hash(key.hashCode());
        this.segmentFor(hash).recalculateSize(key, hash);
    }

    protected final Segment segmentFor(int hash) {
        return this.segments[hash >>> this.segmentShift & this.segmentMask];
    }

    protected final List<Segment> segments() {
        return Collections.unmodifiableList(Arrays.asList(this.segments));
    }

    protected static int hash(int h) {
        h += h << 15 ^ 0xFFFFCD7D;
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return h ^ h >>> 16;
    }

    abstract class HashIterator {
        int nextSegmentIndex;
        int nextTableIndex;
        HashEntry[] currentTable;
        HashEntry nextEntry;
        HashEntry lastReturned;

        HashIterator() {
            this.nextSegmentIndex = SelectableConcurrentHashMap.this.segments.length - 1;
            this.nextTableIndex = -1;
            this.advance();
        }

        final void advance() {
            if (this.nextEntry != null && (this.nextEntry = this.nextEntry.next) != null) {
                return;
            }
            while (this.nextTableIndex >= 0) {
                if ((this.nextEntry = this.currentTable[this.nextTableIndex--]) == null) continue;
                return;
            }
            while (this.nextSegmentIndex >= 0) {
                Segment seg = SelectableConcurrentHashMap.this.segments[this.nextSegmentIndex--];
                if (seg.count == 0) continue;
                this.currentTable = seg.table;
                for (int j = this.currentTable.length - 1; j >= 0; --j) {
                    this.nextEntry = this.currentTable[j];
                    if (this.nextEntry == null) continue;
                    this.nextTableIndex = j - 1;
                    return;
                }
            }
        }

        public boolean hasNext() {
            return this.nextEntry != null;
        }

        HashEntry nextEntry() {
            if (this.nextEntry == null) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.nextEntry;
            this.advance();
            return this.lastReturned;
        }

        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            SelectableConcurrentHashMap.this.remove(this.lastReturned.key);
            this.lastReturned = null;
        }
    }

    abstract class HashEntryIterator
    extends HashIterator {
        private HashEntry myNextEntry = this.advanceToNextEntry();

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove is not supported");
        }

        @Override
        public HashEntry nextEntry() {
            if (this.myNextEntry == null) {
                throw new NoSuchElementException();
            }
            HashEntry entry = this.myNextEntry;
            this.myNextEntry = this.advanceToNextEntry();
            return entry;
        }

        @Override
        public boolean hasNext() {
            return this.myNextEntry != null;
        }

        private HashEntry advanceToNextEntry() {
            HashEntry myEntry = null;
            while (super.hasNext() && (myEntry = super.nextEntry()) == null) {
                myEntry = null;
            }
            return myEntry;
        }
    }

    final class EntryIterator
    extends HashEntryIterator
    implements Iterator<Map.Entry<Object, Element>> {
        EntryIterator() {
        }

        @Override
        public Map.Entry<Object, Element> next() {
            HashEntry entry = this.nextEntry();
            final Object key = entry.key;
            final Element value = entry.value;
            return new Map.Entry<Object, Element>(){

                @Override
                public Object getKey() {
                    return key;
                }

                @Override
                public Element getValue() {
                    return value;
                }

                @Override
                public Element setValue(Element value2) {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    final class ValueIterator
    extends HashEntryIterator
    implements Iterator<Element> {
        ValueIterator() {
        }

        @Override
        public Element next() {
            return this.nextEntry().value;
        }
    }

    class KeyIterator
    extends HashEntryIterator
    implements Iterator<Object> {
        KeyIterator() {
        }

        @Override
        public Object next() {
            return this.nextEntry().key;
        }
    }

    final class EntrySet
    extends AbstractSet<Map.Entry<Object, Element>> {
        EntrySet() {
        }

        @Override
        public Iterator<Map.Entry<Object, Element>> iterator() {
            return new EntryIterator();
        }

        @Override
        public int size() {
            return SelectableConcurrentHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return SelectableConcurrentHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Element v = SelectableConcurrentHashMap.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return SelectableConcurrentHashMap.this.remove(e.getKey(), e.getValue());
        }

        @Override
        public void clear() {
            SelectableConcurrentHashMap.this.clear();
        }

        @Override
        public Object[] toArray() {
            ArrayList<Map.Entry<Object, Element>> c = new ArrayList<Map.Entry<Object, Element>>();
            for (Map.Entry<Object, Element> object : this) {
                c.add(object);
            }
            return c.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            ArrayList<Map.Entry<Object, Element>> c = new ArrayList<Map.Entry<Object, Element>>();
            for (Map.Entry<Object, Element> object : this) {
                c.add(object);
            }
            return c.toArray(a);
        }
    }

    final class Values
    extends AbstractCollection<Element> {
        Values() {
        }

        @Override
        public Iterator<Element> iterator() {
            return new ValueIterator();
        }

        @Override
        public int size() {
            return SelectableConcurrentHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return SelectableConcurrentHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return SelectableConcurrentHashMap.this.containsValue(o);
        }

        @Override
        public void clear() {
            SelectableConcurrentHashMap.this.clear();
        }

        @Override
        public Object[] toArray() {
            ArrayList<Element> c = new ArrayList<Element>();
            for (Element object : this) {
                c.add(object);
            }
            return c.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            ArrayList<Element> c = new ArrayList<Element>();
            for (Element object : this) {
                c.add(object);
            }
            return c.toArray(a);
        }
    }

    final class KeySet
    extends AbstractSet<Object> {
        KeySet() {
        }

        @Override
        public Iterator<Object> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return SelectableConcurrentHashMap.this.size();
        }

        @Override
        public boolean isEmpty() {
            return SelectableConcurrentHashMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return SelectableConcurrentHashMap.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return SelectableConcurrentHashMap.this.remove(o) != null;
        }

        @Override
        public void clear() {
            SelectableConcurrentHashMap.this.clear();
        }

        @Override
        public Object[] toArray() {
            ArrayList<Object> c = new ArrayList<Object>();
            for (Object object : this) {
                c.add(object);
            }
            return c.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            ArrayList<Object> c = new ArrayList<Object>();
            for (Object object : this) {
                c.add(object);
            }
            return c.toArray(a);
        }
    }

    static class SegmentIterator
    implements Iterator<HashEntry> {
        int nextTableIndex = -1;
        HashEntry[] currentTable;
        HashEntry nextEntry;
        private final Segment seg;

        private SegmentIterator(Segment memoryStoreSegment) {
            this.seg = memoryStoreSegment;
            this.advance();
        }

        @Override
        public boolean hasNext() {
            return this.nextEntry != null;
        }

        @Override
        public HashEntry next() {
            if (this.nextEntry == null) {
                return null;
            }
            HashEntry lastReturned = this.nextEntry;
            this.advance();
            return lastReturned;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove is not supported");
        }

        final void advance() {
            if (this.nextEntry != null && (this.nextEntry = this.nextEntry.next) != null) {
                return;
            }
            while (this.nextTableIndex >= 0) {
                if ((this.nextEntry = this.currentTable[this.nextTableIndex--]) == null) continue;
                return;
            }
            if (this.seg.count != 0) {
                this.currentTable = this.seg.table;
                for (int j = this.currentTable.length - 1; j >= 0; --j) {
                    this.nextEntry = this.currentTable[j];
                    if (this.nextEntry == null) continue;
                    this.nextTableIndex = j - 1;
                    return;
                }
            }
        }
    }

    public static class HashEntry {
        public final Object key;
        public final int hash;
        public final HashEntry next;
        public volatile Element value;
        public volatile long sizeOf;
        public volatile boolean accessed = true;

        protected HashEntry(Object key, int hash, HashEntry next, Element value, long sizeOf) {
            this.key = key;
            this.hash = hash;
            this.next = next;
            this.value = value;
            this.sizeOf = sizeOf;
        }
    }

    public class Segment
    extends ReentrantReadWriteLock {
        private static final int MAX_EVICTION = 5;
        protected volatile int count;
        int modCount;
        int threshold;
        protected volatile HashEntry[] table;
        final float loadFactor;
        private Iterator<HashEntry> evictionIterator;

        protected Segment(int initialCapacity, float lf) {
            this.loadFactor = lf;
            this.setTable(new HashEntry[initialCapacity]);
        }

        protected void preRemove(HashEntry e) {
        }

        protected void postInstall(Object key, Element value) {
        }

        void setTable(HashEntry[] newTable) {
            this.threshold = (int)((float)newTable.length * this.loadFactor);
            this.table = newTable;
        }

        protected HashEntry getFirst(int hash) {
            HashEntry[] tab = this.table;
            return tab[hash & tab.length - 1];
        }

        private HashEntry removeAndGetFirst(HashEntry e, HashEntry first) {
            this.preRemove(e);
            HashEntry newFirst = e.next;
            HashEntry p = first;
            while (p != e) {
                newFirst = this.relinkHashEntry(p, newFirst);
                p = p.next;
            }
            return newFirst;
        }

        protected HashEntry createHashEntry(Object key, int hash, HashEntry next, Element value, long sizeOf) {
            return new HashEntry(key, hash, next, value, sizeOf);
        }

        protected HashEntry relinkHashEntry(HashEntry e, HashEntry next) {
            return new HashEntry(e.key, e.hash, next, e.value, e.sizeOf);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void clear() {
            ReentrantReadWriteLock.WriteLock writeLock = this.writeLock();
            writeLock.lock();
            try {
                if (this.count != 0) {
                    HashEntry[] tab = this.table;
                    for (int i = 0; i < tab.length; ++i) {
                        tab[i] = null;
                    }
                    ++this.modCount;
                    this.count = 0;
                }
                this.evictionIterator = null;
            }
            finally {
                writeLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        Element remove(Object key, int hash, Object value) {
            ReentrantReadWriteLock.WriteLock writeLock = this.writeLock();
            writeLock.lock();
            try {
                HashEntry first;
                int c = this.count - 1;
                HashEntry[] tab = this.table;
                int index = hash & tab.length - 1;
                HashEntry e = first = tab[index];
                while (!(e == null || e.hash == hash && key.equals(e.key))) {
                    e = e.next;
                }
                Element oldValue = null;
                if (e != null) {
                    Element v = e.value;
                    if (value == null || value.equals(v)) {
                        oldValue = v;
                        ++this.modCount;
                        tab[index] = this.removeAndGetFirst(e, first);
                        this.count = c;
                        if (SelectableConcurrentHashMap.this.cacheEventNotificationService != null) {
                            SelectableConcurrentHashMap.this.cacheEventNotificationService.notifyElementRemovedOrdered(oldValue);
                        }
                        SelectableConcurrentHashMap.this.poolAccessor.delete(e.sizeOf);
                        if (this.evictionIterator != null && ((SegmentIterator)this.evictionIterator).nextEntry == e) {
                            this.evictionIterator.next();
                        }
                    }
                }
                Element element = oldValue;
                return element;
            }
            finally {
                writeLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void recalculateSize(Object key, int hash) {
            HashEntry e;
            Element value = null;
            long oldSize = 0L;
            ReentrantReadWriteLock.ReadLock readLock = this.readLock();
            readLock.lock();
            try {
                HashEntry first;
                HashEntry[] tab = this.table;
                int index = hash & tab.length - 1;
                e = first = tab[index];
                while (!(e == null || e.hash == hash && key.equals(e.key))) {
                    e = e.next;
                }
                if (e != null) {
                    key = e.key;
                    value = e.value;
                    oldSize = e.sizeOf;
                }
            }
            finally {
                readLock.unlock();
            }
            if (value != null) {
                long delta = SelectableConcurrentHashMap.this.poolAccessor.replace(oldSize, key, value, SelectableConcurrentHashMap.this.storedObject(value), true);
                ReentrantReadWriteLock.WriteLock writeLock = this.writeLock();
                writeLock.lock();
                try {
                    e = this.getFirst(hash);
                    while (e != null && key != e.key) {
                        e = e.next;
                    }
                    if (e != null && e.value == value && oldSize == e.sizeOf) {
                        e.sizeOf = oldSize + delta;
                    } else {
                        SelectableConcurrentHashMap.this.poolAccessor.delete(delta);
                    }
                }
                finally {
                    writeLock.unlock();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected Element put(Object key, int hash, Element value, long sizeOf, boolean onlyIfAbsent, boolean fire) {
            Element[] evicted = new Element[5];
            ReentrantReadWriteLock.WriteLock writeLock = this.writeLock();
            writeLock.lock();
            try {
                Element oldValue;
                HashEntry first;
                int c = this.count;
                if (c++ > this.threshold) {
                    this.rehash();
                }
                HashEntry[] tab = this.table;
                int index = hash & tab.length - 1;
                HashEntry e = first = tab[index];
                while (!(e == null || e.hash == hash && key.equals(e.key))) {
                    e = e.next;
                }
                if (e != null) {
                    oldValue = e.value;
                    if (!onlyIfAbsent) {
                        SelectableConcurrentHashMap.this.poolAccessor.delete(e.sizeOf);
                        e.value = value;
                        e.sizeOf = sizeOf;
                        if (SelectableConcurrentHashMap.this.cacheEventNotificationService != null) {
                            SelectableConcurrentHashMap.this.cacheEventNotificationService.notifyElementUpdatedOrdered(oldValue, value);
                        }
                        if (fire) {
                            this.postInstall(key, value);
                        }
                    }
                } else {
                    oldValue = null;
                    ++this.modCount;
                    tab[index] = this.createHashEntry(key, hash, first, value, sizeOf);
                    this.count = c;
                    if (SelectableConcurrentHashMap.this.cacheEventNotificationService != null) {
                        SelectableConcurrentHashMap.this.cacheEventNotificationService.notifyElementPutOrdered(value);
                    }
                    if (fire) {
                        this.postInstall(key, value);
                    }
                }
                if ((onlyIfAbsent && oldValue != null || !onlyIfAbsent) && SelectableConcurrentHashMap.this.maxSize > 0L) {
                    int runs = Math.min(5, SelectableConcurrentHashMap.this.quickSize() - (int)SelectableConcurrentHashMap.this.maxSize);
                    while (runs-- > 0) {
                        SelectableConcurrentHashMap.this.evictionObserver.begin();
                        Element evict = this.nextExpiredOrToEvict(value);
                        if (evict != null) {
                            Element removed;
                            while ((removed = this.remove(evict.getKey(), SelectableConcurrentHashMap.hash(evict.getKey().hashCode()), null)) == null && (evict = this.nextExpiredOrToEvict(value)) != null) {
                            }
                            evicted[runs] = removed;
                        }
                        SelectableConcurrentHashMap.this.evictionObserver.end(CacheOperationOutcomes.EvictionOutcome.SUCCESS);
                    }
                }
                Element element = oldValue;
                return element;
            }
            finally {
                writeLock.unlock();
                for (Element element : evicted) {
                    this.notifyEvictionOrExpiry(element);
                }
            }
        }

        private void notifyEvictionOrExpiry(Element element) {
            if (element != null && SelectableConcurrentHashMap.this.cacheEventNotificationService != null) {
                if (element.isExpired()) {
                    SelectableConcurrentHashMap.this.cacheEventNotificationService.notifyElementExpiry(element, false);
                } else {
                    SelectableConcurrentHashMap.this.cacheEventNotificationService.notifyElementEvicted(element, false);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        Element get(Object key, int hash) {
            ReentrantReadWriteLock.ReadLock readLock = this.readLock();
            readLock.lock();
            try {
                if (this.count != 0) {
                    HashEntry e = this.getFirst(hash);
                    while (e != null) {
                        if (e.hash == hash && key.equals(e.key)) {
                            e.accessed = true;
                            Element element = e.value;
                            return element;
                        }
                        e = e.next;
                    }
                }
                Element element = null;
                return element;
            }
            finally {
                readLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        boolean containsKey(Object key, int hash) {
            ReentrantReadWriteLock.ReadLock readLock = this.readLock();
            readLock.lock();
            try {
                if (this.count != 0) {
                    HashEntry e = this.getFirst(hash);
                    while (e != null) {
                        if (e.hash == hash && key.equals(e.key)) {
                            boolean bl = true;
                            return bl;
                        }
                        e = e.next;
                    }
                }
                boolean bl = false;
                return bl;
            }
            finally {
                readLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        boolean containsValue(Object value) {
            ReentrantReadWriteLock.ReadLock readLock = this.readLock();
            readLock.lock();
            try {
                if (this.count != 0) {
                    for (HashEntry e : this.table) {
                        while (e != null) {
                            Element v = e.value;
                            if (value.equals(v)) {
                                boolean bl = true;
                                return bl;
                            }
                            e = e.next;
                        }
                    }
                }
                boolean bl = false;
                return bl;
            }
            finally {
                readLock.unlock();
            }
        }

        private Element nextExpiredOrToEvict(Element justAdded) {
            Element lastUnpinned = null;
            int i = 0;
            while (i++ < this.count) {
                if (this.evictionIterator == null || !this.evictionIterator.hasNext()) {
                    this.evictionIterator = this.iterator();
                }
                HashEntry next = this.evictionIterator.next();
                if (!next.accessed || next.value.isExpired()) {
                    return next.value;
                }
                if (next.value != justAdded) {
                    lastUnpinned = next.value;
                }
                next.accessed = false;
            }
            return lastUnpinned;
        }

        protected Iterator<HashEntry> iterator() {
            return new SegmentIterator(this);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        boolean evict() {
            Element remove = null;
            ReentrantReadWriteLock.WriteLock writeLock = this.writeLock();
            writeLock.lock();
            try {
                Element evict = this.nextExpiredOrToEvict(null);
                if (evict != null) {
                    if (SelectableConcurrentHashMap.this.cacheEventNotificationService != null) {
                        SelectableConcurrentHashMap.this.evictionObserver.begin();
                        remove = this.remove(evict.getKey(), SelectableConcurrentHashMap.hash(evict.getKey().hashCode()), null);
                        SelectableConcurrentHashMap.this.evictionObserver.end(CacheOperationOutcomes.EvictionOutcome.SUCCESS);
                    } else {
                        remove = this.remove(evict.getKey(), SelectableConcurrentHashMap.hash(evict.getKey().hashCode()), null);
                    }
                }
            }
            finally {
                writeLock.unlock();
            }
            this.notifyEvictionOrExpiry(remove);
            return remove != null;
        }

        void rehash() {
            HashEntry[] oldTable = this.table;
            int oldCapacity = oldTable.length;
            if (oldCapacity >= 0x40000000) {
                return;
            }
            HashEntry[] newTable = new HashEntry[oldCapacity << 1];
            this.threshold = (int)((float)newTable.length * this.loadFactor);
            int sizeMask = newTable.length - 1;
            for (int i = 0; i < oldCapacity; ++i) {
                int k;
                HashEntry e = oldTable[i];
                if (e == null) continue;
                HashEntry next = e.next;
                int idx = e.hash & sizeMask;
                if (next == null) {
                    newTable[idx] = e;
                    continue;
                }
                HashEntry lastRun = e;
                int lastIdx = idx;
                HashEntry last = next;
                while (last != null) {
                    k = last.hash & sizeMask;
                    if (k != lastIdx) {
                        lastIdx = k;
                        lastRun = last;
                    }
                    last = last.next;
                }
                newTable[lastIdx] = lastRun;
                HashEntry p = e;
                while (p != lastRun) {
                    k = p.hash & sizeMask;
                    HashEntry n = newTable[k];
                    newTable[k] = this.relinkHashEntry(p, n);
                    p = p.next;
                }
            }
            this.table = newTable;
            if (this.evictionIterator != null) {
                this.evictionIterator = this.iterator();
            }
        }

        Iterator<HashEntry> getEvictionIterator() {
            return this.evictionIterator;
        }
    }
}

