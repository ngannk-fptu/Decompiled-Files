/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.enums;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EnumToStringConverter<T extends Enum<T>>
extends AbstractSingleValueConverter {
    private final Class<T> enumType;
    private final Map<String, T> strings;
    private final EnumMap<T, String> values;

    public EnumToStringConverter(Class<T> type) {
        this(type, EnumToStringConverter.extractStringMap(type), null);
    }

    public EnumToStringConverter(Class<T> type, Map<String, T> strings) {
        this(type, strings, EnumToStringConverter.buildValueMap(type, strings));
    }

    private EnumToStringConverter(Class<T> type, Map<String, T> strings, EnumMap<T, String> values) {
        this.enumType = type;
        this.strings = strings;
        this.values = values;
    }

    private static <T extends Enum<T>> Map<String, T> extractStringMap(Class<T> type) {
        EnumToStringConverter.checkType(type);
        EnumSet<Enum> values = EnumSet.allOf(type);
        HashMap<String, Enum> strings = new HashMap<String, Enum>(values.size());
        for (Enum value : values) {
            if (strings.put(value.toString(), value) == null) continue;
            throw new InitializationException("Enum type " + type.getName() + " does not have unique string representations for its values");
        }
        return strings;
    }

    private static <T> void checkType(Class<T> type) {
        if (!Enum.class.isAssignableFrom(type) && type != Enum.class) {
            throw new InitializationException("Converter can only handle enum types");
        }
    }

    private static <T extends Enum<T>> EnumMap<T, String> buildValueMap(Class<T> type, Map<String, T> strings) {
        EnumMap<T, String> values = new EnumMap<T, String>(type);
        for (Map.Entry<String, T> entry : strings.entrySet()) {
            values.put((Enum)entry.getValue(), entry.getKey());
        }
        return values;
    }

    @Override
    public boolean canConvert(Class type) {
        return type != null && this.enumType.isAssignableFrom(type);
    }

    @Override
    public String toString(Object obj) {
        Enum value = (Enum)Enum.class.cast(obj);
        return this.values == null ? value.toString() : this.values.get(value);
    }

    @Override
    public Object fromString(String str) {
        if (str == null) {
            return null;
        }
        Enum result = (Enum)this.strings.get(str);
        if (result == null) {
            ConversionException exception = new ConversionException("Invalid string representation for enum type");
            exception.add("enum-type", this.enumType.getName());
            exception.add("enum-string", str);
            throw exception;
        }
        return result;
    }
}

