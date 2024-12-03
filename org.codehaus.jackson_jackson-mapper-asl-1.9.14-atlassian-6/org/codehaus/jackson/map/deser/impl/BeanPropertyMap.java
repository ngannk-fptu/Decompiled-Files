/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.codehaus.jackson.map.deser.SettableBeanProperty;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class BeanPropertyMap {
    private final Bucket[] _buckets;
    private final int _hashMask;
    private final int _size;

    public BeanPropertyMap(Collection<SettableBeanProperty> properties) {
        this._size = properties.size();
        int bucketCount = BeanPropertyMap.findSize(this._size);
        this._hashMask = bucketCount - 1;
        Bucket[] buckets = new Bucket[bucketCount];
        for (SettableBeanProperty property : properties) {
            String key = property.getName();
            int index = key.hashCode() & this._hashMask;
            buckets[index] = new Bucket(buckets[index], key, property);
        }
        this._buckets = buckets;
    }

    public void assignIndexes() {
        int index = 0;
        for (Bucket bucket : this._buckets) {
            while (bucket != null) {
                bucket.value.assignIndex(index++);
                bucket = bucket.next;
            }
        }
    }

    private static final int findSize(int size) {
        int result;
        int needed = size <= 32 ? size + size : size + (size >> 2);
        for (result = 2; result < needed; result += result) {
        }
        return result;
    }

    public int size() {
        return this._size;
    }

    public Iterator<SettableBeanProperty> allProperties() {
        return new IteratorImpl(this._buckets);
    }

    public SettableBeanProperty find(String key) {
        int index = key.hashCode() & this._hashMask;
        Bucket bucket = this._buckets[index];
        if (bucket == null) {
            return null;
        }
        if (bucket.key == key) {
            return bucket.value;
        }
        while ((bucket = bucket.next) != null) {
            if (bucket.key != key) continue;
            return bucket.value;
        }
        return this._findWithEquals(key, index);
    }

    public void replace(SettableBeanProperty property) {
        String name = property.getName();
        int index = name.hashCode() & this._buckets.length - 1;
        Bucket tail = null;
        boolean found = false;
        Bucket bucket = this._buckets[index];
        while (bucket != null) {
            if (!found && bucket.key.equals(name)) {
                found = true;
            } else {
                tail = new Bucket(tail, bucket.key, bucket.value);
            }
            bucket = bucket.next;
        }
        if (!found) {
            throw new NoSuchElementException("No entry '" + property + "' found, can't replace");
        }
        this._buckets[index] = new Bucket(tail, name, property);
    }

    public void remove(SettableBeanProperty property) {
        String name = property.getName();
        int index = name.hashCode() & this._buckets.length - 1;
        Bucket tail = null;
        boolean found = false;
        Bucket bucket = this._buckets[index];
        while (bucket != null) {
            if (!found && bucket.key.equals(name)) {
                found = true;
            } else {
                tail = new Bucket(tail, bucket.key, bucket.value);
            }
            bucket = bucket.next;
        }
        if (!found) {
            throw new NoSuchElementException("No entry '" + property + "' found, can't remove");
        }
        this._buckets[index] = tail;
    }

    private SettableBeanProperty _findWithEquals(String key, int index) {
        Bucket bucket = this._buckets[index];
        while (bucket != null) {
            if (key.equals(bucket.key)) {
                return bucket.value;
            }
            bucket = bucket.next;
        }
        return null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class IteratorImpl
    implements Iterator<SettableBeanProperty> {
        private final Bucket[] _buckets;
        private Bucket _currentBucket;
        private int _nextBucketIndex;

        public IteratorImpl(Bucket[] buckets) {
            this._buckets = buckets;
            int i = 0;
            int len = this._buckets.length;
            while (i < len) {
                Bucket b;
                if ((b = this._buckets[i++]) == null) continue;
                this._currentBucket = b;
                break;
            }
            this._nextBucketIndex = i;
        }

        @Override
        public boolean hasNext() {
            return this._currentBucket != null;
        }

        @Override
        public SettableBeanProperty next() {
            Bucket curr = this._currentBucket;
            if (curr == null) {
                throw new NoSuchElementException();
            }
            Bucket b = curr.next;
            while (b == null && this._nextBucketIndex < this._buckets.length) {
                b = this._buckets[this._nextBucketIndex++];
            }
            this._currentBucket = b;
            return curr.value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class Bucket {
        public final Bucket next;
        public final String key;
        public final SettableBeanProperty value;

        public Bucket(Bucket next, String key, SettableBeanProperty value) {
            this.next = next;
            this.key = key;
            this.value = value;
        }
    }
}

