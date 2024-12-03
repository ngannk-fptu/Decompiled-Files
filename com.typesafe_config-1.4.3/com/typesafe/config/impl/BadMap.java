/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

final class BadMap<K, V> {
    private final int size;
    private final Entry[] entries;
    private static final Entry[] emptyEntries = new Entry[0];
    private static final int[] primes = new int[]{2, 5, 11, 17, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997, 1009, 2053, 3079, 4057, 7103, 10949, 16069, 32609, 65867, 104729};

    BadMap() {
        this(0, emptyEntries);
    }

    private BadMap(int size, Entry[] entries) {
        this.size = size;
        this.entries = entries;
    }

    BadMap<K, V> copyingPut(K k, V v) {
        int newSize = this.size + 1;
        Entry[] newEntries = newSize > this.entries.length ? new Entry[BadMap.nextPrime(newSize * 2 - 1)] : new Entry[this.entries.length];
        if (newEntries.length == this.entries.length) {
            System.arraycopy(this.entries, 0, newEntries, 0, this.entries.length);
        } else {
            BadMap.rehash(this.entries, newEntries);
        }
        int hash = Math.abs(k.hashCode());
        BadMap.store(newEntries, hash, k, v);
        return new BadMap<K, V>(newSize, newEntries);
    }

    private static <K, V> void store(Entry[] entries, int hash, K k, V v) {
        int i = hash % entries.length;
        Entry old = entries[i];
        entries[i] = new Entry(hash, k, v, old);
    }

    private static void store(Entry[] entries, Entry e) {
        int i = e.hash % entries.length;
        Entry old = entries[i];
        entries[i] = old == null && e.next == null ? e : new Entry(e.hash, e.key, e.value, old);
    }

    private static void rehash(Entry[] src, Entry[] dest) {
        for (Entry entry : src) {
            while (entry != null) {
                BadMap.store(dest, entry);
                entry = entry.next;
            }
        }
    }

    V get(K k) {
        if (this.entries.length == 0) {
            return null;
        }
        int hash = Math.abs(k.hashCode());
        int i = hash % this.entries.length;
        Entry e = this.entries[i];
        if (e == null) {
            return null;
        }
        return (V)e.find(k);
    }

    private static int nextPrime(int i) {
        for (int p : primes) {
            if (p <= i) continue;
            return p;
        }
        return primes[primes.length - 1];
    }

    static final class Entry {
        final int hash;
        final Object key;
        final Object value;
        final Entry next;

        Entry(int hash, Object k, Object v, Entry next) {
            this.hash = hash;
            this.key = k;
            this.value = v;
            this.next = next;
        }

        Object find(Object k) {
            if (this.key.equals(k)) {
                return this.value;
            }
            if (this.next != null) {
                return this.next.find(k);
            }
            return null;
        }
    }
}

