/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.generic.ClassGenException;
import org.aspectj.apache.bcel.generic.InstructionLV;

public class InstructionCLV
extends InstructionLV {
    public InstructionCLV(short opcode) {
        super(opcode);
    }

    public InstructionCLV(short opcode, int localVariableIndex) {
        super(opcode, localVariableIndex);
    }

    @Override
    public void setIndex(int localVariableIndex) {
        if (localVariableIndex != this.getIndex()) {
            throw new ClassGenException("Do not attempt to modify the index to '" + localVariableIndex + "' for this constant instruction: " + this);
        }
    }

    @Override
    public boolean canSetIndex() {
        return false;
    }
}

