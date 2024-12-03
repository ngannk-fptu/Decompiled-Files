/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.util.Time;
import com.twelvemonkeys.util.convert.ConversionException;
import com.twelvemonkeys.util.convert.ConverterImpl;
import com.twelvemonkeys.util.convert.DateConverter;
import com.twelvemonkeys.util.convert.DefaultConverter;
import com.twelvemonkeys.util.convert.NumberConverter;
import com.twelvemonkeys.util.convert.PropertyConverter;
import com.twelvemonkeys.util.convert.TimeConverter;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

public abstract class Converter
implements PropertyConverter {
    protected static final Converter sInstance = new ConverterImpl();
    protected final Map<Class, PropertyConverter> converters = new Hashtable<Class, PropertyConverter>();

    protected Converter() {
    }

    public static Converter getInstance() {
        return sInstance;
    }

    public static void registerConverter(Class<?> clazz, PropertyConverter propertyConverter) {
        Converter.getInstance().converters.put(clazz, propertyConverter);
    }

    public static void unregisterConverter(Class<?> clazz) {
        Converter.getInstance().converters.remove(clazz);
    }

    public Object toObject(String string, Class clazz) throws ConversionException {
        return this.toObject(string, clazz, null);
    }

    @Override
    public abstract Object toObject(String var1, Class var2, String var3) throws ConversionException;

    public String toString(Object object) throws ConversionException {
        return this.toString(object, null);
    }

    @Override
    public abstract String toString(Object var1, String var2) throws ConversionException;

    static {
        DefaultConverter defaultConverter = new DefaultConverter();
        Converter.registerConverter(Object.class, defaultConverter);
        Converter.registerConverter(Boolean.TYPE, defaultConverter);
        NumberConverter numberConverter = new NumberConverter();
        Converter.registerConverter(Number.class, numberConverter);
        Converter.registerConverter(Byte.TYPE, numberConverter);
        Converter.registerConverter(Double.TYPE, numberConverter);
        Converter.registerConverter(Float.TYPE, numberConverter);
        Converter.registerConverter(Integer.TYPE, numberConverter);
        Converter.registerConverter(Long.TYPE, numberConverter);
        Converter.registerConverter(Short.TYPE, numberConverter);
        Converter.registerConverter(Date.class, new DateConverter());
        Converter.registerConverter(Time.class, new TimeConverter());
    }
}

