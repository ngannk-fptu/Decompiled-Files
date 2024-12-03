/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.Visitor;
import org.apache.bcel.util.ByteSequence;

public class IINC
extends LocalVariableInstruction {
    private boolean wide;
    private int c;

    IINC() {
    }

    public IINC(int n, int c) {
        super.setOpcode((short)132);
        super.setLength(3);
        this.setIndex(n);
        this.setIncrement(c);
    }

    @Override
    public void accept(Visitor v) {
        v.visitLocalVariableInstruction(this);
        v.visitIINC(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        if (this.wide) {
            out.writeByte(196);
        }
        out.writeByte(super.getOpcode());
        if (this.wide) {
            out.writeShort(super.getIndex());
            out.writeShort(this.c);
        } else {
            out.writeByte(super.getIndex());
            out.writeByte(this.c);
        }
    }

    public final int getIncrement() {
        return this.c;
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        return Type.INT;
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        this.wide = wide;
        if (wide) {
            super.setLength(6);
            super.setIndexOnly(bytes.readUnsignedShort());
            this.c = bytes.readShort();
        } else {
            super.setLength(3);
            super.setIndexOnly(bytes.readUnsignedByte());
            this.c = bytes.readByte();
        }
    }

    public final void setIncrement(int c) {
        this.c = c;
        this.setWide();
    }

    @Override
    public final void setIndex(int n) {
        if (n < 0) {
            throw new ClassGenException("Negative index value: " + n);
        }
        super.setIndexOnly(n);
        this.setWide();
    }

    private void setWide() {
        boolean bl = this.wide = super.getIndex() > 255;
        if (this.c > 0) {
            this.wide = this.wide || this.c > 127;
        } else {
            boolean bl2 = this.wide = this.wide || this.c < -128;
        }
        if (this.wide) {
            super.setLength(6);
        } else {
            super.setLength(3);
        }
    }

    @Override
    public String toString(boolean verbose) {
        return super.toString(verbose) + " " + this.c;
    }
}

