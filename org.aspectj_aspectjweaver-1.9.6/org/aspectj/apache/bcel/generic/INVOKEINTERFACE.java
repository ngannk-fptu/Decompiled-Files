/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.generic.ClassGenException;
import org.aspectj.apache.bcel.generic.InvokeInstruction;

public final class INVOKEINTERFACE
extends InvokeInstruction {
    private int nargs;

    public INVOKEINTERFACE(int index, int nargs, int zerobyte) {
        super((short)185, index);
        if (nargs < 1) {
            throw new ClassGenException("Number of arguments must be > 0 " + nargs);
        }
        this.nargs = nargs;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
        out.writeShort(this.index);
        out.writeByte(this.nargs);
        out.writeByte(0);
    }

    public int getCount() {
        return this.nargs;
    }

    @Override
    public String toString(ConstantPool cp) {
        return super.toString(cp) + " " + this.nargs;
    }

    @Override
    public int consumeStack(ConstantPool cpg) {
        return this.nargs;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof INVOKEINTERFACE)) {
            return false;
        }
        INVOKEINTERFACE o = (INVOKEINTERFACE)other;
        return o.opcode == this.opcode && o.index == this.index && o.nargs == this.nargs;
    }

    @Override
    public int hashCode() {
        return this.opcode * 37 + this.index * (this.nargs + 17);
    }
}

