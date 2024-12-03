/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.generic.InstructionLV;

public class IINC
extends InstructionLV {
    private int c;

    public IINC(int n, int c, boolean w) {
        super((short)132, n);
        this.c = c;
    }

    private boolean wide() {
        return this.lvar > 255 || Math.abs(this.c) > 127;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        if (this.wide()) {
            out.writeByte(196);
            out.writeByte(this.opcode);
            out.writeShort(this.lvar);
            out.writeShort(this.c);
        } else {
            out.writeByte(this.opcode);
            out.writeByte(this.lvar);
            out.writeByte(this.c);
        }
    }

    @Override
    public int getLength() {
        if (this.wide()) {
            return 6;
        }
        return 3;
    }

    @Override
    public String toString(boolean verbose) {
        return super.toString(verbose) + " " + this.c;
    }

    public final int getIncrement() {
        return this.c;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof IINC)) {
            return false;
        }
        IINC o = (IINC)other;
        return o.lvar == this.lvar && o.c == this.c;
    }

    @Override
    public int hashCode() {
        return this.opcode * 37 + this.lvar * (this.c + 17);
    }
}

