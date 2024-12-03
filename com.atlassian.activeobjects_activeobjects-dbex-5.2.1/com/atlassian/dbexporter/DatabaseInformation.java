/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DatabaseInformation {
    private final Map<String, String> meta;

    public DatabaseInformation(Map<String, String> meta) {
        this.meta = new HashMap<String, String>(Objects.requireNonNull(meta));
    }

    public <T> T get(String key, StringConverter<T> converter) {
        return converter.convert(this.getString(key));
    }

    public <T> T get(String key, StringConverter<T> converter, T defaultValue) {
        return converter.convert(this.getString(key), defaultValue);
    }

    public String getString(String key) {
        return this.meta.get(key);
    }

    public String getString(String key, String defaultValue) {
        return this.get(key, new StringStringConverter(), defaultValue);
    }

    public int getInt(String key) {
        return this.get(key, new IntStringConverter());
    }

    public int getInt(String key, int defaultValue) {
        return this.get(key, new IntStringConverter(), defaultValue);
    }

    public boolean isEmpty() {
        return this.meta.isEmpty();
    }

    private static final class IntStringConverter
    extends AbstractStringConverter<Integer> {
        private IntStringConverter() {
        }

        @Override
        public Integer convert(String s) {
            return Integer.valueOf(s);
        }
    }

    private static final class StringStringConverter
    extends AbstractStringConverter<String> {
        private StringStringConverter() {
        }

        @Override
        public String convert(String s) {
            return s;
        }
    }

    public static abstract class AbstractStringConverter<T>
    implements StringConverter<T> {
        @Override
        public final T convert(String s, T defaultValue) {
            Object value = this.convert(s);
            return value != null ? value : defaultValue;
        }
    }

    public static interface StringConverter<T> {
        public T convert(String var1);

        public T convert(String var1, T var2);
    }
}

