/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.lang.BeanUtil;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.util.convert.ConversionException;
import com.twelvemonkeys.util.convert.NumberConverter;
import com.twelvemonkeys.util.convert.TypeMismathException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter
extends NumberConverter {
    @Override
    public Object toObject(String string, Class clazz, String string2) throws ConversionException {
        if (StringUtil.isEmpty(string)) {
            return null;
        }
        try {
            DateFormat dateFormat = string2 == null ? DateFormat.getDateTimeInstance() : this.getDateFormat(string2);
            Date date = StringUtil.toDate(string, dateFormat);
            if (clazz != Date.class) {
                try {
                    date = (Date)BeanUtil.createInstance(clazz, (Object)new Long(date.getTime()));
                }
                catch (ClassCastException classCastException) {
                    throw new TypeMismathException(clazz);
                }
                catch (InvocationTargetException invocationTargetException) {
                    throw new ConversionException(invocationTargetException);
                }
            }
            return date;
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
        if (!(object instanceof Date)) {
            throw new TypeMismathException(object.getClass());
        }
        try {
            if (StringUtil.isEmpty(string)) {
                return DateFormat.getDateTimeInstance().format(object);
            }
            DateFormat dateFormat = this.getDateFormat(string);
            return dateFormat.format(object);
        }
        catch (RuntimeException runtimeException) {
            throw new ConversionException(runtimeException);
        }
    }

    private DateFormat getDateFormat(String string) {
        return (DateFormat)this.getFormat(SimpleDateFormat.class, string, Locale.US);
    }
}

