/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.org.objectweb.asm;

import com.sun.xml.ws.org.objectweb.asm.Frame;
import com.sun.xml.ws.org.objectweb.asm.Label;
import com.sun.xml.ws.org.objectweb.asm.Symbol;
import com.sun.xml.ws.org.objectweb.asm.SymbolTable;

final class CurrentFrame
extends Frame {
    CurrentFrame(Label owner) {
        super(owner);
    }

    @Override
    void execute(int opcode, int arg, Symbol symbolArg, SymbolTable symbolTable) {
        super.execute(opcode, arg, symbolArg, symbolTable);
        Frame successor = new Frame(null);
        this.merge(symbolTable, successor, 0);
        this.copyFrom(successor);
    }
}

