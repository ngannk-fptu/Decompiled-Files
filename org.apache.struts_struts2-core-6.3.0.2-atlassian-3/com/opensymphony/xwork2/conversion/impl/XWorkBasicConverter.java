/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.impl.ArrayConverter;
import com.opensymphony.xwork2.conversion.impl.CollectionConverter;
import com.opensymphony.xwork2.conversion.impl.DateConverter;
import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;
import com.opensymphony.xwork2.conversion.impl.NumberConverter;
import com.opensymphony.xwork2.conversion.impl.StringConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.apache.struts2.conversion.TypeConversionException;

public class XWorkBasicConverter
extends DefaultTypeConverter {
    private Container container;

    @Override
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Object convertValue(Map<String, Object> context, Object o, Member member, String propertyName, Object value, Class toType) {
        Object result = null;
        if (value == null || toType.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (toType == String.class) {
            result = this.doConvertToString(context, value);
        } else if (toType == Boolean.TYPE) {
            result = this.doConvertToBoolean(value);
        } else if (toType == Boolean.class) {
            result = this.doConvertToBoolean(value);
        } else if (toType.isArray()) {
            result = this.doConvertToArray(context, o, member, propertyName, value, toType);
        } else if (Date.class.isAssignableFrom(toType)) {
            result = this.doConvertToDate(context, value, toType);
        } else if (LocalDateTime.class.isAssignableFrom(toType)) {
            result = this.doConvertToDate(context, value, toType);
        } else if (Calendar.class.isAssignableFrom(toType)) {
            result = this.doConvertToCalendar(context, value);
        } else if (Collection.class.isAssignableFrom(toType)) {
            result = this.doConvertToCollection(context, o, member, propertyName, value, toType);
        } else if (toType == Character.class) {
            result = this.doConvertToCharacter(value);
        } else if (toType == Character.TYPE) {
            result = this.doConvertToCharacter(value);
        } else if (Number.class.isAssignableFrom(toType) || toType.isPrimitive()) {
            result = this.doConvertToNumber(context, value, toType);
        } else if (toType == Class.class) {
            result = this.doConvertToClass(value);
        }
        if (result == null) {
            if (value instanceof Object[]) {
                Object[] array = (Object[])value;
                value = array.length >= 1 ? array[0] : null;
                result = this.convertValue(context, o, member, propertyName, value, toType);
            } else if (!"".equals(value)) {
                result = super.convertValue(context, value, toType);
            }
            if (result == null && value != null && !"".equals(value)) {
                throw new TypeConversionException("Cannot create type " + toType + " from value " + value);
            }
        }
        return result;
    }

    private Object doConvertToCalendar(Map<String, Object> context, Object value) {
        Calendar result = null;
        Date dateResult = (Date)this.doConvertToDate(context, value, Date.class);
        if (dateResult != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateResult);
            result = calendar;
        }
        return result;
    }

    private Object doConvertToCharacter(Object value) {
        if (value instanceof String) {
            String cStr = (String)value;
            return cStr.length() > 0 ? Character.valueOf(cStr.charAt(0)) : null;
        }
        return null;
    }

    private Object doConvertToBoolean(Object value) {
        if (value instanceof String) {
            String bStr = (String)value;
            return Boolean.valueOf(bStr);
        }
        return null;
    }

    private Class doConvertToClass(Object value) {
        Class<?> clazz = null;
        if (value != null && value instanceof String && ((String)value).length() > 0) {
            try {
                clazz = Class.forName((String)value);
            }
            catch (ClassNotFoundException e) {
                throw new TypeConversionException(e.getLocalizedMessage(), e);
            }
        }
        return clazz;
    }

    private Object doConvertToCollection(Map<String, Object> context, Object o, Member member, String prop, Object value, Class toType) {
        TypeConverter converter = this.container.getInstance(CollectionConverter.class);
        if (converter == null) {
            throw new TypeConversionException("TypeConverter with name [#0] must be registered first! Converter: struts.converter.collection");
        }
        return converter.convertValue(context, o, member, prop, value, toType);
    }

    private Object doConvertToArray(Map<String, Object> context, Object o, Member member, String prop, Object value, Class toType) {
        TypeConverter converter = this.container.getInstance(ArrayConverter.class);
        if (converter == null) {
            throw new TypeConversionException("TypeConverter with name [#0] must be registered first! Converter: struts.converter.array");
        }
        return converter.convertValue(context, o, member, prop, value, toType);
    }

    private Object doConvertToDate(Map<String, Object> context, Object value, Class toType) {
        TypeConverter converter = this.container.getInstance(DateConverter.class);
        if (converter == null) {
            throw new TypeConversionException("TypeConverter with name [#0] must be registered first! Converter: struts.converter.date");
        }
        return converter.convertValue(context, null, null, null, value, toType);
    }

    private Object doConvertToNumber(Map<String, Object> context, Object value, Class toType) {
        TypeConverter converter = this.container.getInstance(NumberConverter.class);
        if (converter == null) {
            throw new TypeConversionException("TypeConverter with name [#0] must be registered first! Converter: struts.converter.number");
        }
        return converter.convertValue(context, null, null, null, value, toType);
    }

    private Object doConvertToString(Map<String, Object> context, Object value) {
        TypeConverter converter = this.container.getInstance(StringConverter.class);
        if (converter == null) {
            throw new TypeConversionException("TypeConverter with name [#0] must be registered first! Converter: struts.converter.string");
        }
        return converter.convertValue(context, null, null, null, value, null);
    }
}

