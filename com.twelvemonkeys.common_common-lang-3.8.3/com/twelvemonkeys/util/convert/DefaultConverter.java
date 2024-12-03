/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.lang.BeanUtil;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.util.convert.ConversionException;
import com.twelvemonkeys.util.convert.Converter;
import com.twelvemonkeys.util.convert.MissingTypeException;
import com.twelvemonkeys.util.convert.PropertyConverter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

public final class DefaultConverter
implements PropertyConverter {
    @Override
    public Object toObject(String string, Class clazz, String string2) throws ConversionException {
        if (string == null) {
            return null;
        }
        if (clazz == null) {
            throw new MissingTypeException();
        }
        if (clazz.isArray()) {
            return this.toArray(string, clazz, string2);
        }
        Class<?> clazz2 = this.unBoxType(clazz);
        try {
            Object object = BeanUtil.createInstance(clazz2, (Object)string);
            if (object == null && (object = BeanUtil.invokeStaticMethod(clazz2, "valueOf", (Object)string)) == null) {
                throw new ConversionException(String.format("Could not convert String to %1$s: No constructor %1$s(String) or static %1$s.valueOf(String) method found!", clazz2.getName()));
            }
            return object;
        }
        catch (InvocationTargetException invocationTargetException) {
            throw new ConversionException(invocationTargetException.getTargetException());
        }
        catch (ConversionException conversionException) {
            throw conversionException;
        }
        catch (RuntimeException runtimeException) {
            throw new ConversionException(runtimeException);
        }
    }

    private Object toArray(String string, Class clazz, String string2) {
        String[] stringArray = StringUtil.toStringArray(string, string2 != null ? string2 : ", \t\n\r\f");
        Class<?> clazz2 = clazz.getComponentType();
        if (clazz2 == String.class) {
            return stringArray;
        }
        Object object = Array.newInstance(clazz2, stringArray.length);
        try {
            for (int i = 0; i < stringArray.length; ++i) {
                Array.set(object, i, Converter.getInstance().toObject(stringArray[i], clazz2));
            }
        }
        catch (ConversionException conversionException) {
            if (string2 != null) {
                throw new ConversionException(String.format("%s for string \"%s\" with format \"%s\"", conversionException.getMessage(), string, string2), conversionException);
            }
            throw new ConversionException(String.format("%s for string \"%s\"", conversionException.getMessage(), string), conversionException);
        }
        return object;
    }

    @Override
    public String toString(Object object, String string) throws ConversionException {
        try {
            return object == null ? null : (object.getClass().isArray() ? this.arrayToString(this.toObjectArray(object), string) : object.toString());
        }
        catch (RuntimeException runtimeException) {
            throw new ConversionException(runtimeException);
        }
    }

    private String arrayToString(Object[] objectArray, String string) {
        return string == null ? StringUtil.toCSVString(objectArray) : StringUtil.toCSVString(objectArray, string);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Object[] toObjectArray(Object object) {
        Object[] objectArray;
        Class<?> clazz = object.getClass().getComponentType();
        if (!clazz.isPrimitive()) return (Object[])object;
        if (Integer.TYPE == clazz) {
            objectArray = new Integer[Array.getLength(object)];
            int n = 0;
            while (n < objectArray.length) {
                Array.set(objectArray, n, Array.get(object, n));
                ++n;
            }
            return objectArray;
        }
        if (Short.TYPE == clazz) {
            objectArray = new Short[Array.getLength(object)];
            int n = 0;
            while (n < objectArray.length) {
                Array.set(objectArray, n, Array.get(object, n));
                ++n;
            }
            return objectArray;
        }
        if (Long.TYPE == clazz) {
            objectArray = new Long[Array.getLength(object)];
            int n = 0;
            while (n < objectArray.length) {
                Array.set(objectArray, n, Array.get(object, n));
                ++n;
            }
            return objectArray;
        }
        if (Float.TYPE == clazz) {
            objectArray = new Float[Array.getLength(object)];
            int n = 0;
            while (n < objectArray.length) {
                Array.set(objectArray, n, Array.get(object, n));
                ++n;
            }
            return objectArray;
        }
        if (Double.TYPE == clazz) {
            objectArray = new Double[Array.getLength(object)];
            int n = 0;
            while (n < objectArray.length) {
                Array.set(objectArray, n, Array.get(object, n));
                ++n;
            }
            return objectArray;
        }
        if (Boolean.TYPE == clazz) {
            objectArray = new Boolean[Array.getLength(object)];
            int n = 0;
            while (n < objectArray.length) {
                Array.set(objectArray, n, Array.get(object, n));
                ++n;
            }
            return objectArray;
        }
        if (Byte.TYPE == clazz) {
            objectArray = new Byte[Array.getLength(object)];
            int n = 0;
            while (n < objectArray.length) {
                Array.set(objectArray, n, Array.get(object, n));
                ++n;
            }
            return objectArray;
        }
        if (Character.TYPE != clazz) throw new IllegalArgumentException("Unknown type " + clazz);
        objectArray = new Character[Array.getLength(object)];
        int n = 0;
        while (n < objectArray.length) {
            Array.set(objectArray, n, Array.get(object, n));
            ++n;
        }
        return objectArray;
    }

    private Class<?> unBoxType(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == Boolean.TYPE) {
                return Boolean.class;
            }
            if (clazz == Byte.TYPE) {
                return Byte.class;
            }
            if (clazz == Character.TYPE) {
                return Character.class;
            }
            if (clazz == Short.TYPE) {
                return Short.class;
            }
            if (clazz == Integer.TYPE) {
                return Integer.class;
            }
            if (clazz == Float.TYPE) {
                return Float.class;
            }
            if (clazz == Long.TYPE) {
                return Long.class;
            }
            if (clazz == Double.TYPE) {
                return Double.class;
            }
            throw new IllegalArgumentException("Unknown type: " + clazz);
        }
        return clazz;
    }
}

