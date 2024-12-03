/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.search.function.DocValues;
import com.atlassian.lucene36.search.function.FieldCacheSource;
import java.io.IOException;

public class FloatFieldSource
extends FieldCacheSource {
    private FieldCache.FloatParser parser;

    public FloatFieldSource(String field) {
        this(field, null);
    }

    public FloatFieldSource(String field, FieldCache.FloatParser parser) {
        super(field);
        this.parser = parser;
    }

    public String description() {
        return "float(" + super.description() + ')';
    }

    public DocValues getCachedFieldValues(FieldCache cache, String field, IndexReader reader) throws IOException {
        final float[] arr = cache.getFloats(reader, field, this.parser);
        return new DocValues(){

            public float floatVal(int doc) {
                return arr[doc];
            }

            public String toString(int doc) {
                return FloatFieldSource.this.description() + '=' + arr[doc];
            }

            Object getInnerArray() {
                return arr;
            }
        };
    }

    public boolean cachedFieldSourceEquals(FieldCacheSource o) {
        if (o.getClass() != FloatFieldSource.class) {
            return false;
        }
        FloatFieldSource other = (FloatFieldSource)o;
        return this.parser == null ? other.parser == null : this.parser.getClass() == other.parser.getClass();
    }

    public int cachedFieldSourceHashCode() {
        return this.parser == null ? Float.class.hashCode() : this.parser.getClass().hashCode();
    }
}

