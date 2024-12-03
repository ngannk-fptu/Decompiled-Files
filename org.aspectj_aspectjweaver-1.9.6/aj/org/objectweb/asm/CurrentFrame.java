/*
 * Decompiled with CFR 0.152.
 */
package aj.org.objectweb.asm;

import aj.org.objectweb.asm.Frame;
import aj.org.objectweb.asm.Label;
import aj.org.objectweb.asm.Symbol;
import aj.org.objectweb.asm.SymbolTable;

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

