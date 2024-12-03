/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface Value {
    public byte byteValue();

    public short shortValue();

    public int intValue();

    public long longValue();

    public BigDecimal bigDecimalValue();

    public BigInteger bigIntegerValue();

    public float floatValue();

    public double doubleValue();

    public boolean booleanValue();

    public Date dateValue();

    public String stringValue();

    public String stringValueEncoded();

    public Object toValue();

    public <T extends Enum> T toEnum(Class<T> var1);

    public boolean isContainer();

    public void chop();

    public char charValue();
}

