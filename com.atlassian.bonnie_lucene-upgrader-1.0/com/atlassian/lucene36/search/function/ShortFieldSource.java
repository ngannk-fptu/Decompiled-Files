/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.search.function.DocValues;
import com.atlassian.lucene36.search.function.FieldCacheSource;
import java.io.IOException;

public class ShortFieldSource
extends FieldCacheSource {
    private FieldCache.ShortParser parser;

    public ShortFieldSource(String field) {
        this(field, null);
    }

    public ShortFieldSource(String field, FieldCache.ShortParser parser) {
        super(field);
        this.parser = parser;
    }

    public String description() {
        return "short(" + super.description() + ')';
    }

    public DocValues getCachedFieldValues(FieldCache cache, String field, IndexReader reader) throws IOException {
        final short[] arr = cache.getShorts(reader, field, this.parser);
        return new DocValues(){

            public float floatVal(int doc) {
                return arr[doc];
            }

            public int intVal(int doc) {
                return arr[doc];
            }

            public String toString(int doc) {
                return ShortFieldSource.this.description() + '=' + this.intVal(doc);
            }

            Object getInnerArray() {
                return arr;
            }
        };
    }

    public boolean cachedFieldSourceEquals(FieldCacheSource o) {
        if (o.getClass() != ShortFieldSource.class) {
            return false;
        }
        ShortFieldSource other = (ShortFieldSource)o;
        return this.parser == null ? other.parser == null : this.parser.getClass() == other.parser.getClass();
    }

    public int cachedFieldSourceHashCode() {
        return this.parser == null ? Short.class.hashCode() : this.parser.getClass().hashCode();
    }
}

