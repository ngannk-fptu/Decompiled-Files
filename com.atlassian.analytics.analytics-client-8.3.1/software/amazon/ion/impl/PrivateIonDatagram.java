/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import software.amazon.ion.IonDatagram;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.impl.PrivateIonValue;

@Deprecated
public interface PrivateIonDatagram
extends PrivateIonValue,
IonDatagram {
    public void appendTrailingSymbolTable(SymbolTable var1);
}

