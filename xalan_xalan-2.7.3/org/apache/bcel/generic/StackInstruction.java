/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.Type;

public abstract class StackInstruction
extends Instruction {
    StackInstruction() {
    }

    protected StackInstruction(short opcode) {
        super(opcode, (short)1);
    }

    public Type getType(ConstantPoolGen cp) {
        return Type.UNKNOWN;
    }
}

