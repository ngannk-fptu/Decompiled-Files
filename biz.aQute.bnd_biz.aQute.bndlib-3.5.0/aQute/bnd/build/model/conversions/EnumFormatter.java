/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.conversions.Converter;

public class EnumFormatter<E extends Enum<E>>
implements Converter<String, E> {
    private final E defaultValue;

    public static <E extends Enum<E>> EnumFormatter<E> create(Class<E> enumType) {
        return new EnumFormatter<Object>(null);
    }

    public static <E extends Enum<E>> EnumFormatter<E> create(Class<E> enumType, E defaultValue) {
        return new EnumFormatter<E>(defaultValue);
    }

    private EnumFormatter(E defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String convert(E input) throws IllegalArgumentException {
        String result = input == this.defaultValue || input == null ? null : ((Enum)input).toString();
        return result;
    }

    @Override
    public String error(String msg) {
        return msg;
    }
}

