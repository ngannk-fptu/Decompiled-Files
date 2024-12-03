/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.IonCatalog;
import software.amazon.ion.SymbolTable;

public interface IonMutableCatalog
extends IonCatalog {
    public void putTable(SymbolTable var1);
}

