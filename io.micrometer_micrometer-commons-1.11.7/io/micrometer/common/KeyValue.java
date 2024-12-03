/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.common;

import io.micrometer.common.ImmutableKeyValue;
import io.micrometer.common.ValidatedKeyValue;
import io.micrometer.common.docs.KeyName;
import java.util.function.Function;
import java.util.function.Predicate;

public interface KeyValue
extends Comparable<KeyValue> {
    public static final String NONE_VALUE = "none";

    public String getKey();

    public String getValue();

    public static KeyValue of(String key, String value) {
        return new ImmutableKeyValue(key, value);
    }

    public static KeyValue of(KeyName keyName, String value) {
        return KeyValue.of(keyName.asString(), value);
    }

    public static <E> KeyValue of(E element, Function<E, String> keyExtractor, Function<E, String> valueExtractor) {
        return KeyValue.of(keyExtractor.apply(element), valueExtractor.apply(element));
    }

    public static <T> KeyValue of(String key, T value, Predicate<? super T> validator) {
        return new ValidatedKeyValue<T>(key, value, validator);
    }

    public static <T> KeyValue of(KeyName keyName, T value, Predicate<? super T> validator) {
        return KeyValue.of(keyName.asString(), value, validator);
    }

    @Override
    default public int compareTo(KeyValue o) {
        return this.getKey().compareTo(o.getKey());
    }
}

