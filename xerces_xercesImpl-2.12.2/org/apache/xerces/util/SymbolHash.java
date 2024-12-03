/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import org.apache.xerces.util.PrimeNumberSequenceGenerator;

public class SymbolHash {
    protected static final int TABLE_SIZE = 101;
    protected static final int MAX_HASH_COLLISIONS = 40;
    protected static final int MULTIPLIERS_SIZE = 32;
    protected static final int MULTIPLIERS_MASK = 31;
    protected int fTableSize;
    protected Entry[] fBuckets;
    protected int fNum = 0;
    protected int[] fHashMultipliers;

    public SymbolHash() {
        this(101);
    }

    public SymbolHash(int n) {
        this.fTableSize = n;
        this.fBuckets = new Entry[this.fTableSize];
    }

    public void put(Object object, Object object2) {
        int n = 0;
        int n2 = this.hash(object);
        int n3 = n2 % this.fTableSize;
        Entry entry = this.fBuckets[n3];
        while (entry != null) {
            if (object.equals(entry.key)) {
                entry.value = object2;
                return;
            }
            ++n;
            entry = entry.next;
        }
        if (this.fNum >= this.fTableSize) {
            this.rehash();
            n3 = n2 % this.fTableSize;
        } else if (n >= 40 && object instanceof String) {
            this.rebalance();
            n3 = this.hash(object) % this.fTableSize;
        }
        this.fBuckets[n3] = entry = new Entry(object, object2, this.fBuckets[n3]);
        ++this.fNum;
    }

    public Object get(Object object) {
        int n = this.hash(object) % this.fTableSize;
        Entry entry = this.search(object, n);
        if (entry != null) {
            return entry.value;
        }
        return null;
    }

    public int getLength() {
        return this.fNum;
    }

    public int getValues(Object[] objectArray, int n) {
        int n2 = 0;
        for (int i = 0; i < this.fTableSize && n2 < this.fNum; ++i) {
            Entry entry = this.fBuckets[i];
            while (entry != null) {
                objectArray[n + n2] = entry.value;
                ++n2;
                entry = entry.next;
            }
        }
        return this.fNum;
    }

    public Object[] getEntries() {
        Object[] objectArray = new Object[this.fNum << 1];
        int n = 0;
        for (int i = 0; i < this.fTableSize && n < this.fNum << 1; ++i) {
            Entry entry = this.fBuckets[i];
            while (entry != null) {
                objectArray[n] = entry.key;
                objectArray[++n] = entry.value;
                ++n;
                entry = entry.next;
            }
        }
        return objectArray;
    }

    public SymbolHash makeClone() {
        SymbolHash symbolHash = new SymbolHash(this.fTableSize);
        symbolHash.fNum = this.fNum;
        symbolHash.fHashMultipliers = this.fHashMultipliers != null ? (int[])this.fHashMultipliers.clone() : null;
        for (int i = 0; i < this.fTableSize; ++i) {
            if (this.fBuckets[i] == null) continue;
            symbolHash.fBuckets[i] = this.fBuckets[i].makeClone();
        }
        return symbolHash;
    }

    public void clear() {
        for (int i = 0; i < this.fTableSize; ++i) {
            this.fBuckets[i] = null;
        }
        this.fNum = 0;
        this.fHashMultipliers = null;
    }

    protected Entry search(Object object, int n) {
        Entry entry = this.fBuckets[n];
        while (entry != null) {
            if (object.equals(entry.key)) {
                return entry;
            }
            entry = entry.next;
        }
        return null;
    }

    protected int hash(Object object) {
        if (this.fHashMultipliers == null || !(object instanceof String)) {
            return object.hashCode() & Integer.MAX_VALUE;
        }
        return this.hash0((String)object);
    }

    private int hash0(String string) {
        int n = 0;
        int n2 = string.length();
        int[] nArray = this.fHashMultipliers;
        for (int i = 0; i < n2; ++i) {
            n = n * nArray[i & 0x1F] + string.charAt(i);
        }
        return n & Integer.MAX_VALUE;
    }

    protected void rehash() {
        this.rehashCommon((this.fBuckets.length << 1) + 1);
    }

    protected void rebalance() {
        if (this.fHashMultipliers == null) {
            this.fHashMultipliers = new int[32];
        }
        PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
        this.rehashCommon(this.fBuckets.length);
    }

    private void rehashCommon(int n) {
        int n2 = this.fBuckets.length;
        Entry[] entryArray = this.fBuckets;
        Entry[] entryArray2 = new Entry[n];
        this.fBuckets = entryArray2;
        this.fTableSize = this.fBuckets.length;
        int n3 = n2;
        while (n3-- > 0) {
            Entry entry = entryArray[n3];
            while (entry != null) {
                Entry entry2 = entry;
                entry = entry.next;
                int n4 = this.hash(entry2.key) % n;
                entry2.next = entryArray2[n4];
                entryArray2[n4] = entry2;
            }
        }
    }

    protected static final class Entry {
        public Object key;
        public Object value;
        public Entry next;

        public Entry() {
            this.key = null;
            this.value = null;
            this.next = null;
        }

        public Entry(Object object, Object object2, Entry entry) {
            this.key = object;
            this.value = object2;
            this.next = entry;
        }

        public Entry makeClone() {
            Entry entry = new Entry();
            entry.key = this.key;
            entry.value = this.value;
            if (this.next != null) {
                entry.next = this.next.makeClone();
            }
            return entry;
        }
    }
}

