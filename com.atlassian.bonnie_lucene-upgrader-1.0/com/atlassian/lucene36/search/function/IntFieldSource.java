/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.search.function.DocValues;
import com.atlassian.lucene36.search.function.FieldCacheSource;
import java.io.IOException;

public class IntFieldSource
extends FieldCacheSource {
    private FieldCache.IntParser parser;

    public IntFieldSource(String field) {
        this(field, null);
    }

    public IntFieldSource(String field, FieldCache.IntParser parser) {
        super(field);
        this.parser = parser;
    }

    public String description() {
        return "int(" + super.description() + ')';
    }

    public DocValues getCachedFieldValues(FieldCache cache, String field, IndexReader reader) throws IOException {
        final int[] arr = cache.getInts(reader, field, this.parser);
        return new DocValues(){

            public float floatVal(int doc) {
                return arr[doc];
            }

            public int intVal(int doc) {
                return arr[doc];
            }

            public String toString(int doc) {
                return IntFieldSource.this.description() + '=' + this.intVal(doc);
            }

            Object getInnerArray() {
                return arr;
            }
        };
    }

    public boolean cachedFieldSourceEquals(FieldCacheSource o) {
        if (o.getClass() != IntFieldSource.class) {
            return false;
        }
        IntFieldSource other = (IntFieldSource)o;
        return this.parser == null ? other.parser == null : this.parser.getClass() == other.parser.getClass();
    }

    public int cachedFieldSourceHashCode() {
        return this.parser == null ? Integer.class.hashCode() : this.parser.getClass().hashCode();
    }
}

