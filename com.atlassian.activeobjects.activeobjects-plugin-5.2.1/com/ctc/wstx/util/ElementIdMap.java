/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import com.ctc.wstx.util.ElementId;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.Location;

public final class ElementIdMap {
    protected static final int DEFAULT_SIZE = 128;
    protected static final int MIN_SIZE = 16;
    protected static final int FILL_PCT = 80;
    protected ElementId[] mTable;
    protected int mSize;
    protected int mSizeThreshold;
    protected int mIndexMask;
    protected ElementId mHead;
    protected ElementId mTail;

    public ElementIdMap() {
        this(128);
    }

    public ElementIdMap(int initialSize) {
        int actual;
        for (actual = 16; actual < initialSize; actual += actual) {
        }
        this.mTable = new ElementId[actual];
        this.mIndexMask = actual - 1;
        this.mSize = 0;
        this.mSizeThreshold = actual * 80 / 100;
        this.mTail = null;
        this.mHead = null;
    }

    public ElementId getFirstUndefined() {
        return this.mHead;
    }

    public ElementId addReferenced(char[] buffer, int start, int len, int hash, Location loc, PrefixedName elemName, PrefixedName attrName) {
        ElementId id;
        int index = hash & this.mIndexMask;
        for (id = this.mTable[index]; id != null; id = id.nextColliding()) {
            if (!id.idMatches(buffer, start, len)) continue;
            return id;
        }
        if (this.mSize >= this.mSizeThreshold) {
            this.rehash();
            index = hash & this.mIndexMask;
        }
        ++this.mSize;
        String idStr = new String(buffer, start, len);
        id = new ElementId(idStr, loc, false, elemName, attrName);
        id.setNextColliding(this.mTable[index]);
        this.mTable[index] = id;
        if (this.mHead == null) {
            this.mHead = this.mTail = id;
        } else {
            this.mTail.linkUndefined(id);
            this.mTail = id;
        }
        return id;
    }

    public ElementId addReferenced(String idStr, Location loc, PrefixedName elemName, PrefixedName attrName) {
        ElementId id;
        int hash = ElementIdMap.calcHash(idStr);
        int index = hash & this.mIndexMask;
        for (id = this.mTable[index]; id != null; id = id.nextColliding()) {
            if (!id.idMatches(idStr)) continue;
            return id;
        }
        if (this.mSize >= this.mSizeThreshold) {
            this.rehash();
            index = hash & this.mIndexMask;
        }
        ++this.mSize;
        id = new ElementId(idStr, loc, false, elemName, attrName);
        id.setNextColliding(this.mTable[index]);
        this.mTable[index] = id;
        if (this.mHead == null) {
            this.mHead = this.mTail = id;
        } else {
            this.mTail.linkUndefined(id);
            this.mTail = id;
        }
        return id;
    }

    public ElementId addDefined(char[] buffer, int start, int len, int hash, Location loc, PrefixedName elemName, PrefixedName attrName) {
        ElementId id;
        int index = hash & this.mIndexMask;
        for (id = this.mTable[index]; id != null && !id.idMatches(buffer, start, len); id = id.nextColliding()) {
        }
        if (id == null) {
            if (this.mSize >= this.mSizeThreshold) {
                this.rehash();
                index = hash & this.mIndexMask;
            }
            ++this.mSize;
            String idStr = new String(buffer, start, len);
            id = new ElementId(idStr, loc, true, elemName, attrName);
            id.setNextColliding(this.mTable[index]);
            this.mTable[index] = id;
        } else if (!id.isDefined()) {
            id.markDefined(loc);
            if (id == this.mHead) {
                do {
                    this.mHead = this.mHead.nextUndefined();
                } while (this.mHead != null && this.mHead.isDefined());
                if (this.mHead == null) {
                    this.mTail = null;
                }
            }
        }
        return id;
    }

    public ElementId addDefined(String idStr, Location loc, PrefixedName elemName, PrefixedName attrName) {
        ElementId id;
        int hash = ElementIdMap.calcHash(idStr);
        int index = hash & this.mIndexMask;
        for (id = this.mTable[index]; id != null && !id.idMatches(idStr); id = id.nextColliding()) {
        }
        if (id == null) {
            if (this.mSize >= this.mSizeThreshold) {
                this.rehash();
                index = hash & this.mIndexMask;
            }
            ++this.mSize;
            id = new ElementId(idStr, loc, true, elemName, attrName);
            id.setNextColliding(this.mTable[index]);
            this.mTable[index] = id;
        } else if (!id.isDefined()) {
            id.markDefined(loc);
            if (id == this.mHead) {
                do {
                    this.mHead = this.mHead.nextUndefined();
                } while (this.mHead != null && this.mHead.isDefined());
                if (this.mHead == null) {
                    this.mTail = null;
                }
            }
        }
        return id;
    }

    public static int calcHash(char[] buffer, int start, int len) {
        int hash = buffer[0];
        for (int i = 1; i < len; ++i) {
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

    private void rehash() {
        int size = this.mTable.length;
        int newSize = size << 2;
        ElementId[] oldSyms = this.mTable;
        this.mTable = new ElementId[newSize];
        this.mIndexMask = newSize - 1;
        this.mSizeThreshold <<= 2;
        int count = 0;
        for (int i = 0; i < size; ++i) {
            ElementId id = oldSyms[i];
            while (id != null) {
                ++count;
                int index = ElementIdMap.calcHash(id.getId()) & this.mIndexMask;
                ElementId nextIn = id.nextColliding();
                id.setNextColliding(this.mTable[index]);
                this.mTable[index] = id;
                id = nextIn;
            }
        }
        if (count != this.mSize) {
            ExceptionUtil.throwInternal("on rehash(): had " + this.mSize + " entries; now have " + count + ".");
        }
    }
}

