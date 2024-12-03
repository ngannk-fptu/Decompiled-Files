/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import software.amazon.ion.IonSymbol;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.impl.PrivateIonValue;

@Deprecated
public interface PrivateIonSymbol
extends IonSymbol {
    public SymbolToken symbolValue(PrivateIonValue.SymbolTableProvider var1);
}

