/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.math.BigDecimal;
import software.amazon.ion.Decimal;
import software.amazon.ion.IonNumber;
import software.amazon.ion.NullValueException;
import software.amazon.ion.UnknownSymbolException;

public interface IonDecimal
extends IonNumber {
    public float floatValue() throws NullValueException;

    public double doubleValue() throws NullValueException;

    public BigDecimal bigDecimalValue();

    public Decimal decimalValue();

    public void setValue(long var1);

    public void setValue(float var1);

    public void setValue(double var1);

    public void setValue(BigDecimal var1);

    public IonDecimal clone() throws UnknownSymbolException;
}

