/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

import org.objectweb.asm.Frame;
import org.objectweb.asm.Label;
import org.objectweb.asm.Symbol;
import org.objectweb.asm.SymbolTable;

final class CurrentFrame
extends Frame {
    CurrentFrame(Label owner) {
        super(owner);
    }

    void execute(int opcode, int arg, Symbol symbolArg, SymbolTable symbolTable) {
        super.execute(opcode, arg, symbolArg, symbolTable);
        Frame successor = new Frame(null);
        this.merge(symbolTable, successor, 0);
        this.copyFrom(successor);
    }
}

