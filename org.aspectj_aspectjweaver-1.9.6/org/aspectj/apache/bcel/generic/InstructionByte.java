/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.generic.ArrayType;
import org.aspectj.apache.bcel.generic.BasicType;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.Type;

public class InstructionByte
extends Instruction {
    private final byte theByte;

    public InstructionByte(short opcode, byte b) {
        super(opcode);
        this.theByte = b;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
        out.writeByte(this.theByte);
    }

    @Override
    public String toString(boolean verbose) {
        return super.toString(verbose) + " " + this.theByte;
    }

    public final byte getTypecode() {
        return this.theByte;
    }

    @Override
    public final Type getType() {
        return new ArrayType(BasicType.getType(this.theByte), 1);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InstructionByte)) {
            return false;
        }
        InstructionByte o = (InstructionByte)other;
        return o.opcode == this.opcode && o.theByte == this.theByte;
    }

    @Override
    public int hashCode() {
        return this.opcode * 37 + this.theByte;
    }
}

