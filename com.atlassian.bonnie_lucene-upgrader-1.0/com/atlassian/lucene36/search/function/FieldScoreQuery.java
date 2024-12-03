/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.function;

import com.atlassian.lucene36.search.function.ByteFieldSource;
import com.atlassian.lucene36.search.function.FloatFieldSource;
import com.atlassian.lucene36.search.function.IntFieldSource;
import com.atlassian.lucene36.search.function.ShortFieldSource;
import com.atlassian.lucene36.search.function.ValueSource;
import com.atlassian.lucene36.search.function.ValueSourceQuery;

public class FieldScoreQuery
extends ValueSourceQuery {
    public FieldScoreQuery(String field, Type type) {
        super(FieldScoreQuery.getValueSource(field, type));
    }

    private static ValueSource getValueSource(String field, Type type) {
        if (type == Type.BYTE) {
            return new ByteFieldSource(field);
        }
        if (type == Type.SHORT) {
            return new ShortFieldSource(field);
        }
        if (type == Type.INT) {
            return new IntFieldSource(field);
        }
        if (type == Type.FLOAT) {
            return new FloatFieldSource(field);
        }
        throw new IllegalArgumentException(type + " is not a known Field Score Query Type!");
    }

    public static class Type {
        public static final Type BYTE = new Type("byte");
        public static final Type SHORT = new Type("short");
        public static final Type INT = new Type("int");
        public static final Type FLOAT = new Type("float");
        private String typeName;

        private Type(String name) {
            this.typeName = name;
        }

        public String toString() {
            return this.getClass().getName() + "::" + this.typeName;
        }
    }
}

