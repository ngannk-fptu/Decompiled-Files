/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.HashingStrategy
 */
package io.netty.handler.codec;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.HashingStrategy;

public final class DefaultHeadersImpl<K, V>
extends DefaultHeaders<K, V, DefaultHeadersImpl<K, V>> {
    public DefaultHeadersImpl(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, DefaultHeaders.NameValidator<K> nameValidator) {
        super(nameHashingStrategy, valueConverter, nameValidator);
    }

    public DefaultHeadersImpl(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, DefaultHeaders.NameValidator<K> nameValidator, int arraySizeHint, DefaultHeaders.ValueValidator<V> valueValidator) {
        super(nameHashingStrategy, valueConverter, nameValidator, arraySizeHint, valueValidator);
    }
}

