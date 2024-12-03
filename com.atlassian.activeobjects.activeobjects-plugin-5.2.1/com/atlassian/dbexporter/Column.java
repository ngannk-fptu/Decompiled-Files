/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter;

import java.util.Objects;

public final class Column {
    private final String name;
    private final int sqlType;
    private final Boolean primaryKey;
    private final Boolean autoIncrement;
    private final Integer precision;
    private final Integer scale;

    public Column(String name, int sqlType, Boolean pk, Boolean autoIncrement, Integer precision, Integer scale) {
        this.name = Objects.requireNonNull(name);
        this.sqlType = sqlType;
        this.primaryKey = pk;
        this.autoIncrement = autoIncrement;
        this.precision = precision;
        this.scale = scale;
    }

    public String getName() {
        return this.name;
    }

    public Boolean isPrimaryKey() {
        return this.primaryKey;
    }

    public Boolean isAutoIncrement() {
        return this.autoIncrement;
    }

    public int getSqlType() {
        return this.sqlType;
    }

    public Integer getPrecision() {
        return this.precision;
    }

    public Integer getScale() {
        return this.scale;
    }
}

