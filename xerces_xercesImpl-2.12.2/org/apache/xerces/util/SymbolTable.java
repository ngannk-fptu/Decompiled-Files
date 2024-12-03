/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import org.apache.xerces.util.PrimeNumberSequenceGenerator;

public class SymbolTable {
    protected static final int TABLE_SIZE = 101;
    protected static final int MAX_HASH_COLLISIONS = 40;
    protected static final int MULTIPLIERS_SIZE = 32;
    protected static final int MULTIPLIERS_MASK = 31;
    protected Entry[] fBuckets = null;
    protected int fTableSize;
    protected transient int fCount;
    protected int fThreshold;
    protected float fLoadFactor;
    protected final int fCollisionThreshold;
    protected int[] fHashMultipliers;

    public SymbolTable(int n, float f) {
        if (n < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + n);
        }
        if (f <= 0.0f || Float.isNaN(f)) {
            throw new IllegalArgumentException("Illegal Load: " + f);
        }
        if (n == 0) {
            n = 1;
        }
        this.fLoadFactor = f;
        this.fTableSize = n;
        this.fBuckets = new Entry[this.fTableSize];
        this.fThreshold = (int)((float)this.fTableSize * f);
        this.fCollisionThreshold = (int)(40.0f * f);
        this.fCount = 0;
    }

    public SymbolTable(int n) {
        this(n, 0.75f);
    }

    public SymbolTable() {
        this(101, 0.75f);
    }

    public String addSymbol(String string) {
        int n = 0;
        int n2 = this.hash(string) % this.fTableSize;
        Entry entry = this.fBuckets[n2];
        while (entry != null) {
            if (entry.symbol.equals(string)) {
                return entry.symbol;
            }
            ++n;
            entry = entry.next;
        }
        return this.addSymbol0(string, n2, n);
    }

    private String addSymbol0(String string, int n, int n2) {
        Entry entry;
        if (this.fCount >= this.fThreshold) {
            this.rehash();
            n = this.hash(string) % this.fTableSize;
        } else if (n2 >= this.fCollisionThreshold) {
            this.rebalance();
            n = this.hash(string) % this.fTableSize;
        }
        this.fBuckets[n] = entry = new Entry(string, this.fBuckets[n]);
        ++this.fCount;
        return entry.symbol;
    }

    public String addSymbol(char[] cArray, int n, int n2) {
        int n3 = 0;
        int n4 = this.hash(cArray, n, n2) % this.fTableSize;
        Entry entry = this.fBuckets[n4];
        while (entry != null) {
            block3: {
                if (n2 == entry.characters.length) {
                    for (int i = 0; i < n2; ++i) {
                        if (cArray[n + i] == entry.characters[i]) continue;
                        ++n3;
                        break block3;
                    }
                    return entry.symbol;
                }
                ++n3;
            }
            entry = entry.next;
        }
        return this.addSymbol0(cArray, n, n2, n4, n3);
    }

    private String addSymbol0(char[] cArray, int n, int n2, int n3, int n4) {
        Entry entry;
        if (this.fCount >= this.fThreshold) {
            this.rehash();
            n3 = this.hash(cArray, n, n2) % this.fTableSize;
        } else if (n4 >= this.fCollisionThreshold) {
            this.rebalance();
            n3 = this.hash(cArray, n, n2) % this.fTableSize;
        }
        this.fBuckets[n3] = entry = new Entry(cArray, n, n2, this.fBuckets[n3]);
        ++this.fCount;
        return entry.symbol;
    }

    public int hash(String string) {
        if (this.fHashMultipliers == null) {
            return string.hashCode() & Integer.MAX_VALUE;
        }
        return this.hash0(string);
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

    public int hash(char[] cArray, int n, int n2) {
        if (this.fHashMultipliers == null) {
            int n3 = 0;
            for (int i = 0; i < n2; ++i) {
                n3 = n3 * 31 + cArray[n + i];
            }
            return n3 & Integer.MAX_VALUE;
        }
        return this.hash0(cArray, n, n2);
    }

    private int hash0(char[] cArray, int n, int n2) {
        int n3 = 0;
        int[] nArray = this.fHashMultipliers;
        for (int i = 0; i < n2; ++i) {
            n3 = n3 * nArray[i & 0x1F] + cArray[n + i];
        }
        return n3 & Integer.MAX_VALUE;
    }

    protected void rehash() {
        this.rehashCommon(this.fBuckets.length * 2 + 1);
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
        this.fThreshold = (int)((float)n * this.fLoadFactor);
        this.fBuckets = entryArray2;
        this.fTableSize = this.fBuckets.length;
        int n3 = n2;
        while (n3-- > 0) {
            Entry entry = entryArray[n3];
            while (entry != null) {
                Entry entry2 = entry;
                entry = entry.next;
                int n4 = this.hash(entry2.symbol) % n;
                entry2.next = entryArray2[n4];
                entryArray2[n4] = entry2;
            }
        }
    }

    public boolean containsSymbol(String string) {
        int n = this.hash(string) % this.fTableSize;
        int n2 = string.length();
        Entry entry = this.fBuckets[n];
        while (entry != null) {
            block4: {
                if (n2 == entry.characters.length) {
                    for (int i = 0; i < n2; ++i) {
                        if (string.charAt(i) == entry.characters[i]) {
                            continue;
                        }
                        break block4;
                    }
                    return true;
                }
            }
            entry = entry.next;
        }
        return false;
    }

    public boolean containsSymbol(char[] cArray, int n, int n2) {
        int n3 = this.hash(cArray, n, n2) % this.fTableSize;
        Entry entry = this.fBuckets[n3];
        while (entry != null) {
            block4: {
                if (n2 == entry.characters.length) {
                    for (int i = 0; i < n2; ++i) {
                        if (cArray[n + i] == entry.characters[i]) {
                            continue;
                        }
                        break block4;
                    }
                    return true;
                }
            }
            entry = entry.next;
        }
        return false;
    }

    protected static final class Entry {
        public final String symbol;
        public final char[] characters;
        public Entry next;

        public Entry(String string, Entry entry) {
            this.symbol = string.intern();
            this.characters = new char[string.length()];
            string.getChars(0, this.characters.length, this.characters, 0);
            this.next = entry;
        }

        public Entry(char[] cArray, int n, int n2, Entry entry) {
            this.characters = new char[n2];
            System.arraycopy(cArray, n, this.characters, 0, n2);
            this.symbol = new String(this.characters).intern();
            this.next = entry;
        }
    }
}

