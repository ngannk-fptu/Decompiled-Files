/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.conversions.Converter;

public class NoopConverter<T>
implements Converter<T, T> {
    @Override
    public T convert(T input) throws IllegalArgumentException {
        return input;
    }

    @Override
    public T error(String msg) {
        return null;
    }
}

