/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import org.apache.xerces.util.PrimeNumberSequenceGenerator;
import org.apache.xerces.util.SymbolTable;

public class SoftReferenceSymbolTable
extends SymbolTable {
    protected SREntry[] fBuckets = null;
    private final ReferenceQueue fReferenceQueue;

    public SoftReferenceSymbolTable(int n, float f) {
        super(1, f);
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
        this.fBuckets = new SREntry[this.fTableSize];
        this.fThreshold = (int)((float)this.fTableSize * f);
        this.fCount = 0;
        this.fReferenceQueue = new ReferenceQueue();
    }

    public SoftReferenceSymbolTable(int n) {
        this(n, 0.75f);
    }

    public SoftReferenceSymbolTable() {
        this(101, 0.75f);
    }

    @Override
    public String addSymbol(String string) {
        this.clean();
        int n = 0;
        int n2 = this.hash(string) % this.fTableSize;
        SREntry sREntry = this.fBuckets[n2];
        while (sREntry != null) {
            SREntryData sREntryData = (SREntryData)sREntry.get();
            if (sREntryData != null) {
                if (sREntryData.symbol.equals(string)) {
                    return sREntryData.symbol;
                }
                ++n;
            }
            sREntry = sREntry.next;
        }
        return this.addSymbol0(string, n2, n);
    }

    private String addSymbol0(String string, int n, int n2) {
        SREntry sREntry;
        if (this.fCount >= this.fThreshold) {
            this.rehash();
            n = this.hash(string) % this.fTableSize;
        } else if (n2 >= this.fCollisionThreshold) {
            this.rebalance();
            n = this.hash(string) % this.fTableSize;
        }
        string = string.intern();
        this.fBuckets[n] = sREntry = new SREntry(string, this.fBuckets[n], n, this.fReferenceQueue);
        ++this.fCount;
        return string;
    }

    @Override
    public String addSymbol(char[] cArray, int n, int n2) {
        this.clean();
        int n3 = 0;
        int n4 = this.hash(cArray, n, n2) % this.fTableSize;
        SREntry sREntry = this.fBuckets[n4];
        while (sREntry != null) {
            block4: {
                SREntryData sREntryData = (SREntryData)sREntry.get();
                if (sREntryData != null) {
                    if (n2 == sREntryData.characters.length) {
                        for (int i = 0; i < n2; ++i) {
                            if (cArray[n + i] == sREntryData.characters[i]) continue;
                            ++n3;
                            break block4;
                        }
                        return sREntryData.symbol;
                    }
                    ++n3;
                }
            }
            sREntry = sREntry.next;
        }
        return this.addSymbol0(cArray, n, n2, n4, n3);
    }

    private String addSymbol0(char[] cArray, int n, int n2, int n3, int n4) {
        SREntry sREntry;
        if (this.fCount >= this.fThreshold) {
            this.rehash();
            n3 = this.hash(cArray, n, n2) % this.fTableSize;
        } else if (n4 >= this.fCollisionThreshold) {
            this.rebalance();
            n3 = this.hash(cArray, n, n2) % this.fTableSize;
        }
        String string = new String(cArray, n, n2).intern();
        this.fBuckets[n3] = sREntry = new SREntry(string, cArray, n, n2, this.fBuckets[n3], n3, this.fReferenceQueue);
        ++this.fCount;
        return string;
    }

    @Override
    protected void rehash() {
        this.rehashCommon(this.fBuckets.length * 2 + 1);
    }

    protected void compact() {
        this.rehashCommon((int)((float)this.fCount / this.fLoadFactor) * 2 + 1);
    }

    @Override
    protected void rebalance() {
        if (this.fHashMultipliers == null) {
            this.fHashMultipliers = new int[32];
        }
        PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
        this.rehashCommon(this.fBuckets.length);
    }

    private void rehashCommon(int n) {
        int n2 = this.fBuckets.length;
        SREntry[] sREntryArray = this.fBuckets;
        SREntry[] sREntryArray2 = new SREntry[n];
        this.fThreshold = (int)((float)n * this.fLoadFactor);
        this.fBuckets = sREntryArray2;
        this.fTableSize = this.fBuckets.length;
        int n3 = n2;
        while (n3-- > 0) {
            SREntry sREntry = sREntryArray[n3];
            while (sREntry != null) {
                SREntry sREntry2 = sREntry;
                sREntry = sREntry.next;
                SREntryData sREntryData = (SREntryData)sREntry2.get();
                if (sREntryData != null) {
                    int n4 = this.hash(sREntryData.symbol) % n;
                    if (sREntryArray2[n4] != null) {
                        sREntryArray2[n4].prev = sREntry2;
                    }
                    sREntry2.bucket = n4;
                    sREntry2.next = sREntryArray2[n4];
                    sREntryArray2[n4] = sREntry2;
                } else {
                    sREntry2.bucket = -1;
                    sREntry2.next = null;
                    --this.fCount;
                }
                sREntry2.prev = null;
            }
        }
    }

    @Override
    public boolean containsSymbol(String string) {
        int n = this.hash(string) % this.fTableSize;
        int n2 = string.length();
        SREntry sREntry = this.fBuckets[n];
        while (sREntry != null) {
            block4: {
                SREntryData sREntryData = (SREntryData)sREntry.get();
                if (sREntryData != null && n2 == sREntryData.characters.length) {
                    for (int i = 0; i < n2; ++i) {
                        if (string.charAt(i) == sREntryData.characters[i]) {
                            continue;
                        }
                        break block4;
                    }
                    return true;
                }
            }
            sREntry = sREntry.next;
        }
        return false;
    }

    @Override
    public boolean containsSymbol(char[] cArray, int n, int n2) {
        int n3 = this.hash(cArray, n, n2) % this.fTableSize;
        SREntry sREntry = this.fBuckets[n3];
        while (sREntry != null) {
            block4: {
                SREntryData sREntryData = (SREntryData)sREntry.get();
                if (sREntryData != null && n2 == sREntryData.characters.length) {
                    for (int i = 0; i < n2; ++i) {
                        if (cArray[n + i] == sREntryData.characters[i]) {
                            continue;
                        }
                        break block4;
                    }
                    return true;
                }
            }
            sREntry = sREntry.next;
        }
        return false;
    }

    private void removeEntry(SREntry sREntry) {
        int n = sREntry.bucket;
        if (n >= 0) {
            if (sREntry.next != null) {
                sREntry.next.prev = sREntry.prev;
            }
            if (sREntry.prev != null) {
                sREntry.prev.next = sREntry.next;
            } else {
                this.fBuckets[n] = sREntry.next;
            }
            --this.fCount;
        }
    }

    private void clean() {
        SREntry sREntry = (SREntry)this.fReferenceQueue.poll();
        if (sREntry != null) {
            do {
                this.removeEntry(sREntry);
            } while ((sREntry = (SREntry)this.fReferenceQueue.poll()) != null);
            if (this.fCount < this.fThreshold >> 2) {
                this.compact();
            }
        }
    }

    protected static final class SREntryData {
        public final String symbol;
        public final char[] characters;

        public SREntryData(String string) {
            this.symbol = string;
            this.characters = new char[this.symbol.length()];
            this.symbol.getChars(0, this.characters.length, this.characters, 0);
        }

        public SREntryData(String string, char[] cArray, int n, int n2) {
            this.symbol = string;
            this.characters = new char[n2];
            System.arraycopy(cArray, n, this.characters, 0, n2);
        }
    }

    protected static final class SREntry
    extends SoftReference {
        public SREntry next;
        public SREntry prev;
        public int bucket;

        public SREntry(String string, SREntry sREntry, int n, ReferenceQueue referenceQueue) {
            super(new SREntryData(string), referenceQueue);
            this.initialize(sREntry, n);
        }

        public SREntry(String string, char[] cArray, int n, int n2, SREntry sREntry, int n3, ReferenceQueue referenceQueue) {
            super(new SREntryData(string, cArray, n, n2), referenceQueue);
            this.initialize(sREntry, n3);
        }

        private void initialize(SREntry sREntry, int n) {
            this.next = sREntry;
            if (sREntry != null) {
                sREntry.prev = this;
            }
            this.prev = null;
            this.bucket = n;
        }
    }
}

