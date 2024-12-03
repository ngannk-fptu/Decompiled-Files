/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.TypeInfo;

enum StreamType {
    NONE(JDBCType.UNKNOWN, "None"),
    ASCII(JDBCType.LONGVARCHAR, "AsciiStream"),
    BINARY(JDBCType.LONGVARBINARY, "BinaryStream"),
    CHARACTER(JDBCType.LONGVARCHAR, "CharacterStream"),
    NCHARACTER(JDBCType.LONGNVARCHAR, "NCharacterStream"),
    SQLXML(JDBCType.SQLXML, "SQLXML");

    private final JDBCType jdbcType;
    private final String name;

    JDBCType getJDBCType() {
        return this.jdbcType;
    }

    private StreamType(JDBCType jdbcType, String name) {
        this.jdbcType = jdbcType;
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    boolean convertsFrom(TypeInfo typeInfo) {
        if (ASCII == this) {
            if (SSType.XML == typeInfo.getSSType()) {
                return false;
            }
            if (null != typeInfo.getSQLCollation() && !typeInfo.getSQLCollation().supportsAsciiConversion()) {
                return false;
            }
        }
        return typeInfo.getSSType().convertsTo(this.jdbcType);
    }

    boolean convertsTo(TypeInfo typeInfo) {
        if (ASCII == this) {
            if (SSType.XML == typeInfo.getSSType()) {
                return false;
            }
            if (null != typeInfo.getSQLCollation() && !typeInfo.getSQLCollation().supportsAsciiConversion()) {
                return false;
            }
        }
        return this.jdbcType.convertsTo(typeInfo.getSSType());
    }
}

