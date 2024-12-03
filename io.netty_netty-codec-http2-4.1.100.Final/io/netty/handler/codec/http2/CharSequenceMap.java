/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.DefaultHeaders
 *  io.netty.handler.codec.DefaultHeaders$NameValidator
 *  io.netty.handler.codec.UnsupportedValueConverter
 *  io.netty.handler.codec.ValueConverter
 *  io.netty.util.AsciiString
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.UnsupportedValueConverter;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;

public final class CharSequenceMap<V>
extends DefaultHeaders<CharSequence, V, CharSequenceMap<V>> {
    public CharSequenceMap() {
        this(true);
    }

    public CharSequenceMap(boolean caseSensitive) {
        this(caseSensitive, (ValueConverter<V>)UnsupportedValueConverter.instance());
    }

    public CharSequenceMap(boolean caseSensitive, ValueConverter<V> valueConverter) {
        super(caseSensitive ? AsciiString.CASE_SENSITIVE_HASHER : AsciiString.CASE_INSENSITIVE_HASHER, valueConverter);
    }

    public CharSequenceMap(boolean caseSensitive, ValueConverter<V> valueConverter, int arraySizeHint) {
        super(caseSensitive ? AsciiString.CASE_SENSITIVE_HASHER : AsciiString.CASE_INSENSITIVE_HASHER, valueConverter, DefaultHeaders.NameValidator.NOT_NULL, arraySizeHint);
    }
}

