/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.Visitor;

abstract class MarkerInstruction
extends Instruction {
    public MarkerInstruction() {
        super((short)-1, (short)0);
    }

    @Override
    public void accept(Visitor v) {
    }

    @Override
    public final int consumeStack(ConstantPoolGen cpg) {
        return 0;
    }

    @Override
    public final int produceStack(ConstantPoolGen cpg) {
        return 0;
    }

    @Override
    public Instruction copy() {
        return this;
    }

    @Override
    public final void dump(DataOutputStream out) throws IOException {
    }
}

