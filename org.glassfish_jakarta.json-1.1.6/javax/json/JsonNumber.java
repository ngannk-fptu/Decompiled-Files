/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonValue;

public interface JsonNumber
extends JsonValue {
    public boolean isIntegral();

    public int intValue();

    public int intValueExact();

    public long longValue();

    public long longValueExact();

    public BigInteger bigIntegerValue();

    public BigInteger bigIntegerValueExact();

    public double doubleValue();

    public BigDecimal bigDecimalValue();

    default public Number numberValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString();

    public boolean equals(Object var1);

    public int hashCode();
}

