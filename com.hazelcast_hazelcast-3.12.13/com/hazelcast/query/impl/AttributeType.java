/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.query.impl.TypeConverters;

public enum AttributeType {
    DOUBLE(TypeConverters.DOUBLE_CONVERTER),
    LONG(TypeConverters.LONG_CONVERTER),
    SHORT(TypeConverters.SHORT_CONVERTER),
    BOOLEAN(TypeConverters.BOOLEAN_CONVERTER),
    BYTE(TypeConverters.BYTE_CONVERTER),
    STRING(TypeConverters.STRING_CONVERTER),
    FLOAT(TypeConverters.FLOAT_CONVERTER),
    CHAR(TypeConverters.CHAR_CONVERTER),
    INTEGER(TypeConverters.INTEGER_CONVERTER),
    ENUM(TypeConverters.ENUM_CONVERTER),
    BIG_INTEGER(TypeConverters.BIG_INTEGER_CONVERTER),
    BIG_DECIMAL(TypeConverters.BIG_DECIMAL_CONVERTER),
    SQL_TIMESTAMP(TypeConverters.SQL_TIMESTAMP_CONVERTER),
    SQL_DATE(TypeConverters.SQL_DATE_CONVERTER),
    DATE(TypeConverters.DATE_CONVERTER),
    UUID(TypeConverters.UUID_CONVERTER),
    PORTABLE(TypeConverters.PORTABLE_CONVERTER);

    private final TypeConverter converter;

    private AttributeType(TypeConverter converter) {
        this.converter = converter;
    }

    public TypeConverter getConverter() {
        return this.converter;
    }
}

