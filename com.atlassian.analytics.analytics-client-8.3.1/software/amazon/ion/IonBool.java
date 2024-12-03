/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.IonValue;
import software.amazon.ion.NullValueException;
import software.amazon.ion.UnknownSymbolException;

public interface IonBool
extends IonValue {
    public boolean booleanValue() throws NullValueException;

    public void setValue(boolean var1);

    public void setValue(Boolean var1);

    public IonBool clone() throws UnknownSymbolException;
}

