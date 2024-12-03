/*
 * Decompiled with CFR 0.152.
 */
package org.ehcache.sizeof;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.Proxy;
import java.nio.charset.CodingErrorAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.namespace.QName;

enum FlyweightType {
    ENUM((Class)Enum.class){

        @Override
        boolean isShared(Object obj) {
            return true;
        }
    }
    ,
    CLASS((Class)Class.class){

        @Override
        boolean isShared(Object obj) {
            return true;
        }
    }
    ,
    BOOLEAN((Class)Boolean.class){

        @Override
        boolean isShared(Object obj) {
            return obj == Boolean.TRUE || obj == Boolean.FALSE;
        }
    }
    ,
    INTEGER((Class)Integer.class){

        @Override
        boolean isShared(Object obj) {
            int value = (Integer)obj;
            return value >= -128 && value <= 127 && obj == Integer.valueOf(value);
        }
    }
    ,
    SHORT((Class)Short.class){

        @Override
        boolean isShared(Object obj) {
            short value = (Short)obj;
            return value >= -128 && value <= 127 && obj == Short.valueOf(value);
        }
    }
    ,
    BYTE((Class)Byte.class){

        @Override
        boolean isShared(Object obj) {
            return obj == Byte.valueOf((Byte)obj);
        }
    }
    ,
    LONG((Class)Long.class){

        @Override
        boolean isShared(Object obj) {
            long value = (Long)obj;
            return value >= -128L && value <= 127L && obj == Long.valueOf(value);
        }
    }
    ,
    BIGINTEGER((Class)BigInteger.class){

        @Override
        boolean isShared(Object obj) {
            return obj == BigInteger.ZERO || obj == BigInteger.ONE || obj == BigInteger.TEN;
        }
    }
    ,
    BIGDECIMAL((Class)BigDecimal.class){

        @Override
        boolean isShared(Object obj) {
            return obj == BigDecimal.ZERO || obj == BigDecimal.ONE || obj == BigDecimal.TEN;
        }
    }
    ,
    MATHCONTEXT((Class)MathContext.class){

        @Override
        boolean isShared(Object obj) {
            return obj == MathContext.UNLIMITED || obj == MathContext.DECIMAL32 || obj == MathContext.DECIMAL64 || obj == MathContext.DECIMAL128;
        }
    }
    ,
    CHARACTER((Class)Character.class){

        @Override
        boolean isShared(Object obj) {
            return ((Character)obj).charValue() <= '\u007f' && obj == Character.valueOf(((Character)obj).charValue());
        }
    }
    ,
    LOCALE((Class)Locale.class){

        @Override
        boolean isShared(Object obj) {
            return obj instanceof Locale && GLOBAL_LOCALES.contains(obj);
        }
    }
    ,
    LOGGER((Class)Logger.class){

        @Override
        boolean isShared(Object obj) {
            return obj == Logger.global;
        }
    }
    ,
    PROXY((Class)Proxy.class){

        @Override
        boolean isShared(Object obj) {
            return obj == Proxy.NO_PROXY;
        }
    }
    ,
    CODINGERRORACTION((Class)CodingErrorAction.class){

        @Override
        boolean isShared(Object obj) {
            return true;
        }
    }
    ,
    DATATYPECONSTANTS_FIELD((Class)DatatypeConstants.Field.class){

        @Override
        boolean isShared(Object obj) {
            return true;
        }
    }
    ,
    QNAME((Class)QName.class){

        @Override
        boolean isShared(Object obj) {
            return obj == DatatypeConstants.DATETIME || obj == DatatypeConstants.TIME || obj == DatatypeConstants.DATE || obj == DatatypeConstants.GYEARMONTH || obj == DatatypeConstants.GMONTHDAY || obj == DatatypeConstants.GYEAR || obj == DatatypeConstants.GMONTH || obj == DatatypeConstants.GDAY || obj == DatatypeConstants.DURATION || obj == DatatypeConstants.DURATION_DAYTIME || obj == DatatypeConstants.DURATION_YEARMONTH;
        }
    }
    ,
    MISC((Class)Void.class){

        @Override
        boolean isShared(Object obj) {
            boolean emptyCollection = obj == Collections.EMPTY_SET || obj == Collections.EMPTY_LIST || obj == Collections.EMPTY_MAP;
            boolean systemStream = obj == System.in || obj == System.out || obj == System.err;
            return emptyCollection || systemStream || obj == String.CASE_INSENSITIVE_ORDER;
        }
    };

    private static final Map<Class<?>, FlyweightType> TYPE_MAPPINGS;
    private static final Set<Locale> GLOBAL_LOCALES;
    private final Class<?> clazz;

    private FlyweightType(Class<?> clazz) {
        this.clazz = clazz;
    }

    abstract boolean isShared(Object var1);

    static FlyweightType getFlyweightType(Class<?> aClazz) {
        if (aClazz.isEnum() || aClazz.getSuperclass() != null && aClazz.getSuperclass().isEnum()) {
            return ENUM;
        }
        FlyweightType flyweightType = TYPE_MAPPINGS.get(aClazz);
        return flyweightType != null ? flyweightType : MISC;
    }

    static {
        TYPE_MAPPINGS = new HashMap();
        for (FlyweightType type : FlyweightType.values()) {
            TYPE_MAPPINGS.put(type.clazz, type);
        }
        IdentityHashMap locales = new IdentityHashMap();
        for (Field f : Locale.class.getFields()) {
            int modifiers = f.getModifiers();
            if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Locale.class.equals(f.getType())) continue;
            try {
                locales.put((Locale)f.get(null), null);
            }
            catch (IllegalAccessException | IllegalArgumentException exception) {
                // empty catch block
            }
        }
        GLOBAL_LOCALES = locales.keySet();
    }
}

