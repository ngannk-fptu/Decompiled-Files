/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.jar.asm;

import net.bytebuddy.jar.asm.Frame;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Symbol;
import net.bytebuddy.jar.asm.SymbolTable;

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

