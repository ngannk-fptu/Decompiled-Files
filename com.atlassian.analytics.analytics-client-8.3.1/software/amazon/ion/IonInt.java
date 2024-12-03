/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.math.BigInteger;
import software.amazon.ion.IntegerSize;
import software.amazon.ion.IonValue;
import software.amazon.ion.NullValueException;
import software.amazon.ion.UnknownSymbolException;

public interface IonInt
extends IonValue {
    public int intValue() throws NullValueException;

    public long longValue() throws NullValueException;

    public BigInteger bigIntegerValue();

    public IntegerSize getIntegerSize();

    public void setValue(int var1);

    public void setValue(long var1);

    public void setValue(Number var1);

    public IonInt clone() throws UnknownSymbolException;
}

