/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.IndexedInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.ReturnaddressType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;
import org.apache.bcel.generic.Visitor;
import org.apache.bcel.util.ByteSequence;

public class RET
extends Instruction
implements IndexedInstruction,
TypedInstruction {
    private boolean wide;
    private int index;

    RET() {
    }

    public RET(int index) {
        super((short)169, (short)2);
        this.setIndex(index);
    }

    @Override
    public void accept(Visitor v) {
        v.visitRET(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        if (this.wide) {
            out.writeByte(196);
        }
        out.writeByte(super.getOpcode());
        if (this.wide) {
            out.writeShort(this.index);
        } else {
            out.writeByte(this.index);
        }
    }

    @Override
    public final int getIndex() {
        return this.index;
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        return ReturnaddressType.NO_TARGET;
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        this.wide = wide;
        if (wide) {
            this.index = bytes.readUnsignedShort();
            super.setLength(4);
        } else {
            this.index = bytes.readUnsignedByte();
            super.setLength(2);
        }
    }

    @Override
    public final void setIndex(int n) {
        if (n < 0) {
            throw new ClassGenException("Negative index value: " + n);
        }
        this.index = n;
        this.setWide();
    }

    private void setWide() {
        boolean bl = this.wide = this.index > 255;
        if (this.wide) {
            super.setLength(4);
        } else {
            super.setLength(2);
        }
    }

    @Override
    public String toString(boolean verbose) {
        return super.toString(verbose) + " " + this.index;
    }
}

