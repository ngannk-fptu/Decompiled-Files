/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package net.java.ao.types;

import com.google.common.base.Objects;
import net.java.ao.ActiveObjectsConfigurationException;

public class TypeQualifiers {
    public static final int MAX_STRING_LENGTH = 450;
    public static final int OLD_MAX_STRING_LENGTH = 767;
    public static final int UNLIMITED_LENGTH = -1;
    private final Integer precision;
    private final Integer scale;
    private final Integer stringLength;
    private final Integer reportedStringLength;

    private TypeQualifiers(Integer precision, Integer scale, Integer stringLength, Integer reportedStringLength) {
        this.precision = precision;
        this.scale = scale;
        this.stringLength = stringLength;
        this.reportedStringLength = reportedStringLength;
    }

    public static TypeQualifiers qualifiers() {
        return new TypeQualifiers(null, null, null, null);
    }

    public TypeQualifiers precision(int precision) {
        if (precision <= 0) {
            throw new ActiveObjectsConfigurationException("Numeric precision must be greater than zero");
        }
        return new TypeQualifiers(precision, this.scale, this.stringLength, this.reportedStringLength);
    }

    public TypeQualifiers scale(int scale) {
        if (scale < 0) {
            throw new ActiveObjectsConfigurationException("Numeric scale must be greater than or equal to zero");
        }
        return new TypeQualifiers(this.precision, scale, this.stringLength, this.reportedStringLength);
    }

    public TypeQualifiers stringLength(int stringLength) {
        int reportedStringLength = stringLength;
        if (stringLength != -1) {
            if (stringLength <= 0) {
                throw new ActiveObjectsConfigurationException("String length must be greater than zero or unlimited");
            }
            if (stringLength > 450) {
                stringLength = -1;
                if (reportedStringLength > 767) {
                    reportedStringLength = -1;
                }
            }
        }
        return new TypeQualifiers(this.precision, this.scale, stringLength, reportedStringLength);
    }

    public TypeQualifiers withQualifiers(TypeQualifiers overrides) {
        if (overrides.isDefined()) {
            return new TypeQualifiers(overrides.hasPrecision() ? overrides.precision : this.precision, overrides.hasScale() ? overrides.scale : this.scale, overrides.hasStringLength() ? overrides.stringLength : this.stringLength, overrides.hasStringLength() ? overrides.reportedStringLength : this.reportedStringLength);
        }
        return this;
    }

    public Integer getPrecision() {
        return this.precision;
    }

    public Integer getScale() {
        return this.scale;
    }

    public Integer getStringLength() {
        return this.stringLength;
    }

    public boolean isDefined() {
        return this.hasPrecision() || this.hasScale() || this.hasStringLength();
    }

    public boolean hasPrecision() {
        return this.precision != null;
    }

    public boolean hasScale() {
        return this.scale != null;
    }

    public boolean hasStringLength() {
        return this.stringLength != null;
    }

    public boolean isUnlimitedLength() {
        return this.stringLength != null && this.stringLength == -1;
    }

    public boolean areLengthsCorrect() {
        return Objects.equal((Object)this.stringLength, (Object)this.reportedStringLength);
    }

    public boolean isUnlimitedStringLengthSupportCompatible(TypeQualifiers other) {
        if (this.hasStringLength() || other.hasStringLength()) {
            return this.isUnlimitedLength() == other.isUnlimitedLength();
        }
        return true;
    }

    public static boolean areCompatible(TypeQualifiers derivedFromEntityAnnotations, TypeQualifiers derivedFromTableMetadata) {
        if (derivedFromEntityAnnotations.hasPrecision() && !Objects.equal((Object)derivedFromEntityAnnotations.getPrecision(), (Object)derivedFromTableMetadata.getPrecision())) {
            return false;
        }
        if (derivedFromEntityAnnotations.hasScale() && !Objects.equal((Object)derivedFromEntityAnnotations.getScale(), (Object)derivedFromTableMetadata.getScale())) {
            return false;
        }
        return derivedFromEntityAnnotations.isUnlimitedStringLengthSupportCompatible(derivedFromTableMetadata) && derivedFromEntityAnnotations.areLengthsCorrect() && derivedFromTableMetadata.areLengthsCorrect();
    }

    public boolean equals(Object other) {
        if (other instanceof TypeQualifiers) {
            TypeQualifiers q = (TypeQualifiers)other;
            return Objects.equal((Object)this.precision, (Object)q.precision) && Objects.equal((Object)this.scale, (Object)q.scale) && Objects.equal((Object)this.stringLength, (Object)q.stringLength) && Objects.equal((Object)this.reportedStringLength, (Object)q.reportedStringLength);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.precision, this.scale, this.stringLength, this.reportedStringLength});
    }

    public String toString() {
        StringBuilder ret = new StringBuilder("(");
        if (this.precision != null) {
            ret.append("precision=").append(this.precision);
        }
        if (this.scale != null) {
            if (ret.length() > 1) {
                ret.append(",");
            }
            ret.append("scale=").append(this.scale);
        }
        if (this.stringLength != null) {
            ret.append("length=").append(this.stringLength);
        }
        return ret.append(")").toString();
    }
}

