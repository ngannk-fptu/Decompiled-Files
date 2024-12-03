/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Either
 */
package com.atlassian.plugin.webresource.bigpipe;

import com.google.common.base.Preconditions;
import io.atlassian.fugue.Either;

public class KeyedValue<K, V> {
    private final K key;
    private final Either<Throwable, V> value;

    private KeyedValue(K key, Either<Throwable, V> value) {
        this.key = Preconditions.checkNotNull(key);
        this.value = (Either)Preconditions.checkNotNull(value);
    }

    public static <K, T> KeyedValue<K, T> success(K key, T value) {
        return new KeyedValue(key, Either.right(value));
    }

    public static <K, T> KeyedValue<K, T> fail(K key, Throwable ex) {
        return new KeyedValue(key, Either.left((Object)ex));
    }

    public K key() {
        return this.key;
    }

    public Either<Throwable, V> value() {
        return this.value;
    }
}

