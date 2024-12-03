/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.IntEnumeration;
import com.mchange.util.IntObjectMap;
import com.mchange.util.impl.IOHRecElem;
import com.mchange.util.impl.IOHRecord;
import com.mchange.util.impl.IntEnumerationHelperBase;
import java.util.NoSuchElementException;

public class IntObjectHash
implements IntObjectMap {
    IOHRecord[] records;
    int init_capacity;
    float load_factor;
    int threshold;
    int size;

    public IntObjectHash(int n, float f) {
        this.init_capacity = n;
        this.load_factor = f;
        this.clear();
    }

    public IntObjectHash() {
        this(101, 0.75f);
    }

    @Override
    public synchronized Object get(int n) {
        int n2 = this.getIndex(n);
        Object object = null;
        if (this.records[n2] != null) {
            object = this.records[n2].get(n);
        }
        return object;
    }

    @Override
    public synchronized void put(int n, Object object) {
        boolean bl;
        if (object == null) {
            throw new NullPointerException("Null values not permitted.");
        }
        int n2 = this.getIndex(n);
        if (this.records[n2] == null) {
            this.records[n2] = new IOHRecord(n2);
        }
        if (!(bl = this.records[n2].add(n, object, true))) {
            ++this.size;
        }
        if (this.size > this.threshold) {
            this.rehash();
        }
    }

    @Override
    public synchronized boolean putNoReplace(int n, Object object) {
        boolean bl;
        if (object == null) {
            throw new NullPointerException("Null values not permitted.");
        }
        int n2 = this.getIndex(n);
        if (this.records[n2] == null) {
            this.records[n2] = new IOHRecord(n2);
        }
        if (bl = this.records[n2].add(n, object, false)) {
            return false;
        }
        ++this.size;
        if (this.size > this.threshold) {
            this.rehash();
        }
        return true;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public synchronized boolean containsInt(int n) {
        int n2 = this.getIndex(n);
        return this.records[n2] != null && this.records[n2].findInt(n) != null;
    }

    private int getIndex(int n) {
        return Math.abs(n % this.records.length);
    }

    @Override
    public synchronized Object remove(int n) {
        Object object;
        IOHRecord iOHRecord = this.records[this.getIndex(n)];
        Object object2 = object = iOHRecord == null ? null : iOHRecord.remove(n);
        if (object != null) {
            --this.size;
        }
        return object;
    }

    @Override
    public synchronized void clear() {
        this.records = new IOHRecord[this.init_capacity];
        this.threshold = (int)(this.load_factor * (float)this.init_capacity);
        this.size = 0;
    }

    @Override
    public synchronized IntEnumeration ints() {
        return new IntEnumerationHelperBase(){
            int index = -1;
            IOHRecElem finger;
            {
                this.nextIndex();
            }

            @Override
            public boolean hasMoreInts() {
                return this.index < IntObjectHash.this.records.length;
            }

            @Override
            public int nextInt() {
                try {
                    int n = this.finger.num;
                    this.findNext();
                    return n;
                }
                catch (NullPointerException nullPointerException) {
                    throw new NoSuchElementException();
                }
            }

            private void findNext() {
                if (this.finger.next != null) {
                    this.finger = this.finger.next;
                } else {
                    this.nextIndex();
                }
            }

            private void nextIndex() {
                try {
                    int n = IntObjectHash.this.records.length;
                    do {
                        ++this.index;
                    } while (IntObjectHash.this.records[this.index] == null && this.index <= n);
                    this.finger = IntObjectHash.this.records[this.index].next;
                }
                catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                    this.finger = null;
                }
            }
        };
    }

    protected void rehash() {
        IOHRecord[] iOHRecordArray = new IOHRecord[this.records.length * 2];
        for (int i = 0; i < this.records.length; ++i) {
            if (this.records[i] == null) continue;
            iOHRecordArray[i] = this.records[i];
            iOHRecordArray[i * 2] = this.records[i].split(iOHRecordArray.length);
        }
        this.records = iOHRecordArray;
        this.threshold = (int)(this.load_factor * (float)this.records.length);
    }
}

