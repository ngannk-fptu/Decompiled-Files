/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;

public class ConvertUtils {
    @Deprecated
    public static boolean getDefaultBoolean() {
        return ConvertUtilsBean.getInstance().getDefaultBoolean();
    }

    @Deprecated
    public static void setDefaultBoolean(boolean newDefaultBoolean) {
        ConvertUtilsBean.getInstance().setDefaultBoolean(newDefaultBoolean);
    }

    @Deprecated
    public static byte getDefaultByte() {
        return ConvertUtilsBean.getInstance().getDefaultByte();
    }

    @Deprecated
    public static void setDefaultByte(byte newDefaultByte) {
        ConvertUtilsBean.getInstance().setDefaultByte(newDefaultByte);
    }

    @Deprecated
    public static char getDefaultCharacter() {
        return ConvertUtilsBean.getInstance().getDefaultCharacter();
    }

    @Deprecated
    public static void setDefaultCharacter(char newDefaultCharacter) {
        ConvertUtilsBean.getInstance().setDefaultCharacter(newDefaultCharacter);
    }

    @Deprecated
    public static double getDefaultDouble() {
        return ConvertUtilsBean.getInstance().getDefaultDouble();
    }

    @Deprecated
    public static void setDefaultDouble(double newDefaultDouble) {
        ConvertUtilsBean.getInstance().setDefaultDouble(newDefaultDouble);
    }

    @Deprecated
    public static float getDefaultFloat() {
        return ConvertUtilsBean.getInstance().getDefaultFloat();
    }

    @Deprecated
    public static void setDefaultFloat(float newDefaultFloat) {
        ConvertUtilsBean.getInstance().setDefaultFloat(newDefaultFloat);
    }

    @Deprecated
    public static int getDefaultInteger() {
        return ConvertUtilsBean.getInstance().getDefaultInteger();
    }

    @Deprecated
    public static void setDefaultInteger(int newDefaultInteger) {
        ConvertUtilsBean.getInstance().setDefaultInteger(newDefaultInteger);
    }

    @Deprecated
    public static long getDefaultLong() {
        return ConvertUtilsBean.getInstance().getDefaultLong();
    }

    @Deprecated
    public static void setDefaultLong(long newDefaultLong) {
        ConvertUtilsBean.getInstance().setDefaultLong(newDefaultLong);
    }

    @Deprecated
    public static short getDefaultShort() {
        return ConvertUtilsBean.getInstance().getDefaultShort();
    }

    @Deprecated
    public static void setDefaultShort(short newDefaultShort) {
        ConvertUtilsBean.getInstance().setDefaultShort(newDefaultShort);
    }

    public static String convert(Object value) {
        return ConvertUtilsBean.getInstance().convert(value);
    }

    public static Object convert(String value, Class<?> clazz) {
        return ConvertUtilsBean.getInstance().convert(value, clazz);
    }

    public static Object convert(String[] values, Class<?> clazz) {
        return ConvertUtilsBean.getInstance().convert(values, clazz);
    }

    public static Object convert(Object value, Class<?> targetType) {
        return ConvertUtilsBean.getInstance().convert(value, targetType);
    }

    public static void deregister() {
        ConvertUtilsBean.getInstance().deregister();
    }

    public static void deregister(Class<?> clazz) {
        ConvertUtilsBean.getInstance().deregister(clazz);
    }

    public static Converter lookup(Class<?> clazz) {
        return ConvertUtilsBean.getInstance().lookup(clazz);
    }

    public static Converter lookup(Class<?> sourceType, Class<?> targetType) {
        return ConvertUtilsBean.getInstance().lookup(sourceType, targetType);
    }

    public static void register(Converter converter, Class<?> clazz) {
        ConvertUtilsBean.getInstance().register(converter, clazz);
    }

    public static <T> Class<T> primitiveToWrapper(Class<T> type) {
        if (type == null || !type.isPrimitive()) {
            return type;
        }
        if (type == Integer.TYPE) {
            return Integer.class;
        }
        if (type == Double.TYPE) {
            return Double.class;
        }
        if (type == Long.TYPE) {
            return Long.class;
        }
        if (type == Boolean.TYPE) {
            return Boolean.class;
        }
        if (type == Float.TYPE) {
            return Float.class;
        }
        if (type == Short.TYPE) {
            return Short.class;
        }
        if (type == Byte.TYPE) {
            return Byte.class;
        }
        if (type == Character.TYPE) {
            return Character.class;
        }
        return type;
    }
}

