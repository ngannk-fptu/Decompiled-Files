/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Visitor;

public final class ConstantNameAndType
extends Constant {
    private int nameIndex;
    private int signatureIndex;

    public ConstantNameAndType(ConstantNameAndType c) {
        this(c.getNameIndex(), c.getSignatureIndex());
    }

    ConstantNameAndType(DataInput file) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort());
    }

    public ConstantNameAndType(int nameIndex, int signatureIndex) {
        super((byte)12);
        this.nameIndex = nameIndex;
        this.signatureIndex = signatureIndex;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantNameAndType(this);
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        file.writeByte(super.getTag());
        file.writeShort(this.nameIndex);
        file.writeShort(this.signatureIndex);
    }

    public String getName(ConstantPool cp) {
        return cp.constantToString(this.getNameIndex(), (byte)1);
    }

    public int getNameIndex() {
        return this.nameIndex;
    }

    public String getSignature(ConstantPool cp) {
        return cp.constantToString(this.getSignatureIndex(), (byte)1);
    }

    public int getSignatureIndex() {
        return this.signatureIndex;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    public void setSignatureIndex(int signatureIndex) {
        this.signatureIndex = signatureIndex;
    }

    @Override
    public String toString() {
        return super.toString() + "(nameIndex = " + this.nameIndex + ", signatureIndex = " + this.signatureIndex + ")";
    }
}

