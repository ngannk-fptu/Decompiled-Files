/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.math.BigDecimal;
import software.amazon.ion.IonValue;
import software.amazon.ion.NullValueException;
import software.amazon.ion.UnknownSymbolException;

public interface IonFloat
extends IonValue {
    public float floatValue() throws NullValueException;

    public double doubleValue() throws NullValueException;

    public BigDecimal bigDecimalValue() throws NullValueException;

    public void setValue(float var1);

    public void setValue(double var1);

    public void setValue(BigDecimal var1);

    public boolean isNumericValue();

    public IonFloat clone() throws UnknownSymbolException;
}

