/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import org.aspectj.weaver.tools.cache.CacheKeyResolver;

public class CachedClassReference {
    private final String key;
    private final String className;

    protected CachedClassReference(String key, CacheKeyResolver resolver) {
        this(key, resolver.keyToClass(key));
    }

    protected CachedClassReference(String key, String className) {
        this.key = key;
        this.className = className;
    }

    public String getKey() {
        return this.key;
    }

    public String getClassName() {
        return this.className;
    }

    public int hashCode() {
        return this.getKey().hashCode() + this.getClassName().hashCode();
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
        CachedClassReference other = (CachedClassReference)obj;
        return this.getKey().equals(other.getKey()) && this.getClassName().equals(other.getClassName());
    }

    public String toString() {
        return this.getClassName() + "[" + this.getKey() + "]";
    }

    static enum EntryType {
        GENERATED,
        WEAVED,
        IGNORED;

    }
}

