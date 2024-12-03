/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.generic.Instruction;

public class InstructionShort
extends Instruction {
    private final short value;

    public InstructionShort(short opcode, short value) {
        super(opcode);
        this.value = value;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
        out.writeShort(this.value);
    }

    @Override
    public String toString(boolean verbose) {
        return super.toString(verbose) + " " + this.value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InstructionShort)) {
            return false;
        }
        InstructionShort o = (InstructionShort)other;
        return o.opcode == this.opcode && o.value == this.value;
    }

    @Override
    public int hashCode() {
        return this.opcode * 37 + this.value;
    }
}

