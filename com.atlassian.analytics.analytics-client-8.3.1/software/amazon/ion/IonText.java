/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.EmptySymbolException;
import software.amazon.ion.IonValue;
import software.amazon.ion.UnknownSymbolException;

public interface IonText
extends IonValue {
    public String stringValue();

    public void setValue(String var1) throws EmptySymbolException;

    public IonText clone() throws UnknownSymbolException;
}

