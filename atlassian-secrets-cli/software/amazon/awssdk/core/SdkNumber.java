/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
@Immutable
public final class SdkNumber
extends Number
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Number numberValue;
    private final String stringValue;

    private SdkNumber(Number value) {
        this.numberValue = value;
        this.stringValue = null;
    }

    private SdkNumber(String stringValue) {
        this.stringValue = stringValue;
        this.numberValue = null;
    }

    private static boolean isNumberValueNaN(Number numberValue) {
        return numberValue instanceof Double && Double.isNaN((Double)numberValue) || numberValue instanceof Float && Float.isNaN(((Float)numberValue).floatValue());
    }

    private static boolean isNumberValueInfinite(Number numberValue) {
        return numberValue instanceof Double && Double.isInfinite((Double)numberValue) || numberValue instanceof Float && Float.isInfinite(((Float)numberValue).floatValue());
    }

    private static Number valueOf(Number numberValue) {
        Number valueOfInfiniteOrNaN = SdkNumber.valueOfInfiniteOrNaN(numberValue);
        return valueOfInfiniteOrNaN != null ? valueOfInfiniteOrNaN : SdkNumber.valueInBigDecimal(numberValue);
    }

    private static Number valueOfInfiniteOrNaN(Number numberValue) {
        if (numberValue instanceof Double && (Double.isInfinite((Double)numberValue) || Double.isNaN((Double)numberValue))) {
            return numberValue.doubleValue();
        }
        if (numberValue instanceof Float && (Float.isInfinite(((Float)numberValue).floatValue()) || Float.isNaN(((Float)numberValue).floatValue()))) {
            return Float.valueOf(numberValue.floatValue());
        }
        return null;
    }

    private static BigDecimal valueInBigDecimal(Number numberValue) {
        if (numberValue instanceof Double) {
            return BigDecimal.valueOf((Double)numberValue);
        }
        if (numberValue instanceof Float) {
            return BigDecimal.valueOf(((Float)numberValue).floatValue());
        }
        if (numberValue instanceof Integer) {
            return new BigDecimal((Integer)numberValue);
        }
        if (numberValue instanceof Short) {
            return new BigDecimal(((Short)numberValue).shortValue());
        }
        if (numberValue instanceof Long) {
            return BigDecimal.valueOf((Long)numberValue);
        }
        if (numberValue instanceof BigDecimal) {
            return (BigDecimal)numberValue;
        }
        if (numberValue instanceof BigInteger) {
            return new BigDecimal((BigInteger)numberValue);
        }
        return new BigDecimal(numberValue.toString());
    }

    public static SdkNumber fromInteger(int integerValue) {
        return new SdkNumber(integerValue);
    }

    public static SdkNumber fromBigInteger(BigInteger bigIntegerValue) {
        return new SdkNumber(bigIntegerValue);
    }

    public static SdkNumber fromBigDecimal(BigDecimal bigDecimalValue) {
        Validate.notNull(bigDecimalValue, "BigDecimal cannot be null", new Object[0]);
        return new SdkNumber(bigDecimalValue);
    }

    public static SdkNumber fromLong(long longValue) {
        return new SdkNumber(longValue);
    }

    public static SdkNumber fromDouble(double doubleValue) {
        return new SdkNumber(doubleValue);
    }

    public static SdkNumber fromShort(short shortValue) {
        return new SdkNumber(shortValue);
    }

    public static SdkNumber fromFloat(float floatValue) {
        return new SdkNumber(Float.valueOf(floatValue));
    }

    public static SdkNumber fromString(String stringValue) {
        return new SdkNumber(stringValue);
    }

    @Override
    public int intValue() {
        return this.numberValue instanceof Integer ? this.numberValue.intValue() : (this.stringValue != null ? new BigDecimal(this.stringValue).intValue() : SdkNumber.valueOf(this.numberValue).intValue());
    }

    @Override
    public long longValue() {
        return this.numberValue instanceof Long ? this.numberValue.longValue() : (this.stringValue != null ? new BigDecimal(this.stringValue).longValue() : SdkNumber.valueOf(this.numberValue).longValue());
    }

    @Override
    public float floatValue() {
        return this.numberValue instanceof Float ? this.numberValue.floatValue() : (this.numberValue != null ? SdkNumber.valueOf(this.numberValue).floatValue() : new BigDecimal(this.stringValue).floatValue());
    }

    @Override
    public double doubleValue() {
        return this.numberValue instanceof Double ? this.numberValue.doubleValue() : (this.numberValue != null ? SdkNumber.valueOf(this.numberValue).doubleValue() : new BigDecimal(this.stringValue).doubleValue());
    }

    public BigDecimal bigDecimalValue() {
        if (this.stringValue != null) {
            return new BigDecimal(this.stringValue);
        }
        if (this.numberValue instanceof BigDecimal) {
            return (BigDecimal)this.numberValue;
        }
        if (SdkNumber.isNumberValueNaN(this.numberValue) || SdkNumber.isNumberValueInfinite(this.numberValue)) {
            throw new NumberFormatException("Nan or Infinite Number can not be converted to BigDecimal.");
        }
        return SdkNumber.valueInBigDecimal(this.numberValue);
    }

    public String stringValue() {
        return this.stringValue != null ? this.stringValue : this.numberValue.toString();
    }

    public String toString() {
        return this.stringValue != null ? this.stringValue : this.numberValue.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SdkNumber)) {
            return false;
        }
        SdkNumber sdkNumber = (SdkNumber)o;
        return Objects.equals(this.stringValue(), sdkNumber.stringValue());
    }

    public int hashCode() {
        return Objects.hashCode(this.stringValue());
    }
}

