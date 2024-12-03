/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.PrefixedNameSet;
import com.ctc.wstx.util.PrefixedName;
import java.util.Iterator;
import java.util.TreeSet;

public final class LargePrefixedNameSet
extends PrefixedNameSet {
    static final int MIN_HASH_AREA = 8;
    final boolean mNsAware;
    final PrefixedName[] mNames;
    final Bucket[] mBuckets;

    public LargePrefixedNameSet(boolean nsAware, PrefixedName[] names) {
        int tableSize;
        this.mNsAware = nsAware;
        int len = names.length;
        int minSize = len + (len + 7 >> 3);
        for (tableSize = 8; tableSize < minSize; tableSize += tableSize) {
        }
        this.mNames = new PrefixedName[tableSize];
        Bucket[] buckets = null;
        int mask = tableSize - 1;
        for (int i = 0; i < len; ++i) {
            Bucket old;
            PrefixedName nk = names[i];
            int ix = nk.hashCode() & mask;
            if (this.mNames[ix] == null) {
                this.mNames[ix] = nk;
                continue;
            }
            ix >>= 2;
            if (buckets == null) {
                buckets = new Bucket[tableSize >> 2];
                old = null;
            } else {
                old = buckets[ix];
            }
            buckets[ix] = new Bucket(nk, old);
        }
        this.mBuckets = buckets;
    }

    @Override
    public boolean hasMultiple() {
        return true;
    }

    @Override
    public boolean contains(PrefixedName name) {
        PrefixedName[] hashArea = this.mNames;
        int index = name.hashCode() & hashArea.length - 1;
        PrefixedName res = hashArea[index];
        if (res != null && res.equals(name)) {
            return true;
        }
        Bucket[] buckets = this.mBuckets;
        if (buckets != null) {
            for (Bucket bucket = buckets[index >> 2]; bucket != null; bucket = bucket.getNext()) {
                res = bucket.getName();
                if (!res.equals(name)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendNames(StringBuilder sb, String sep) {
        int i;
        TreeSet<PrefixedName> ts = new TreeSet<PrefixedName>();
        for (i = 0; i < this.mNames.length; ++i) {
            PrefixedName name = this.mNames[i];
            if (name == null) continue;
            ts.add(name);
        }
        if (this.mBuckets != null) {
            for (i = 0; i < this.mNames.length >> 2; ++i) {
                for (Bucket b = this.mBuckets[i]; b != null; b = b.getNext()) {
                    ts.add(b.getName());
                }
            }
        }
        Iterator it = ts.iterator();
        boolean first = true;
        while (it.hasNext()) {
            if (first) {
                first = false;
            } else {
                sb.append(sep);
            }
            sb.append(((PrefixedName)it.next()).toString());
        }
    }

    private static final class Bucket {
        final PrefixedName mName;
        final Bucket mNext;

        public Bucket(PrefixedName name, Bucket next) {
            this.mName = name;
            this.mNext = next;
        }

        public PrefixedName getName() {
            return this.mName;
        }

        public Bucket getNext() {
            return this.mNext;
        }
    }
}

