/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.ReturnaddressType;
import org.aspectj.apache.bcel.generic.Type;

public class RET
extends Instruction {
    private boolean wide;
    private int index;

    public RET(int index, boolean wide) {
        super((short)169);
        this.index = index;
        this.wide = wide;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        if (this.wide) {
            out.writeByte(196);
        }
        out.writeByte(this.opcode);
        if (this.wide) {
            out.writeShort(this.index);
        } else {
            out.writeByte(this.index);
        }
    }

    @Override
    public int getLength() {
        if (this.wide) {
            return 4;
        }
        return 2;
    }

    @Override
    public final int getIndex() {
        return this.index;
    }

    @Override
    public final void setIndex(int index) {
        this.index = index;
        this.wide = index > 255;
    }

    @Override
    public String toString(boolean verbose) {
        return super.toString(verbose) + " " + this.index;
    }

    @Override
    public Type getType(ConstantPool cp) {
        return ReturnaddressType.NO_TARGET;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RET)) {
            return false;
        }
        RET o = (RET)other;
        return o.opcode == this.opcode && o.index == this.index;
    }

    @Override
    public int hashCode() {
        return this.opcode * 37 + this.index;
    }
}

