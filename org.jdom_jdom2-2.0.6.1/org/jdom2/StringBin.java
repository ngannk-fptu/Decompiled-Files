/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import org.jdom2.internal.ArrayCopy;

final class StringBin {
    private static final int GROW = 4;
    private static final int DEFAULTCAP = 1023;
    private static final int MAXBUCKET = 64;
    private String[][] buckets;
    private int[] lengths;
    private int mask = 0;

    public StringBin() {
        this(1023);
    }

    public StringBin(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Can not have a negative capacity");
        }
        if (--capacity < 1023) {
            capacity = 1023;
        }
        capacity /= 3;
        int shift = 0;
        while (capacity != 0) {
            capacity >>>= 1;
            ++shift;
        }
        this.mask = (1 << shift) - 1;
        this.buckets = new String[this.mask + 1][];
        this.lengths = new int[this.buckets.length];
    }

    private final int locate(int hash, String value, String[] bucket, int length) {
        int left = 0;
        int right = length - 1;
        int mid = 0;
        while (left <= right) {
            mid = left + right >>> 1;
            if (bucket[mid].hashCode() > hash) {
                right = mid - 1;
                continue;
            }
            if (bucket[mid].hashCode() < hash) {
                left = mid + 1;
                continue;
            }
            int cmp = value.compareTo(bucket[mid]);
            if (cmp == 0) {
                return mid;
            }
            if (cmp < 0) {
                while (--mid >= left && bucket[mid].hashCode() == hash) {
                    cmp = value.compareTo(bucket[mid]);
                    if (cmp == 0) {
                        return mid;
                    }
                    if (cmp <= 0) continue;
                    return -(mid + 1) - 1;
                }
                return -(mid + 1) - 1;
            }
            while (++mid <= right && bucket[mid].hashCode() == hash) {
                cmp = value.compareTo(bucket[mid]);
                if (cmp == 0) {
                    return mid;
                }
                if (cmp >= 0) continue;
                return -mid - 1;
            }
            return -mid - 1;
        }
        return -left - 1;
    }

    public String reuse(String value) {
        String v;
        if (value == null) {
            return null;
        }
        int hash = value.hashCode();
        int bucketid = (hash >>> 16 ^ hash) & this.mask;
        int length = this.lengths[bucketid];
        if (length == 0) {
            String v2 = StringBin.compact(value);
            this.buckets[bucketid] = new String[4];
            this.buckets[bucketid][0] = v2;
            this.lengths[bucketid] = 1;
            return v2;
        }
        String[] bucket = this.buckets[bucketid];
        int ip = -this.locate(hash, value, bucket, length) - 1;
        if (ip < 0) {
            return bucket[-ip - 1];
        }
        if (length >= 64) {
            this.rehash();
            return this.reuse(value);
        }
        if (length == bucket.length) {
            bucket = ArrayCopy.copyOf(bucket, length + 4);
            this.buckets[bucketid] = bucket;
        }
        System.arraycopy(bucket, ip, bucket, ip + 1, length - ip);
        bucket[ip] = v = StringBin.compact(value);
        int n = bucketid;
        this.lengths[n] = this.lengths[n] + 1;
        return v;
    }

    private void rehash() {
        String[][] olddata = this.buckets;
        this.mask = (this.mask + 1 << 2) - 1;
        this.buckets = new String[this.mask + 1][];
        this.lengths = new int[this.buckets.length];
        int hash = 0;
        int bucketid = 0;
        int length = 0;
        for (String[] ob : olddata) {
            if (ob == null) continue;
            for (String val : ob) {
                if (val == null) break;
                hash = val.hashCode();
                bucketid = (hash >>> 16 ^ hash) & this.mask;
                length = this.lengths[bucketid];
                if (length == 0) {
                    this.buckets[bucketid] = new String[(ob.length + 4) / 4];
                    this.buckets[bucketid][0] = val;
                } else {
                    if (this.buckets[bucketid].length == length) {
                        this.buckets[bucketid] = ArrayCopy.copyOf(this.buckets[bucketid], this.lengths[bucketid] + 4);
                    }
                    this.buckets[bucketid][length] = val;
                }
                int n = bucketid;
                this.lengths[n] = this.lengths[n] + 1;
            }
        }
    }

    private static final String compact(String input) {
        return new String(input.toCharArray());
    }

    public int size() {
        int sum = 0;
        for (int l : this.lengths) {
            sum += l;
        }
        return sum;
    }
}

