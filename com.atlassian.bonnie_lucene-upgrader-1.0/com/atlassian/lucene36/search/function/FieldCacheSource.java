/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.search.function.DocValues;
import com.atlassian.lucene36.search.function.ValueSource;
import java.io.IOException;

public abstract class FieldCacheSource
extends ValueSource {
    private String field;

    public FieldCacheSource(String field) {
        this.field = field;
    }

    public final DocValues getValues(IndexReader reader) throws IOException {
        return this.getCachedFieldValues(FieldCache.DEFAULT, this.field, reader);
    }

    public String description() {
        return this.field;
    }

    public abstract DocValues getCachedFieldValues(FieldCache var1, String var2, IndexReader var3) throws IOException;

    public final boolean equals(Object o) {
        if (!(o instanceof FieldCacheSource)) {
            return false;
        }
        FieldCacheSource other = (FieldCacheSource)o;
        return this.field.equals(other.field) && this.cachedFieldSourceEquals(other);
    }

    public final int hashCode() {
        return this.field.hashCode() + this.cachedFieldSourceHashCode();
    }

    public abstract boolean cachedFieldSourceEquals(FieldCacheSource var1);

    public abstract int cachedFieldSourceHashCode();
}

