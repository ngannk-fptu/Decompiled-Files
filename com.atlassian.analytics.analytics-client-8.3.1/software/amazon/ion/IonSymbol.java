/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.EmptySymbolException;
import software.amazon.ion.IonText;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.UnknownSymbolException;

public interface IonSymbol
extends IonText {
    public String stringValue() throws UnknownSymbolException;

    public SymbolToken symbolValue();

    public void setValue(String var1) throws EmptySymbolException;

    public IonSymbol clone() throws UnknownSymbolException;
}

