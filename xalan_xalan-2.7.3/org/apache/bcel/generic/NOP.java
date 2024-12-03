/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.Visitor;

public class NOP
extends Instruction {
    public NOP() {
        super((short)0, (short)1);
    }

    @Override
    public void accept(Visitor v) {
        v.visitNOP(this);
    }
}

