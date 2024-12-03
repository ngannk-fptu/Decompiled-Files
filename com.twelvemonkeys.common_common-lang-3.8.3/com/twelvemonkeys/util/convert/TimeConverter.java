/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.util.Time;
import com.twelvemonkeys.util.TimeFormat;
import com.twelvemonkeys.util.convert.ConversionException;
import com.twelvemonkeys.util.convert.NumberConverter;
import com.twelvemonkeys.util.convert.TypeMismathException;

public class TimeConverter
extends NumberConverter {
    @Override
    public Object toObject(String string, Class clazz, String string2) throws ConversionException {
        if (StringUtil.isEmpty(string)) {
            return null;
        }
        try {
            TimeFormat timeFormat = string2 == null ? TimeFormat.getInstance() : this.getTimeFormat(string2);
            return timeFormat.parse(string);
        }
        catch (RuntimeException runtimeException) {
            throw new ConversionException(runtimeException);
        }
    }

    @Override
    public String toString(Object object, String string) throws ConversionException {
        if (object == null) {
            return null;
        }
        if (!(object instanceof Time)) {
            throw new TypeMismathException(object.getClass());
        }
        try {
            if (StringUtil.isEmpty(string)) {
                return object.toString();
            }
            TimeFormat timeFormat = this.getTimeFormat(string);
            return timeFormat.format((Time)object);
        }
        catch (RuntimeException runtimeException) {
            throw new ConversionException(runtimeException);
        }
    }

    private TimeFormat getTimeFormat(String string) {
        return (TimeFormat)this.getFormat(TimeFormat.class, string);
    }
}

