/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class StringConverter
extends DefaultTypeConverter {
    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        String result;
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            ArrayList<String> converted = new ArrayList<String>(length);
            for (int i = 0; i < length; ++i) {
                Object o = Array.get(value, i);
                converted.add(this.convertToString(this.getLocale(context), o));
            }
            result = StringUtils.join(converted, (String)", ");
        } else if (value.getClass().isAssignableFrom(Collection.class)) {
            Collection colValue = (Collection)value;
            ArrayList<String> converted = new ArrayList<String>(colValue.hashCode());
            for (Object o : colValue) {
                converted.add(this.convertToString(this.getLocale(context), o));
            }
            result = StringUtils.join(converted, (String)", ");
        } else if (value instanceof Date) {
            DateFormat df;
            if (value instanceof Time) {
                df = DateFormat.getTimeInstance(2, this.getLocale(context));
            } else if (value instanceof Timestamp) {
                SimpleDateFormat dfmt = (SimpleDateFormat)DateFormat.getDateTimeInstance(3, 2, this.getLocale(context));
                df = new SimpleDateFormat(dfmt.toPattern() + ".SSS");
            } else {
                df = DateFormat.getDateInstance(3, this.getLocale(context));
            }
            result = df.format(value);
        } else {
            result = this.convertToString(this.getLocale(context), value);
        }
        return result;
    }

    protected String convertToString(Locale locale, Object value) {
        if (Number.class.isInstance(value)) {
            NumberFormat format = NumberFormat.getNumberInstance(locale);
            format.setGroupingUsed(false);
            Object fixedValue = value;
            if (BigDecimal.class.isInstance(value) || Double.class.isInstance(value) || Float.class.isInstance(value)) {
                format.setMaximumFractionDigits(Integer.MAX_VALUE);
                if (Float.class.isInstance(value)) {
                    fixedValue = Double.valueOf(value.toString());
                }
            }
            return format.format(fixedValue);
        }
        return Objects.toString(value, null);
    }
}

