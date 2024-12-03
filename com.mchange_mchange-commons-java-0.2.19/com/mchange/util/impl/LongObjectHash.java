/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.LongObjectMap;
import com.mchange.util.impl.LOHRecord;

public class LongObjectHash
implements LongObjectMap {
    LOHRecord[] records;
    float load_factor;
    long threshold;
    long size;

    public LongObjectHash(int n, float f) {
        this.records = new LOHRecord[n];
        this.load_factor = f;
        this.threshold = (long)(f * (float)n);
    }

    public LongObjectHash() {
        this(101, 0.75f);
    }

    @Override
    public synchronized Object get(long l) {
        int n = (int)(l % (long)this.records.length);
        Object object = null;
        if (this.records[n] != null) {
            object = this.records[n].get(l);
        }
        return object;
    }

    @Override
    public synchronized void put(long l, Object object) {
        boolean bl;
        int n = (int)(l % (long)this.records.length);
        if (this.records[n] == null) {
            this.records[n] = new LOHRecord(n);
        }
        if (!(bl = this.records[n].add(l, object, true))) {
            ++this.size;
        }
        if (this.size > this.threshold) {
            this.rehash();
        }
    }

    @Override
    public synchronized boolean putNoReplace(long l, Object object) {
        boolean bl;
        int n = (int)(l % (long)this.records.length);
        if (this.records[n] == null) {
            this.records[n] = new LOHRecord(n);
        }
        if (bl = this.records[n].add(l, object, false)) {
            return false;
        }
        ++this.size;
        if (this.size > this.threshold) {
            this.rehash();
        }
        return true;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public synchronized boolean containsLong(long l) {
        int n = (int)(l % (long)this.records.length);
        return this.records[n] != null && this.records[n].findLong(l) != null;
    }

    @Override
    public synchronized Object remove(long l) {
        Object object;
        LOHRecord lOHRecord = this.records[(int)(l % (long)this.records.length)];
        Object object2 = object = lOHRecord == null ? null : lOHRecord.remove(l);
        if (object != null) {
            --this.size;
        }
        return object;
    }

    protected void rehash() {
        if ((long)this.records.length * 2L > Integer.MAX_VALUE) {
            throw new Error("Implementation of LongObjectHash allows a capacity of only 2147483647");
        }
        LOHRecord[] lOHRecordArray = new LOHRecord[this.records.length * 2];
        for (int i = 0; i < this.records.length; ++i) {
            if (this.records[i] == null) continue;
            lOHRecordArray[i] = this.records[i];
            lOHRecordArray[i * 2] = this.records[i].split(lOHRecordArray.length);
        }
        this.records = lOHRecordArray;
        this.threshold = (long)(this.load_factor * (float)this.records.length);
    }
}

