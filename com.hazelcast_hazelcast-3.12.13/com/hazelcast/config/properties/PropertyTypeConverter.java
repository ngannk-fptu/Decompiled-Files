/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.properties;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.util.Preconditions;

public enum PropertyTypeConverter implements TypeConverter
{
    STRING{

        @Override
        public Comparable convert(Comparable value) {
            Preconditions.checkNotNull(value, "The value to convert cannot be null");
            return value.toString();
        }
    }
    ,
    SHORT{

        @Override
        public Comparable convert(Comparable value) {
            if (value instanceof String) {
                return Short.valueOf(Short.parseShort((String)((Object)value)));
            }
            throw new IllegalArgumentException("Cannot convert to short");
        }
    }
    ,
    INTEGER{

        @Override
        public Comparable convert(Comparable value) {
            if (value instanceof String) {
                return Integer.valueOf(Integer.parseInt((String)((Object)value)));
            }
            throw new IllegalArgumentException("Cannot convert to integer");
        }
    }
    ,
    LONG{

        @Override
        public Comparable convert(Comparable value) {
            if (value instanceof String) {
                return Long.valueOf(Long.parseLong((String)((Object)value)));
            }
            throw new IllegalArgumentException("Cannot convert to long");
        }
    }
    ,
    FLOAT{

        @Override
        public Comparable convert(Comparable value) {
            if (value instanceof String) {
                return Float.valueOf(Float.parseFloat((String)((Object)value)));
            }
            throw new IllegalArgumentException("Cannot convert to float");
        }
    }
    ,
    DOUBLE{

        @Override
        public Comparable convert(Comparable value) {
            if (value instanceof String) {
                return Double.valueOf(Double.parseDouble((String)((Object)value)));
            }
            throw new IllegalArgumentException("Cannot convert to double");
        }
    }
    ,
    BOOLEAN{

        @Override
        public Comparable convert(Comparable value) {
            if (value instanceof Boolean) {
                return value;
            }
            if (value instanceof String) {
                return Boolean.valueOf(Boolean.parseBoolean((String)((Object)value)));
            }
            throw new IllegalArgumentException("Cannot convert to boolean");
        }
    };

}

