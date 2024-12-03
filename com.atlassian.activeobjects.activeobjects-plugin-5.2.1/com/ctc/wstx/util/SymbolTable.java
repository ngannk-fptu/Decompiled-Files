/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

public class SymbolTable {
    protected static final int DEFAULT_TABLE_SIZE = 128;
    protected static final float DEFAULT_FILL_FACTOR = 0.75f;
    protected static final String EMPTY_STRING = "";
    protected boolean mInternStrings;
    protected String[] mSymbols;
    protected Bucket[] mBuckets;
    protected int mSize;
    protected int mSizeThreshold;
    protected int mIndexMask;
    protected int mThisVersion;
    protected boolean mDirty;

    public SymbolTable() {
        this(true);
    }

    public SymbolTable(boolean internStrings) {
        this(internStrings, 128);
    }

    public SymbolTable(boolean internStrings, int initialSize) {
        this(internStrings, initialSize, 0.75f);
    }

    public SymbolTable(boolean internStrings, int initialSize, float fillFactor) {
        int currSize;
        this.mInternStrings = internStrings;
        this.mThisVersion = 1;
        this.mDirty = true;
        if (initialSize < 1) {
            throw new IllegalArgumentException("Can not use negative/zero initial size: " + initialSize);
        }
        for (currSize = 4; currSize < initialSize; currSize += currSize) {
        }
        initialSize = currSize;
        this.mSymbols = new String[initialSize];
        this.mBuckets = new Bucket[initialSize >> 1];
        this.mIndexMask = initialSize - 1;
        this.mSize = 0;
        if (fillFactor < 0.01f) {
            throw new IllegalArgumentException("Fill factor can not be lower than 0.01.");
        }
        if (fillFactor > 10.0f) {
            throw new IllegalArgumentException("Fill factor can not be higher than 10.0.");
        }
        this.mSizeThreshold = (int)((double)((float)initialSize * fillFactor) + 0.5);
    }

    private SymbolTable(boolean internStrings, String[] symbols, Bucket[] buckets, int size, int sizeThreshold, int indexMask, int version) {
        this.mInternStrings = internStrings;
        this.mSymbols = symbols;
        this.mBuckets = buckets;
        this.mSize = size;
        this.mSizeThreshold = sizeThreshold;
        this.mIndexMask = indexMask;
        this.mThisVersion = version;
        this.mDirty = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SymbolTable makeChild() {
        int version;
        int indexMask;
        int sizeThreshold;
        int size;
        Bucket[] buckets;
        String[] symbols;
        boolean internStrings;
        SymbolTable symbolTable = this;
        synchronized (symbolTable) {
            internStrings = this.mInternStrings;
            symbols = this.mSymbols;
            buckets = this.mBuckets;
            size = this.mSize;
            sizeThreshold = this.mSizeThreshold;
            indexMask = this.mIndexMask;
            version = this.mThisVersion + 1;
        }
        return new SymbolTable(internStrings, symbols, buckets, size, sizeThreshold, indexMask, version);
    }

    public synchronized void mergeChild(SymbolTable child) {
        if (child.size() <= this.size()) {
            return;
        }
        this.mSymbols = child.mSymbols;
        this.mBuckets = child.mBuckets;
        this.mSize = child.mSize;
        this.mSizeThreshold = child.mSizeThreshold;
        this.mIndexMask = child.mIndexMask;
        ++this.mThisVersion;
        this.mDirty = false;
        child.mDirty = false;
    }

    public void setInternStrings(boolean state) {
        this.mInternStrings = state;
    }

    public int size() {
        return this.mSize;
    }

    public int version() {
        return this.mThisVersion;
    }

    public boolean isDirty() {
        return this.mDirty;
    }

    public boolean isDirectChildOf(SymbolTable t) {
        return this.mThisVersion == t.mThisVersion + 1;
    }

    public String findSymbol(char[] buffer, int start, int len, int hash) {
        if (len < 1) {
            return EMPTY_STRING;
        }
        String sym = this.mSymbols[hash &= this.mIndexMask];
        if (sym != null) {
            Bucket b;
            if (sym.length() == len) {
                int i = 0;
                while (sym.charAt(i) == buffer[start + i] && ++i < len) {
                }
                if (i == len) {
                    return sym;
                }
            }
            if ((b = this.mBuckets[hash >> 1]) != null && (sym = b.find(buffer, start, len)) != null) {
                return sym;
            }
        }
        if (this.mSize >= this.mSizeThreshold) {
            this.rehash();
            hash = SymbolTable.calcHash(buffer, start, len) & this.mIndexMask;
        } else if (!this.mDirty) {
            this.copyArrays();
            this.mDirty = true;
        }
        ++this.mSize;
        String newSymbol = new String(buffer, start, len);
        if (this.mInternStrings) {
            newSymbol = newSymbol.intern();
        }
        if (this.mSymbols[hash] == null) {
            this.mSymbols[hash] = newSymbol;
        } else {
            int bix = hash >> 1;
            this.mBuckets[bix] = new Bucket(newSymbol, this.mBuckets[bix]);
        }
        return newSymbol;
    }

    public String findSymbolIfExists(char[] buffer, int start, int len, int hash) {
        if (len < 1) {
            return EMPTY_STRING;
        }
        String sym = this.mSymbols[hash &= this.mIndexMask];
        if (sym != null) {
            Bucket b;
            if (sym.length() == len) {
                int i = 0;
                while (sym.charAt(i) == buffer[start + i] && ++i < len) {
                }
                if (i == len) {
                    return sym;
                }
            }
            if ((b = this.mBuckets[hash >> 1]) != null && (sym = b.find(buffer, start, len)) != null) {
                return sym;
            }
        }
        return null;
    }

    public String findSymbol(String str) {
        int len = str.length();
        if (len < 1) {
            return EMPTY_STRING;
        }
        int index = SymbolTable.calcHash(str) & this.mIndexMask;
        String sym = this.mSymbols[index];
        if (sym != null) {
            Bucket b;
            if (sym.length() == len) {
                int i;
                for (i = 0; i < len && sym.charAt(i) == str.charAt(i); ++i) {
                }
                if (i == len) {
                    return sym;
                }
            }
            if ((b = this.mBuckets[index >> 1]) != null && (sym = b.find(str)) != null) {
                return sym;
            }
        }
        if (this.mSize >= this.mSizeThreshold) {
            this.rehash();
            index = SymbolTable.calcHash(str) & this.mIndexMask;
        } else if (!this.mDirty) {
            this.copyArrays();
            this.mDirty = true;
        }
        ++this.mSize;
        if (this.mInternStrings) {
            str = str.intern();
        }
        if (this.mSymbols[index] == null) {
            this.mSymbols[index] = str;
        } else {
            int bix = index >> 1;
            this.mBuckets[bix] = new Bucket(str, this.mBuckets[bix]);
        }
        return str;
    }

    public static int calcHash(char[] buffer, int start, int len) {
        int hash = buffer[start];
        int end = start + len;
        for (int i = start + 1; i < end; ++i) {
            hash = hash * 31 + buffer[i];
        }
        return hash;
    }

    public static int calcHash(String key) {
        int hash = key.charAt(0);
        int len = key.length();
        for (int i = 1; i < len; ++i) {
            hash = hash * 31 + key.charAt(i);
        }
        return hash;
    }

    private void copyArrays() {
        String[] oldSyms = this.mSymbols;
        int size = oldSyms.length;
        this.mSymbols = new String[size];
        System.arraycopy(oldSyms, 0, this.mSymbols, 0, size);
        Bucket[] oldBuckets = this.mBuckets;
        size = oldBuckets.length;
        this.mBuckets = new Bucket[size];
        System.arraycopy(oldBuckets, 0, this.mBuckets, 0, size);
    }

    private void rehash() {
        int i;
        int size = this.mSymbols.length;
        int newSize = size + size;
        String[] oldSyms = this.mSymbols;
        Bucket[] oldBuckets = this.mBuckets;
        this.mSymbols = new String[newSize];
        this.mBuckets = new Bucket[newSize >> 1];
        this.mIndexMask = newSize - 1;
        this.mSizeThreshold += this.mSizeThreshold;
        int count = 0;
        for (i = 0; i < size; ++i) {
            String symbol = oldSyms[i];
            if (symbol == null) continue;
            ++count;
            int index = SymbolTable.calcHash(symbol) & this.mIndexMask;
            if (this.mSymbols[index] == null) {
                this.mSymbols[index] = symbol;
                continue;
            }
            int bix = index >> 1;
            this.mBuckets[bix] = new Bucket(symbol, this.mBuckets[bix]);
        }
        size >>= 1;
        for (i = 0; i < size; ++i) {
            for (Bucket b = oldBuckets[i]; b != null; b = b.getNext()) {
                ++count;
                String symbol = b.getSymbol();
                int index = SymbolTable.calcHash(symbol) & this.mIndexMask;
                if (this.mSymbols[index] == null) {
                    this.mSymbols[index] = symbol;
                    continue;
                }
                int bix = index >> 1;
                this.mBuckets[bix] = new Bucket(symbol, this.mBuckets[bix]);
            }
        }
        if (count != this.mSize) {
            throw new IllegalStateException("Internal error on SymbolTable.rehash(): had " + this.mSize + " entries; now have " + count + ".");
        }
    }

    public double calcAvgSeek() {
        int i;
        int count = 0;
        int len = this.mSymbols.length;
        for (i = 0; i < len; ++i) {
            if (this.mSymbols[i] == null) continue;
            ++count;
        }
        len = this.mBuckets.length;
        for (i = 0; i < len; ++i) {
            int cost = 2;
            for (Bucket b = this.mBuckets[i]; b != null; b = b.getNext()) {
                count += cost;
                ++cost;
            }
        }
        return (double)count / (double)this.mSize;
    }

    static final class Bucket {
        private final String mSymbol;
        private final Bucket mNext;

        public Bucket(String symbol, Bucket next) {
            this.mSymbol = symbol;
            this.mNext = next;
        }

        public String getSymbol() {
            return this.mSymbol;
        }

        public Bucket getNext() {
            return this.mNext;
        }

        public String find(char[] buf, int start, int len) {
            String sym = this.mSymbol;
            Bucket b = this.mNext;
            while (true) {
                if (sym.length() == len) {
                    int i = 0;
                    while (sym.charAt(i) == buf[start + i] && ++i < len) {
                    }
                    if (i == len) {
                        return sym;
                    }
                }
                if (b == null) break;
                sym = b.getSymbol();
                b = b.getNext();
            }
            return null;
        }

        public String find(String str) {
            String sym = this.mSymbol;
            Bucket b = this.mNext;
            while (true) {
                if (sym.equals(str)) {
                    return sym;
                }
                if (b == null) break;
                sym = b.getSymbol();
                b = b.getNext();
            }
            return null;
        }
    }
}

