/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.lang.BeanUtil;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.util.LRUHashMap;
import com.twelvemonkeys.util.convert.ConversionException;
import com.twelvemonkeys.util.convert.PropertyConverter;
import com.twelvemonkeys.util.convert.TypeMismathException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class NumberConverter
implements PropertyConverter {
    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.US);
    private static final NumberFormat sDefaultFormat = new DecimalFormat("#0.#", SYMBOLS);
    private static final Map<String, Format> sFormats = new LRUHashMap<String, Format>(50);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object toObject(String string, Class clazz, String string2) throws ConversionException {
        if (StringUtil.isEmpty(string)) {
            return null;
        }
        try {
            Number number;
            if (clazz.equals(BigInteger.class)) {
                return new BigInteger(string);
            }
            if (clazz.equals(BigDecimal.class)) {
                return new BigDecimal(string);
            }
            NumberFormat numberFormat = string2 == null ? sDefaultFormat : this.getNumberFormat(string2);
            NumberFormat numberFormat2 = numberFormat;
            synchronized (numberFormat2) {
                number = numberFormat.parse(string);
            }
            if (clazz == Integer.TYPE || clazz == Integer.class) {
                return number.intValue();
            }
            if (clazz == Long.TYPE || clazz == Long.class) {
                return number.longValue();
            }
            if (clazz == Double.TYPE || clazz == Double.class) {
                return number.doubleValue();
            }
            if (clazz == Float.TYPE || clazz == Float.class) {
                return Float.valueOf(number.floatValue());
            }
            if (clazz == Byte.TYPE || clazz == Byte.class) {
                return number.byteValue();
            }
            if (clazz == Short.TYPE || clazz == Short.class) {
                return number.shortValue();
            }
            return number;
        }
        catch (ParseException parseException) {
            throw new ConversionException(parseException);
        }
        catch (RuntimeException runtimeException) {
            throw new ConversionException(runtimeException);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String toString(Object object, String string) throws ConversionException {
        if (object == null) {
            return null;
        }
        if (!(object instanceof Number)) {
            throw new TypeMismathException(object.getClass());
        }
        try {
            NumberFormat numberFormat;
            if (StringUtil.isEmpty(string)) {
                return sDefaultFormat.format(object);
            }
            NumberFormat numberFormat2 = numberFormat = this.getNumberFormat(string);
            synchronized (numberFormat2) {
                return numberFormat.format(object);
            }
        }
        catch (RuntimeException runtimeException) {
            throw new ConversionException(runtimeException);
        }
    }

    private NumberFormat getNumberFormat(String string) {
        return (NumberFormat)this.getFormat(DecimalFormat.class, string, SYMBOLS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final Format getFormat(Class clazz, Object ... objectArray) {
        Map<String, Format> map = sFormats;
        synchronized (map) {
            String string = clazz.getName() + ":" + Arrays.toString(objectArray);
            Format format = sFormats.get(string);
            if (format == null) {
                try {
                    format = (Format)BeanUtil.createInstance(clazz, objectArray);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                    return null;
                }
                sFormats.put(string, format);
            }
            return format;
        }
    }
}

