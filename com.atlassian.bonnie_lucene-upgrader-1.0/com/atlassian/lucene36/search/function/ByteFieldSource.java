/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.search.function.DocValues;
import com.atlassian.lucene36.search.function.FieldCacheSource;
import java.io.IOException;

public class ByteFieldSource
extends FieldCacheSource {
    private FieldCache.ByteParser parser;

    public ByteFieldSource(String field) {
        this(field, null);
    }

    public ByteFieldSource(String field, FieldCache.ByteParser parser) {
        super(field);
        this.parser = parser;
    }

    public String description() {
        return "byte(" + super.description() + ')';
    }

    public DocValues getCachedFieldValues(FieldCache cache, String field, IndexReader reader) throws IOException {
        final byte[] arr = cache.getBytes(reader, field, this.parser);
        return new DocValues(){

            public float floatVal(int doc) {
                return arr[doc];
            }

            public int intVal(int doc) {
                return arr[doc];
            }

            public String toString(int doc) {
                return ByteFieldSource.this.description() + '=' + this.intVal(doc);
            }

            Object getInnerArray() {
                return arr;
            }
        };
    }

    public boolean cachedFieldSourceEquals(FieldCacheSource o) {
        if (o.getClass() != ByteFieldSource.class) {
            return false;
        }
        ByteFieldSource other = (ByteFieldSource)o;
        return this.parser == null ? other.parser == null : this.parser.getClass() == other.parser.getClass();
    }

    public int cachedFieldSourceHashCode() {
        return this.parser == null ? Byte.class.hashCode() : this.parser.getClass().hashCode();
    }
}

