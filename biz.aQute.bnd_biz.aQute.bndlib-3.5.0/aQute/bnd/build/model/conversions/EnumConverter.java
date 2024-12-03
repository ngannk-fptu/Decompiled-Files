/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.conversions.Converter;

public class EnumConverter<E extends Enum<E>>
implements Converter<E, String> {
    private final Class<E> enumType;
    private final E defaultValue;

    public static <E extends Enum<E>> EnumConverter<E> create(Class<E> enumType) {
        return new EnumConverter<Object>(enumType, null);
    }

    public static <E extends Enum<E>> EnumConverter<E> create(Class<E> enumType, E defaultValue) {
        return new EnumConverter<E>(enumType, defaultValue);
    }

    private EnumConverter(Class<E> enumType, E defaultValue) {
        this.enumType = enumType;
        this.defaultValue = defaultValue;
    }

    @Override
    public E convert(String input) throws IllegalArgumentException {
        if (input == null) {
            return this.defaultValue;
        }
        return Enum.valueOf(this.enumType, input);
    }

    @Override
    public E error(String msg) {
        return null;
    }
}

