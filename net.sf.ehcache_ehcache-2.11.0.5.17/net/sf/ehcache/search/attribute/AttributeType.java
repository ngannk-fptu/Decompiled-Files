/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.attribute;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.search.SearchException;

public enum AttributeType {
    BOOLEAN{

        @Override
        public void validateValue(String name, Object value) {
            if (!(value instanceof Boolean)) {
                throw new SearchException("Expecting a Boolean value for attribute [" + name + "] but was " + AttributeType.type(value));
            }
        }
    }
    ,
    BYTE{

        @Override
        public void validateValue(String name, Object value) {
            if (!(value instanceof Byte)) {
                throw new SearchException("Expecting a Byte value for attribute [" + name + "] but was " + AttributeType.type(value));
            }
        }
    }
    ,
    CHAR{

        @Override
        public void validateValue(String name, Object value) {
            if (!(value instanceof Character)) {
                throw new SearchException("Expecting a Character value for attribute [" + name + "] but was " + AttributeType.type(value));
            }
        }
    }
    ,
    DOUBLE{

        @Override
        public void validateValue(String name, Object value) {
            if (!(value instanceof Double)) {
                throw new SearchException("Expecting a Double value for attribute [" + name + "] but was " + AttributeType.type(value));
            }
        }
    }
    ,
    FLOAT{

        @Override
        public void validateValue(String name, Object value) {
            if (!(value instanceof Float)) {
                throw new SearchException("Expecting a Float value for attribute [" + name + "] but was " + AttributeType.type(value));
            }
        }
    }
    ,
    INT{

        @Override
        public void validateValue(String name, Object value) {
            if (!(value instanceof Integer)) {
                throw new SearchException("Expecting an Integer value for attribute [" + name + "] but was " + AttributeType.type(value));
            }
        }
    }
    ,
    LONG{

        @Override
        public void validateValue(String name, Object value) {
            if (!(value instanceof Long)) {
                throw new SearchException("Expecting a Long value for attribute [" + name + "] but was " + AttributeType.type(value));
            }
        }
    }
    ,
    SHORT{

        @Override
        public void validateValue(String name, Object value) {
            if (!(value instanceof Short)) {
                throw new SearchException("Expecting a Short value for attribute [" + name + "] but was " + AttributeType.type(value));
            }
        }
    }
    ,
    DATE{

        @Override
        public void validateValue(String name, Object value) {
            if (value == null || value.getClass() != Date.class) {
                throw new SearchException("Expecting a java.util.Date value for attribute [" + name + "] but was " + AttributeType.type(value));
            }
        }
    }
    ,
    SQL_DATE{

        @Override
        public void validateValue(String name, Object value) {
            if (value == null || value.getClass() != java.sql.Date.class) {
                throw new SearchException("Expecting a java.sql.Date value for attribute [" + name + "] but was " + AttributeType.type(value));
            }
        }
    }
    ,
    ENUM{

        @Override
        public void validateValue(String name, Object value) {
            if (!(value instanceof Enum)) {
                throw new SearchException("Expecting a enum value for attribute [" + name + "] but was " + AttributeType.type(value));
            }
        }
    }
    ,
    STRING{

        @Override
        public void validateValue(String name, Object value) {
            if (!(value instanceof String)) {
                throw new SearchException("Expecting a String value for attribute [" + name + "] but was " + AttributeType.type(value));
            }
        }
    };

    private static final Map<Class<?>, AttributeType> MAPPINGS;

    public static AttributeType typeFor(String name, Object value) throws SearchException {
        if (name == null) {
            throw new NullPointerException("null name");
        }
        if (value == null) {
            throw new NullPointerException("null value");
        }
        AttributeType type = AttributeType.typeForOrNull(value);
        if (type != null) {
            return type;
        }
        throw new SearchException("Unsupported type for search attribute [" + name + "]: " + value.getClass().getName());
    }

    private static AttributeType typeForOrNull(Object value) {
        AttributeType type = MAPPINGS.get(value.getClass());
        if (type != null) {
            return type;
        }
        if (value instanceof Enum) {
            return ENUM;
        }
        return null;
    }

    public static AttributeType typeFor(Class<?> c) {
        if (c == null) {
            throw new NullPointerException("null class");
        }
        AttributeType type = MAPPINGS.get(c);
        if (type != null) {
            return type;
        }
        return c.isEnum() ? ENUM : null;
    }

    public static boolean isSupportedType(Object value) {
        if (value == null) {
            return true;
        }
        return AttributeType.typeForOrNull(value) != null;
    }

    public abstract void validateValue(String var1, Object var2) throws SearchException;

    public boolean isComparable() {
        return true;
    }

    private static String type(Object value) {
        if (value == null) {
            return "null";
        }
        return value.getClass().getName();
    }

    public static Set<Class<?>> getSupportedJavaTypes() {
        return Collections.unmodifiableSet(MAPPINGS.keySet());
    }

    static {
        MAPPINGS = new HashMap();
        MAPPINGS.put(Boolean.class, BOOLEAN);
        MAPPINGS.put(Byte.class, BYTE);
        MAPPINGS.put(Character.class, CHAR);
        MAPPINGS.put(Double.class, DOUBLE);
        MAPPINGS.put(Float.class, FLOAT);
        MAPPINGS.put(Integer.class, INT);
        MAPPINGS.put(Long.class, LONG);
        MAPPINGS.put(Short.class, SHORT);
        MAPPINGS.put(String.class, STRING);
        MAPPINGS.put(Date.class, DATE);
        MAPPINGS.put(java.sql.Date.class, SQL_DATE);
        MAPPINGS.put(Character.TYPE, CHAR);
        MAPPINGS.put(Integer.TYPE, INT);
        MAPPINGS.put(Long.TYPE, LONG);
        MAPPINGS.put(Byte.TYPE, BYTE);
        MAPPINGS.put(Boolean.TYPE, BOOLEAN);
        MAPPINGS.put(Float.TYPE, FLOAT);
        MAPPINGS.put(Double.TYPE, DOUBLE);
        MAPPINGS.put(Short.TYPE, SHORT);
    }
}

