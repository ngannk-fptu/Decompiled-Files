/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.common.docs;

import io.micrometer.common.KeyValue;
import java.util.Arrays;
import java.util.function.Predicate;

public interface KeyName {
    public static KeyName[] merge(KeyName[] ... keyNames) {
        return (KeyName[])Arrays.stream(keyNames).flatMap(Arrays::stream).toArray(KeyName[]::new);
    }

    default public KeyValue withValue(String value) {
        return KeyValue.of(this, value);
    }

    default public KeyValue withValue(String value, Predicate<Object> validator) {
        return KeyValue.of(this, value, validator);
    }

    public String asString();

    default public boolean isRequired() {
        return true;
    }
}

