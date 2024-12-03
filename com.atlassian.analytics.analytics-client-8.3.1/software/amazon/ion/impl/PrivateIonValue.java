/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.PrintWriter;
import software.amazon.ion.IonValue;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;

@Deprecated
public interface PrivateIonValue
extends IonValue {
    public int getElementId();

    public SymbolToken getFieldNameSymbol(SymbolTableProvider var1);

    public SymbolToken[] getTypeAnnotationSymbols(SymbolTableProvider var1);

    public void setSymbolTable(SymbolTable var1);

    public SymbolTable getAssignedSymbolTable();

    public void dump(PrintWriter var1);

    public String validate();

    public static interface SymbolTableProvider {
        public SymbolTable getSymbolTable();
    }
}

