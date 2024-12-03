/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.FieldCache
 */
package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.FieldCache;

public abstract class FieldCacheSource
extends ValueSource {
    protected final String field;
    protected final FieldCache cache = FieldCache.DEFAULT;

    public FieldCacheSource(String field) {
        this.field = field;
    }

    public FieldCache getFieldCache() {
        return this.cache;
    }

    public String getField() {
        return this.field;
    }

    @Override
    public String description() {
        return this.field;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FieldCacheSource)) {
            return false;
        }
        FieldCacheSource other = (FieldCacheSource)o;
        return this.field.equals(other.field) && this.cache == other.cache;
    }

    @Override
    public int hashCode() {
        return this.cache.hashCode() + this.field.hashCode();
    }
}

