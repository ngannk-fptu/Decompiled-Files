/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import org.apache.xerces.util.SymbolTable;

public final class SynchronizedSymbolTable
extends SymbolTable {
    protected SymbolTable fSymbolTable;

    public SynchronizedSymbolTable(SymbolTable symbolTable) {
        this.fSymbolTable = symbolTable;
    }

    public SynchronizedSymbolTable() {
        this.fSymbolTable = new SymbolTable();
    }

    public SynchronizedSymbolTable(int n) {
        this.fSymbolTable = new SymbolTable(n);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String addSymbol(String string) {
        SymbolTable symbolTable = this.fSymbolTable;
        synchronized (symbolTable) {
            return this.fSymbolTable.addSymbol(string);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String addSymbol(char[] cArray, int n, int n2) {
        SymbolTable symbolTable = this.fSymbolTable;
        synchronized (symbolTable) {
            return this.fSymbolTable.addSymbol(cArray, n, n2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsSymbol(String string) {
        SymbolTable symbolTable = this.fSymbolTable;
        synchronized (symbolTable) {
            return this.fSymbolTable.containsSymbol(string);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsSymbol(char[] cArray, int n, int n2) {
        SymbolTable symbolTable = this.fSymbolTable;
        synchronized (symbolTable) {
            return this.fSymbolTable.containsSymbol(cArray, n, n2);
        }
    }
}

