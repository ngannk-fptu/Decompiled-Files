/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.Visitor;

public final class ConstantMethodHandle
extends Constant {
    private int referenceKind;
    private int referenceIndex;

    public ConstantMethodHandle(ConstantMethodHandle c) {
        this(c.getReferenceKind(), c.getReferenceIndex());
    }

    ConstantMethodHandle(DataInput file) throws IOException {
        this(file.readUnsignedByte(), file.readUnsignedShort());
    }

    public ConstantMethodHandle(int referenceKind, int referenceIndex) {
        super((byte)15);
        this.referenceKind = referenceKind;
        this.referenceIndex = referenceIndex;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantMethodHandle(this);
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        file.writeByte(super.getTag());
        file.writeByte(this.referenceKind);
        file.writeShort(this.referenceIndex);
    }

    public int getReferenceIndex() {
        return this.referenceIndex;
    }

    public int getReferenceKind() {
        return this.referenceKind;
    }

    public void setReferenceIndex(int referenceIndex) {
        this.referenceIndex = referenceIndex;
    }

    public void setReferenceKind(int referenceKind) {
        this.referenceKind = referenceKind;
    }

    @Override
    public String toString() {
        return super.toString() + "(referenceKind = " + this.referenceKind + ", referenceIndex = " + this.referenceIndex + ")";
    }
}

