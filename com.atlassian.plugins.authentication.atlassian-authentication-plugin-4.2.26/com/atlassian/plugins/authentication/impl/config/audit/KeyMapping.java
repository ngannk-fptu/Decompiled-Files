/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.config.audit;

import java.util.function.Function;

public class KeyMapping<T> {
    private final String key;
    private final Function<T, String> propertyExtractor;
    private final boolean sanitize;

    private KeyMapping(String key, Function<T, String> propertyExtractor, boolean sanitize) {
        this.key = key;
        this.propertyExtractor = propertyExtractor;
        this.sanitize = sanitize;
    }

    public static <T> KeyMapping<T> mapping(String key, Function<T, String> propertyExtractor, boolean sanitize) {
        return new KeyMapping<T>(key, propertyExtractor, sanitize);
    }

    public static <T> KeyMapping<T> mapping(String key, Function<T, String> propertyExtractor) {
        return KeyMapping.mapping(key, propertyExtractor, false);
    }

    public String getKey() {
        return this.key;
    }

    public Function<T, String> getPropertyExtractor() {
        return this.propertyExtractor;
    }

    public boolean isSanitize() {
        return this.sanitize;
    }
}

