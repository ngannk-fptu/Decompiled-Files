/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.SymbolTable;

public interface IonCatalog {
    public SymbolTable getTable(String var1);

    public SymbolTable getTable(String var1, int var2);
}

