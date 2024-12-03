/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import org.aspectj.weaver.tools.cache.CachedClassReference;

public class CachedClassEntry {
    private final CachedClassReference ref;
    private final byte[] weavedBytes;
    private final EntryType type;

    public CachedClassEntry(CachedClassReference ref, byte[] weavedBytes, EntryType type) {
        this.weavedBytes = weavedBytes;
        this.ref = ref;
        this.type = type;
    }

    public String getClassName() {
        return this.ref.getClassName();
    }

    public byte[] getBytes() {
        return this.weavedBytes;
    }

    public String getKey() {
        return this.ref.getKey();
    }

    public boolean isGenerated() {
        return this.type == EntryType.GENERATED;
    }

    public boolean isWeaved() {
        return this.type == EntryType.WEAVED;
    }

    public boolean isIgnored() {
        return this.type == EntryType.IGNORED;
    }

    public int hashCode() {
        return this.getClassName().hashCode() + this.getKey().hashCode() + this.type.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CachedClassEntry other = (CachedClassEntry)obj;
        return this.getClassName().equals(other.getClassName()) && this.getKey().equals(other.getKey()) && this.type == other.type;
    }

    public String toString() {
        return this.getClassName() + "[" + (Object)((Object)this.type) + "]";
    }

    static enum EntryType {
        GENERATED,
        WEAVED,
        IGNORED;

    }
}

