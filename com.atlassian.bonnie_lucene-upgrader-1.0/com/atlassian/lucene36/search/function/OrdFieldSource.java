/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.FieldCache;
import com.atlassian.lucene36.search.function.DocValues;
import com.atlassian.lucene36.search.function.ValueSource;
import java.io.IOException;

public class OrdFieldSource
extends ValueSource {
    protected String field;
    private static final int hcode = OrdFieldSource.class.hashCode();

    public OrdFieldSource(String field) {
        this.field = field;
    }

    public String description() {
        return "ord(" + this.field + ')';
    }

    public DocValues getValues(IndexReader reader) throws IOException {
        final int[] arr = FieldCache.DEFAULT.getStringIndex((IndexReader)reader, (String)this.field).order;
        return new DocValues(){

            public float floatVal(int doc) {
                return arr[doc];
            }

            public String strVal(int doc) {
                return Integer.toString(arr[doc]);
            }

            public String toString(int doc) {
                return OrdFieldSource.this.description() + '=' + this.intVal(doc);
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
        if (o.getClass() != OrdFieldSource.class) {
            return false;
        }
        OrdFieldSource other = (OrdFieldSource)o;
        return this.field.equals(other.field);
    }

    public int hashCode() {
        return hcode + this.field.hashCode();
    }
}

