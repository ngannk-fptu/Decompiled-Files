/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public class OpenHashMap<K, V>
implements Serializable,
Cloneable,
SortedMap<K, V> {
    private static final long serialVersionUID = 0L;
    protected transient Object[] key;
    protected transient Object[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int first = -1;
    protected transient int last = -1;
    protected transient long[] link;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    protected V defRetValue;
    protected transient Iterable<Map.Entry<K, V>> fast;
    protected transient SortedSet<Map.Entry<K, V>> entries;
    protected transient SortedSet<K> keys;
    protected transient Collection<V> values;

    public OpenHashMap(int expected, float f) {
        if (f > 0.0f && f <= 1.0f) {
            if (expected < 0) {
                throw new IllegalArgumentException("The expected number of elements must be nonnegative");
            }
        } else {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
        this.f = f;
        this.n = OpenHashMap.arraySize(expected, f);
        this.mask = this.n - 1;
        this.maxFill = OpenHashMap.maxFill(this.n, f);
        this.key = new Object[this.n + 1];
        this.value = new Object[this.n + 1];
        this.link = new long[this.n + 1];
    }

    public OpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public OpenHashMap() {
        this(16, 0.75f);
    }

    public OpenHashMap(Map<? extends K, ? extends V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public OpenHashMap(Map<? extends K, ? extends V> m) {
        this(m, 0.75f);
    }

    public OpenHashMap(K[] k, V[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public OpenHashMap(K[] k, V[] v) {
        this(k, v, 0.75f);
    }

    public void defaultReturnValue(V rv) {
        this.defRetValue = rv;
    }

    public V defaultReturnValue() {
        return this.defRetValue;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        Map m = (Map)o;
        int n = m.size();
        if (this.size() != n) {
            return false;
        }
        Iterator<Map.Entry<K, V>> i = this.fast().iterator();
        while (n-- > 0) {
            Map.Entry<K, V> e = i.next();
            K k = e.getKey();
            V v = e.getValue();
            Object v2 = m.get(k);
            if (!(v == null ? v2 != null : !v.equals(v2))) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        Iterator<Map.Entry<K, V>> i = this.fast().iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Map.Entry<K, V> e = i.next();
            if (this == e.getKey()) {
                s.append("(this map)");
            } else {
                s.append(String.valueOf(e.getKey()));
            }
            s.append("=>");
            if (this == e.getValue()) {
                s.append("(this map)");
                continue;
            }
            s.append(String.valueOf(e.getValue()));
        }
        s.append("}");
        return s.toString();
    }

    private int realSize() {
        return this.containsNullKey ? this.size - 1 : this.size;
    }

    private void ensureCapacity(int capacity) {
        int needed = OpenHashMap.arraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private void tryCapacity(long capacity) {
        int needed = (int)Math.min(0x40000000L, Math.max(2L, OpenHashMap.nextPowerOfTwo((long)Math.ceil((float)capacity / this.f))));
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private V removeEntry(int pos) {
        Object oldValue = this.value[pos];
        this.value[pos] = null;
        --this.size;
        this.fixPointers(pos);
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return (V)oldValue;
    }

    private V removeNullEntry() {
        this.containsNullKey = false;
        Object oldValue = this.value[this.n];
        this.value[this.n] = null;
        --this.size;
        this.fixPointers(this.n);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return (V)oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        int n = m.size();
        if (m instanceof OpenHashMap) {
            Iterator<Map.Entry<K, V>> i = ((OpenHashMap)m).fast().iterator();
            while (n-- != 0) {
                Map.Entry<K, V> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        } else {
            Iterator<Map.Entry<K, V>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<K, V> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    private int insert(K k, V v) {
        int pos;
        if (k == null) {
            if (this.containsNullKey) {
                return this.n;
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            Object[] key = this.key;
            pos = OpenHashMap.mix(k.hashCode()) & this.mask;
            Object curr = key[pos];
            if (curr != null) {
                if (curr.equals(k)) {
                    return pos;
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
                    if (!curr.equals(k)) continue;
                    return pos;
                }
            }
            key[pos] = k;
        }
        this.value[pos] = v;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n = this.last;
            this.link[n] = this.link[n] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(OpenHashMap.arraySize(this.size + 1, this.f));
        }
        return -1;
    }

    @Override
    public V put(K k, V v) {
        int pos = this.insert(k, v);
        if (pos < 0) {
            return this.defRetValue;
        }
        Object oldValue = this.value[pos];
        this.value[pos] = v;
        return (V)oldValue;
    }

    public V getOrCompute(K k) {
        int pos;
        if (k == null) {
            if (this.containsNullKey) {
                return (V)this.value[this.n];
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            Object[] key = this.key;
            pos = OpenHashMap.mix(k.hashCode()) & this.mask;
            Object curr = key[pos];
            if (curr != null) {
                if (curr.equals(k)) {
                    return (V)this.value[pos];
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
                    if (!curr.equals(k)) continue;
                    return (V)this.value[pos];
                }
            }
            key[pos] = k;
        }
        V v = this.compute(k);
        this.value[pos] = v;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n = this.last;
            this.link[n] = this.link[n] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(OpenHashMap.arraySize(this.size + 1, this.f));
        }
        return v;
    }

    protected V compute(K k) {
        throw new UnsupportedOperationException();
    }

    protected final void shiftKeys(int pos) {
        Object[] key = this.key;
        block0: while (true) {
            Object curr;
            int last = pos;
            pos = pos + 1 & this.mask;
            while ((curr = key[pos]) != null) {
                int slot = OpenHashMap.mix(curr.hashCode()) & this.mask;
                if (!(last <= pos ? last < slot && slot <= pos : last < slot || slot <= pos)) {
                    key[last] = curr;
                    this.value[last] = this.value[pos];
                    this.fixPointers(pos, last);
                    continue block0;
                }
                pos = pos + 1 & this.mask;
            }
            break;
        }
        key[last] = null;
        this.value[last] = null;
    }

    @Override
    public V remove(Object k) {
        if (k == null) {
            return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
        }
        Object[] key = this.key;
        int pos = OpenHashMap.mix(k.hashCode()) & this.mask;
        Object curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (k.equals(curr)) {
            return this.removeEntry(pos);
        }
        while ((curr = key[pos = pos + 1 & this.mask]) != null) {
            if (!k.equals(curr)) continue;
            return this.removeEntry(pos);
        }
        return this.defRetValue;
    }

    public V removeFirst() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int pos = this.first;
        this.first = (int)this.link[pos];
        if (0 <= this.first) {
            int n = this.first;
            this.link[n] = this.link[n] | 0xFFFFFFFF00000000L;
        }
        --this.size;
        Object v = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
            this.value[this.n] = null;
        } else {
            this.shiftKeys(pos);
        }
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return (V)v;
    }

    public V removeLast() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int pos = this.last;
        this.last = (int)(this.link[pos] >>> 32);
        if (0 <= this.last) {
            int n = this.last;
            this.link[n] = this.link[n] | 0xFFFFFFFFL;
        }
        --this.size;
        Object v = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
            this.value[this.n] = null;
        } else {
            this.shiftKeys(pos);
        }
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return (V)v;
    }

    @Override
    public V get(Object k) {
        if (k == null) {
            return (V)(this.containsNullKey ? this.value[this.n] : this.defRetValue);
        }
        Object[] key = this.key;
        int pos = OpenHashMap.mix(k.hashCode()) & this.mask;
        Object curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (k.equals(curr)) {
            return (V)this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!k.equals(curr));
        return (V)this.value[pos];
    }

    @Override
    public boolean containsKey(Object k) {
        if (k == null) {
            return this.containsNullKey;
        }
        Object[] key = this.key;
        int pos = OpenHashMap.mix(k.hashCode()) & this.mask;
        Object curr = key[pos];
        if (curr == null) {
            return false;
        }
        if (k.equals(curr)) {
            return true;
        }
        while ((curr = key[pos = pos + 1 & this.mask]) != null) {
            if (!k.equals(curr)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object v) {
        Object[] value = this.value;
        Object[] key = this.key;
        if (this.containsNullKey && value[this.n] == null && v == null || value[this.n].equals(v)) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if ((key[i] == null || value[i] != null || v != null) && !value[i].equals(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        if (this.size != 0) {
            this.size = 0;
            this.containsNullKey = false;
            Arrays.fill(this.key, null);
            Arrays.fill(this.value, null);
            this.last = -1;
            this.first = -1;
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    protected void fixPointers(int i) {
        if (this.size == 0) {
            this.last = -1;
            this.first = -1;
        } else if (this.first == i) {
            this.first = (int)this.link[i];
            if (0 <= this.first) {
                int n = this.first;
                this.link[n] = this.link[n] | 0xFFFFFFFF00000000L;
            }
        } else if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            if (0 <= this.last) {
                int n = this.last;
                this.link[n] = this.link[n] | 0xFFFFFFFFL;
            }
        } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            int n = prev;
            this.link[n] = this.link[n] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            int n2 = next;
            this.link[n2] = this.link[n2] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
        }
    }

    protected void fixPointers(int s, int d) {
        if (this.size == 1) {
            this.first = this.last = d;
            this.link[d] = -1L;
        } else if (this.first == s) {
            this.first = d;
            int n = (int)this.link[s];
            this.link[n] = this.link[n] ^ (this.link[(int)this.link[s]] ^ ((long)d & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            this.link[d] = this.link[s];
        } else if (this.last == s) {
            this.last = d;
            int n = (int)(this.link[s] >>> 32);
            this.link[n] = this.link[n] ^ (this.link[(int)(this.link[s] >>> 32)] ^ (long)d & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[d] = this.link[s];
        } else {
            long links = this.link[s];
            int prev = (int)(links >>> 32);
            int next = (int)links;
            int n = prev;
            this.link[n] = this.link[n] ^ (this.link[prev] ^ (long)d & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            int n2 = next;
            this.link[n2] = this.link[n2] ^ (this.link[next] ^ ((long)d & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            this.link[d] = links;
        }
    }

    @Override
    public K firstKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return (K)this.key[this.first];
    }

    @Override
    public K lastKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return (K)this.key[this.last];
    }

    @Override
    public Comparator<? super K> comparator() {
        return null;
    }

    @Override
    public SortedMap<K, V> tailMap(K from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedMap<K, V> headMap(K to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedMap<K, V> subMap(K from, K to) {
        throw new UnsupportedOperationException();
    }

    public Iterable<Map.Entry<K, V>> fast() {
        if (this.fast == null) {
            this.fast = new Iterable<Map.Entry<K, V>>(){

                @Override
                public Iterator<Map.Entry<K, V>> iterator() {
                    return new FastEntryIterator();
                }
            };
        }
        return this.fast;
    }

    @Override
    public SortedSet<Map.Entry<K, V>> entrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public SortedSet<K> keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public Collection<V> values() {
        if (this.values == null) {
            this.values = new AbstractObjectCollection<V>(){

                @Override
                public Iterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return OpenHashMap.this.size;
                }

                @Override
                public boolean contains(Object v) {
                    return OpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    OpenHashMap.this.clear();
                }
            };
        }
        return this.values;
    }

    public boolean trim() {
        int l = OpenHashMap.arraySize(this.size, this.f);
        if (l >= this.n) {
            return true;
        }
        try {
            this.rehash(l);
            return true;
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
    }

    public boolean trim(int n) {
        int l = OpenHashMap.nextPowerOfTwo((int)Math.ceil((float)n / this.f));
        if (n <= l) {
            return true;
        }
        try {
            this.rehash(l);
            return true;
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
    }

    protected void rehash(int newN) {
        Object[] key = this.key;
        Object[] value = this.value;
        int mask = newN - 1;
        Object[] newKey = new Object[newN + 1];
        Object[] newValue = new Object[newN + 1];
        int i = this.first;
        int prev = -1;
        int newPrev = -1;
        long[] link = this.link;
        long[] newLink = new long[newN + 1];
        this.first = -1;
        int j = this.size;
        while (j-- != 0) {
            int pos;
            if (key[i] == null) {
                pos = newN;
            } else {
                pos = OpenHashMap.mix(key[i].hashCode()) & mask;
                while (newKey[pos] != null) {
                    pos = pos + 1 & mask;
                }
                newKey[pos] = key[i];
            }
            newValue[pos] = value[i];
            if (prev != -1) {
                int n = newPrev;
                newLink[n] = newLink[n] ^ (newLink[newPrev] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
                int n2 = pos;
                newLink[n2] = newLink[n2] ^ (newLink[pos] ^ ((long)newPrev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
                newPrev = pos;
            } else {
                newPrev = this.first = pos;
                newLink[pos] = -1L;
            }
            int t = i;
            i = (int)link[i];
            prev = t;
        }
        this.link = newLink;
        this.last = newPrev;
        if (newPrev != -1) {
            int n = newPrev;
            newLink[n] = newLink[n] | 0xFFFFFFFFL;
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = OpenHashMap.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }

    public OpenHashMap<K, V> clone() {
        OpenHashMap c;
        try {
            c = (OpenHashMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.fast = null;
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (Object[])this.key.clone();
        c.value = (Object[])this.value.clone();
        c.link = (long[])this.link.clone();
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (this.key[i] == null) {
                ++i;
            }
            if (this != this.key[i]) {
                t = this.key[i].hashCode();
            }
            if (this != this.value[i]) {
                t ^= this.value[i] == null ? 0 : this.value[i].hashCode();
            }
            h += t;
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n] == null ? 0 : this.value[this.n].hashCode();
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        Object[] key = this.key;
        Object[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeObject(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = OpenHashMap.arraySize(this.size, this.f);
        this.maxFill = OpenHashMap.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new Object[this.n + 1];
        Object[] key = this.key;
        this.value = new Object[this.n + 1];
        Object[] value = this.value;
        this.link = new long[this.n + 1];
        long[] link = this.link;
        int prev = -1;
        this.last = -1;
        this.first = -1;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            Object k = s.readObject();
            Object v = s.readObject();
            if (k == null) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = OpenHashMap.mix(k.hashCode()) & this.mask;
                while (key[pos] != null) {
                    pos = pos + 1 & this.mask;
                }
                key[pos] = k;
            }
            value[pos] = v;
            if (this.first != -1) {
                int n = prev;
                link[n] = link[n] ^ (link[prev] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
                int n2 = pos;
                link[n2] = link[n2] ^ (link[pos] ^ ((long)prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
                prev = pos;
                continue;
            }
            prev = this.first = pos;
            int n = pos;
            link[n] = link[n] | 0xFFFFFFFF00000000L;
        }
        this.last = prev;
        if (prev != -1) {
            int n = prev;
            link[n] = link[n] | 0xFFFFFFFFL;
        }
    }

    private static int arraySize(int expected, float f) {
        long s = Math.max(2L, OpenHashMap.nextPowerOfTwo((long)Math.ceil((float)expected / f)));
        if (s > 0x40000000L) {
            throw new IllegalArgumentException("Too large (" + expected + " expected elements with load factor " + f + ")");
        }
        return (int)s;
    }

    private static int maxFill(int n, float f) {
        return Math.min((int)Math.ceil((float)n * f), n - 1);
    }

    private static int nextPowerOfTwo(int x) {
        if (x == 0) {
            return 1;
        }
        --x;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        return (x | x >> 16) + 1;
    }

    private static long nextPowerOfTwo(long x) {
        if (x == 0L) {
            return 1L;
        }
        --x;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return (x | x >> 32) + 1L;
    }

    private static int mix(int x) {
        int h = x * -1640531527;
        return h ^ h >>> 16;
    }

    private static <K> int unwrap(Iterator<? extends K> i, K[] array, int offset, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset >= 0 && offset + max <= array.length) {
            int j = max;
            while (j-- != 0 && i.hasNext()) {
                array[offset++] = i.next();
            }
            return max - j - 1;
        }
        throw new IllegalArgumentException();
    }

    private static <K> int unwrap(Iterator<? extends K> i, K[] array) {
        return OpenHashMap.unwrap(i, array, 0, array.length);
    }

    public static abstract class AbstractObjectCollection<K>
    extends AbstractCollection<K> {
        protected AbstractObjectCollection() {
        }

        @Override
        public Object[] toArray() {
            Object[] a = new Object[this.size()];
            OpenHashMap.unwrap(this.iterator(), a);
            return a;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            if (a.length < this.size()) {
                a = (Object[])Array.newInstance(a.getClass().getComponentType(), this.size());
            }
            OpenHashMap.unwrap(this.iterator(), a);
            return a;
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            boolean retVal = false;
            Iterator<K> i = c.iterator();
            int n = c.size();
            while (n-- != 0) {
                if (!this.add(i.next())) continue;
                retVal = true;
            }
            return retVal;
        }

        @Override
        public boolean add(K k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            int n = c.size();
            Iterator<?> i = c.iterator();
            do {
                if (n-- != 0) continue;
                return true;
            } while (this.contains(i.next()));
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            boolean retVal = false;
            int n = this.size();
            Iterator i = this.iterator();
            while (n-- != 0) {
                if (c.contains(i.next())) continue;
                i.remove();
                retVal = true;
            }
            return retVal;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean retVal = false;
            int n = c.size();
            Iterator<?> i = c.iterator();
            while (n-- != 0) {
                if (!this.remove(i.next())) continue;
                retVal = true;
            }
            return retVal;
        }

        @Override
        public boolean isEmpty() {
            return this.size() == 0;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            Iterator i = this.iterator();
            int n = this.size();
            boolean first = true;
            s.append("{");
            while (n-- != 0) {
                if (first) {
                    first = false;
                } else {
                    s.append(", ");
                }
                Object k = i.next();
                if (this == k) {
                    s.append("(this collection)");
                    continue;
                }
                s.append(String.valueOf(k));
            }
            s.append("}");
            return s.toString();
        }
    }

    final class MapEntry
    implements Map.Entry<K, V> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return OpenHashMap.this.key[this.index];
        }

        @Override
        public V getValue() {
            return OpenHashMap.this.value[this.index];
        }

        @Override
        public V setValue(V v) {
            Object oldValue = OpenHashMap.this.value[this.index];
            OpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (OpenHashMap.this.key[this.index] == null ? e.getKey() != null : !OpenHashMap.this.key[this.index].equals(e.getKey())) {
                return false;
            }
            return !(OpenHashMap.this.value[this.index] == null ? e.getValue() != null : !OpenHashMap.this.value[this.index].equals(e.getValue()));
        }

        @Override
        public int hashCode() {
            return (OpenHashMap.this.key[this.index] == null ? 0 : OpenHashMap.this.key[this.index].hashCode()) ^ (OpenHashMap.this.value[this.index] == null ? 0 : OpenHashMap.this.value[this.index].hashCode());
        }

        public String toString() {
            return OpenHashMap.this.key[this.index] + "=>" + OpenHashMap.this.value[this.index];
        }
    }

    private class MapIterator {
        int prev = -1;
        int next = -1;
        int curr = -1;
        int index = -1;

        private MapIterator() {
            this.next = OpenHashMap.this.first;
            this.index = 0;
        }

        public boolean hasNext() {
            return this.next != -1;
        }

        private void ensureIndexKnown() {
            if (this.index < 0) {
                if (this.prev == -1) {
                    this.index = 0;
                } else if (this.next == -1) {
                    this.index = OpenHashMap.this.size;
                } else {
                    int pos = OpenHashMap.this.first;
                    this.index = 1;
                    while (pos != this.prev) {
                        pos = (int)OpenHashMap.this.link[pos];
                        ++this.index;
                    }
                }
            }
        }

        public int nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = this.next;
            this.next = (int)OpenHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
                ++this.index;
            }
            return this.curr;
        }

        public void remove() {
            this.ensureIndexKnown();
            if (this.curr == -1) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
                this.prev = (int)(OpenHashMap.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)OpenHashMap.this.link[this.curr];
            }
            --OpenHashMap.this.size;
            if (this.prev == -1) {
                OpenHashMap.this.first = this.next;
            } else {
                int n = this.prev;
                OpenHashMap.this.link[n] = OpenHashMap.this.link[n] ^ (OpenHashMap.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                OpenHashMap.this.last = this.prev;
            } else {
                int n = this.next;
                OpenHashMap.this.link[n] = OpenHashMap.this.link[n] ^ (OpenHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != OpenHashMap.this.n) {
                Object[] key = OpenHashMap.this.key;
                while (true) {
                    Object curr;
                    int last = pos;
                    pos = last + 1 & OpenHashMap.this.mask;
                    while (true) {
                        if ((curr = key[pos]) == null) {
                            key[last] = null;
                            OpenHashMap.this.value[last] = null;
                            return;
                        }
                        int slot = OpenHashMap.mix(curr.hashCode()) & OpenHashMap.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & OpenHashMap.this.mask;
                    }
                    key[last] = curr;
                    OpenHashMap.this.value[last] = OpenHashMap.this.value[pos];
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    OpenHashMap.this.fixPointers(pos, last);
                }
            }
            OpenHashMap.this.containsNullKey = false;
            OpenHashMap.this.value[OpenHashMap.this.n] = null;
        }
    }

    public static abstract class AbstractObjectSet<K>
    extends AbstractObjectCollection<K>
    implements Cloneable {
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Set)) {
                return false;
            }
            Set s = (Set)o;
            return s.size() == this.size() && this.containsAll(s);
        }

        @Override
        public int hashCode() {
            int h = 0;
            int n = this.size();
            Iterator i = this.iterator();
            while (n-- != 0) {
                Object k = i.next();
                h += k == null ? 0 : k.hashCode();
            }
            return h;
        }
    }

    private class EntryIterator
    extends MapIterator
    implements Iterator<Map.Entry<K, V>> {
        private MapEntry entry;

        @Override
        public MapEntry next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements Iterator<Map.Entry<K, V>> {
        final MapEntry entry;

        public FastEntryIterator() {
            this.entry = new MapEntry();
        }

        @Override
        public MapEntry next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Map.Entry<K, V>>
    implements SortedSet<Map.Entry<K, V>> {
        private MapEntrySet() {
        }

        public EntryIterator iterator() {
            return new EntryIterator();
        }

        @Override
        public Comparator<? super Map.Entry<K, V>> comparator() {
            return null;
        }

        @Override
        public SortedSet<Map.Entry<K, V>> subSet(Map.Entry<K, V> fromElement, Map.Entry<K, V> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SortedSet<Map.Entry<K, V>> headSet(Map.Entry<K, V> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SortedSet<Map.Entry<K, V>> tailSet(Map.Entry<K, V> fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map.Entry<K, V> first() {
            if (OpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(OpenHashMap.this.first);
        }

        @Override
        public Map.Entry<K, V> last() {
            if (OpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(OpenHashMap.this.last);
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            if (k == null) {
                if (OpenHashMap.this.containsNullKey) {
                    return !(OpenHashMap.this.value[OpenHashMap.this.n] == null ? e.getValue() != null : !OpenHashMap.this.value[OpenHashMap.this.n].equals(e.getValue()));
                }
                return false;
            }
            Object[] key = OpenHashMap.this.key;
            int pos = OpenHashMap.mix(k.hashCode()) & OpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (k.equals(curr)) {
                return OpenHashMap.this.value[pos] == null ? e.getValue() == null : OpenHashMap.this.value[pos].equals(e.getValue());
            }
            while ((curr = key[pos = pos + 1 & OpenHashMap.this.mask]) != null) {
                if (!k.equals(curr)) continue;
                return OpenHashMap.this.value[pos] == null ? e.getValue() == null : OpenHashMap.this.value[pos].equals(e.getValue());
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Object k = e.getKey();
            Object v = e.getValue();
            if (k == null) {
                if (OpenHashMap.this.containsNullKey) {
                    if (OpenHashMap.this.value[OpenHashMap.this.n] == null ? v != null : !OpenHashMap.this.value[OpenHashMap.this.n].equals(v)) {
                        return false;
                    }
                    OpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            Object[] key = OpenHashMap.this.key;
            int pos = OpenHashMap.mix(k.hashCode()) & OpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (curr.equals(k)) {
                if (OpenHashMap.this.value[pos] == null ? v != null : !OpenHashMap.this.value[pos].equals(v)) {
                    return false;
                }
                OpenHashMap.this.removeEntry(pos);
                return true;
            }
            do {
                if ((curr = key[pos = pos + 1 & OpenHashMap.this.mask]) != null) continue;
                return false;
            } while (!curr.equals(k) || !(OpenHashMap.this.value[pos] == null ? v == null : OpenHashMap.this.value[pos].equals(v)));
            OpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return OpenHashMap.this.size;
        }

        @Override
        public void clear() {
            OpenHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements Iterator<K> {
        @Override
        public K next() {
            return OpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractObjectSet<K>
    implements SortedSet<K> {
        private KeySet() {
        }

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return OpenHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return OpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = OpenHashMap.this.size;
            OpenHashMap.this.remove(k);
            return OpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            OpenHashMap.this.clear();
        }

        @Override
        public K first() {
            if (OpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return OpenHashMap.this.key[OpenHashMap.this.first];
        }

        @Override
        public K last() {
            if (OpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return OpenHashMap.this.key[OpenHashMap.this.last];
        }

        @Override
        public Comparator<? super K> comparator() {
            return null;
        }

        @Override
        public final SortedSet<K> tailSet(K from) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final SortedSet<K> headSet(K to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final SortedSet<K> subSet(K from, K to) {
            throw new UnsupportedOperationException();
        }
    }

    private final class ValueIterator
    extends MapIterator
    implements Iterator<V> {
        @Override
        public V next() {
            return OpenHashMap.this.value[this.nextEntry()];
        }
    }
}

