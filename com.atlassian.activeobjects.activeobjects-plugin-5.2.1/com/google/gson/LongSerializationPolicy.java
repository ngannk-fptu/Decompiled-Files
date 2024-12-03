/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum LongSerializationPolicy {
    DEFAULT(new DefaultStrategy()),
    STRING(new StringStrategy());

    private final Strategy strategy;

    private LongSerializationPolicy(Strategy strategy) {
        this.strategy = strategy;
    }

    public JsonElement serialize(Long value) {
        return this.strategy.serialize(value);
    }

    private static class StringStrategy
    implements Strategy {
        private StringStrategy() {
        }

        public JsonElement serialize(Long value) {
            return new JsonPrimitive(String.valueOf(value));
        }
    }

    private static class DefaultStrategy
    implements Strategy {
        private DefaultStrategy() {
        }

        public JsonElement serialize(Long value) {
            return new JsonPrimitive(value);
        }
    }

    private static interface Strategy {
        public JsonElement serialize(Long var1);
    }
}

