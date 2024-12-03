/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.types;

import java.util.Objects;

public class SchemaProperties {
    private final String sqlTypeName;
    private final Integer overrideJdbcWriteType;
    private final boolean precisionAllowed;
    private final boolean scaleAllowed;
    private final boolean stringLengthAllowed;
    private final boolean defaultValueAllowed;

    private SchemaProperties(String sqlTypeName, Integer overrideJdbcWriteType, boolean precisionAllowed, boolean scaleAllowed, boolean stringLengthAllowed, boolean defaultValueAllowed) {
        this.sqlTypeName = sqlTypeName;
        this.overrideJdbcWriteType = overrideJdbcWriteType;
        this.precisionAllowed = precisionAllowed;
        this.scaleAllowed = scaleAllowed;
        this.stringLengthAllowed = stringLengthAllowed;
        this.defaultValueAllowed = defaultValueAllowed;
    }

    public static SchemaProperties schemaType(String sqlTypeName) {
        return new SchemaProperties(Objects.requireNonNull(sqlTypeName, "sqlTypeName can't be null"), null, false, false, false, true);
    }

    public SchemaProperties jdbcWriteType(int jdbcWriteType) {
        return new SchemaProperties(this.sqlTypeName, jdbcWriteType, this.precisionAllowed, this.scaleAllowed, this.stringLengthAllowed, this.defaultValueAllowed);
    }

    public SchemaProperties precisionAllowed(boolean precisionAllowed) {
        return new SchemaProperties(this.sqlTypeName, this.overrideJdbcWriteType, precisionAllowed, this.scaleAllowed, this.stringLengthAllowed, this.defaultValueAllowed);
    }

    public SchemaProperties scaleAllowed(boolean scaleAllowed) {
        return new SchemaProperties(this.sqlTypeName, this.overrideJdbcWriteType, this.precisionAllowed, scaleAllowed, this.stringLengthAllowed, this.defaultValueAllowed);
    }

    public SchemaProperties stringLengthAllowed(boolean stringLengthAllowed) {
        return new SchemaProperties(this.sqlTypeName, this.overrideJdbcWriteType, this.precisionAllowed, this.scaleAllowed, stringLengthAllowed, this.defaultValueAllowed);
    }

    public SchemaProperties defaultValueAllowed(boolean defaultValueAllowed) {
        return new SchemaProperties(this.sqlTypeName, this.overrideJdbcWriteType, this.precisionAllowed, this.scaleAllowed, this.stringLengthAllowed, defaultValueAllowed);
    }

    public String getSqlTypeName() {
        return this.sqlTypeName;
    }

    public Integer getOverrideJdbcWriteType() {
        return this.overrideJdbcWriteType;
    }

    public boolean hasOverrideJdbcWriteType() {
        return this.overrideJdbcWriteType != null;
    }

    public boolean isPrecisionAllowed() {
        return this.precisionAllowed;
    }

    public boolean isScaleAllowed() {
        return this.scaleAllowed;
    }

    public boolean isStringLengthAllowed() {
        return this.stringLengthAllowed;
    }

    public boolean isDefaultValueAllowed() {
        return this.defaultValueAllowed;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SchemaProperties)) {
            return false;
        }
        SchemaProperties that = (SchemaProperties)o;
        if (this.defaultValueAllowed != that.defaultValueAllowed) {
            return false;
        }
        if (this.precisionAllowed != that.precisionAllowed) {
            return false;
        }
        if (this.scaleAllowed != that.scaleAllowed) {
            return false;
        }
        if (this.stringLengthAllowed != that.stringLengthAllowed) {
            return false;
        }
        if (this.overrideJdbcWriteType != null ? !this.overrideJdbcWriteType.equals(that.overrideJdbcWriteType) : that.overrideJdbcWriteType != null) {
            return false;
        }
        return !(this.sqlTypeName != null ? !this.sqlTypeName.equals(that.sqlTypeName) : that.sqlTypeName != null);
    }

    public int hashCode() {
        int result = this.sqlTypeName != null ? this.sqlTypeName.hashCode() : 0;
        result = 31 * result + (this.overrideJdbcWriteType != null ? this.overrideJdbcWriteType.hashCode() : 0);
        result = 31 * result + (this.precisionAllowed ? 1 : 0);
        result = 31 * result + (this.scaleAllowed ? 1 : 0);
        result = 31 * result + (this.stringLengthAllowed ? 1 : 0);
        result = 31 * result + (this.defaultValueAllowed ? 1 : 0);
        return result;
    }
}

