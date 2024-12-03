/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import org.apache.xerces.util.SymbolTable;

public final class ShadowedSymbolTable
extends SymbolTable {
    protected SymbolTable fSymbolTable;

    public ShadowedSymbolTable(SymbolTable symbolTable) {
        this.fSymbolTable = symbolTable;
    }

    @Override
    public String addSymbol(String string) {
        if (this.fSymbolTable.containsSymbol(string)) {
            return this.fSymbolTable.addSymbol(string);
        }
        return super.addSymbol(string);
    }

    @Override
    public String addSymbol(char[] cArray, int n, int n2) {
        if (this.fSymbolTable.containsSymbol(cArray, n, n2)) {
            return this.fSymbolTable.addSymbol(cArray, n, n2);
        }
        return super.addSymbol(cArray, n, n2);
    }

    @Override
    public int hash(String string) {
        return this.fSymbolTable.hash(string);
    }

    @Override
    public int hash(char[] cArray, int n, int n2) {
        return this.fSymbolTable.hash(cArray, n, n2);
    }
}

