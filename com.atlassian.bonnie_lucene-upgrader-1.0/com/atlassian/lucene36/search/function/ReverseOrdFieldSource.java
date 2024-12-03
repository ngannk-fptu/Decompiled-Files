/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.search.function.DocValues;
import com.atlassian.lucene36.search.function.ValueSource;
import java.io.IOException;

public class ReverseOrdFieldSource
extends ValueSource {
    public String field;
    private static final int hcode = ReverseOrdFieldSource.class.hashCode();

    public ReverseOrdFieldSource(String field) {
        this.field = field;
    }

    public String description() {
        return "rord(" + this.field + ')';
    }

    public DocValues getValues(IndexReader reader) throws IOException {
        FieldCache.StringIndex sindex = FieldCache.DEFAULT.getStringIndex(reader, this.field);
        final int[] arr = sindex.order;
        final int end = sindex.lookup.length;
        return new DocValues(){

            public float floatVal(int doc) {
                return end - arr[doc];
            }

            public int intVal(int doc) {
                return end - arr[doc];
            }

            public String strVal(int doc) {
                return Integer.toString(this.intVal(doc));
            }

            public String toString(int doc) {
                return ReverseOrdFieldSource.this.description() + '=' + this.strVal(doc);
            }

            Object getInnerArray() {
                return arr;
            }
        };
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != ReverseOrdFieldSource.class) {
            return false;
        }
        ReverseOrdFieldSource other = (ReverseOrdFieldSource)o;
        return this.field.equals(other.field);
    }

    public int hashCode() {
        return hcode + this.field.hashCode();
    }
}

