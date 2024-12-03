/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.types;

import net.java.ao.ActiveObjectsConfigurationException;
import net.java.ao.types.LogicalType;
import net.java.ao.types.SchemaProperties;
import net.java.ao.types.TypeQualifiers;

public final class TypeInfo<T> {
    private final LogicalType<T> logicalType;
    private final SchemaProperties schemaProperties;
    private final TypeQualifiers defaultQualifiers;
    private final TypeQualifiers qualifiers;

    public TypeInfo(LogicalType<T> logicalType, SchemaProperties schemaProperties, TypeQualifiers defaultQualifiers) {
        this(logicalType, schemaProperties, defaultQualifiers, TypeQualifiers.qualifiers());
    }

    protected TypeInfo(LogicalType<T> logicalType, SchemaProperties schemaProperties, TypeQualifiers defaultQualifiers, TypeQualifiers qualifiers) {
        this.logicalType = logicalType;
        this.schemaProperties = schemaProperties;
        this.defaultQualifiers = defaultQualifiers;
        this.qualifiers = qualifiers;
    }

    public LogicalType<T> getLogicalType() {
        return this.logicalType;
    }

    public SchemaProperties getSchemaProperties() {
        return this.schemaProperties;
    }

    public TypeQualifiers getQualifiers() {
        return this.defaultQualifiers.withQualifiers(this.qualifiers);
    }

    public boolean isAllowedAsPrimaryKey() {
        return this.logicalType.isAllowedAsPrimaryKey() && !this.qualifiers.isUnlimitedLength();
    }

    public String getSqlTypeIdentifier() {
        TypeQualifiers allQualifiers = this.getQualifiers();
        if (!allQualifiers.isDefined()) {
            return this.schemaProperties.getSqlTypeName();
        }
        StringBuffer ret = new StringBuffer();
        ret.append(this.schemaProperties.getSqlTypeName());
        if (this.schemaProperties.isPrecisionAllowed() && allQualifiers.hasPrecision()) {
            ret.append("(").append(allQualifiers.getPrecision());
            if (this.schemaProperties.isScaleAllowed() && allQualifiers.hasScale()) {
                ret.append(",").append(allQualifiers.getScale());
            }
            ret.append(")");
        } else if (this.schemaProperties.isStringLengthAllowed() && allQualifiers.hasStringLength() && !allQualifiers.isUnlimitedLength()) {
            ret.append("(").append(allQualifiers.getStringLength()).append(")");
        }
        return ret.toString();
    }

    public int getJdbcWriteType() {
        return this.schemaProperties.hasOverrideJdbcWriteType() ? this.schemaProperties.getOverrideJdbcWriteType().intValue() : this.logicalType.getDefaultJdbcWriteType();
    }

    public TypeInfo<T> withQualifiers(TypeQualifiers qualifiers) {
        if (qualifiers.hasStringLength() && !this.schemaProperties.isStringLengthAllowed()) {
            throw new ActiveObjectsConfigurationException("String length cannot be specified for the type " + this.logicalType.getName());
        }
        return new TypeInfo<T>(this.logicalType, this.schemaProperties, this.defaultQualifiers, this.qualifiers.withQualifiers(qualifiers));
    }

    public boolean acceptsQualifiers(TypeQualifiers qualifiers) {
        return !(qualifiers.hasPrecision() && !this.schemaProperties.isPrecisionAllowed() || qualifiers.hasScale() && !this.schemaProperties.isScaleAllowed() || qualifiers.hasStringLength() && !this.schemaProperties.isStringLengthAllowed());
    }

    public boolean equals(Object other) {
        if (other instanceof TypeInfo) {
            TypeInfo type = (TypeInfo)other;
            return type.logicalType.equals(this.logicalType) && type.schemaProperties.equals(this.schemaProperties) && type.defaultQualifiers.equals(this.defaultQualifiers) && type.qualifiers.equals(this.qualifiers);
        }
        return false;
    }

    public int hashCode() {
        return (this.logicalType.hashCode() * 31 + this.schemaProperties.hashCode()) * 31 + this.defaultQualifiers.hashCode() * 31 + this.qualifiers.hashCode();
    }

    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append(this.logicalType.getName());
        if (this.qualifiers.isDefined()) {
            ret.append("(");
            if (this.qualifiers.hasPrecision()) {
                ret.append(this.qualifiers.getPrecision());
                if (this.qualifiers.hasScale()) {
                    ret.append(", ").append(this.qualifiers.getScale());
                }
            } else if (this.qualifiers.hasStringLength()) {
                ret.append(this.qualifiers.getStringLength());
            }
            ret.append(")");
        }
        ret.append(":");
        ret.append(this.schemaProperties.getSqlTypeName());
        return ret.toString();
    }
}

