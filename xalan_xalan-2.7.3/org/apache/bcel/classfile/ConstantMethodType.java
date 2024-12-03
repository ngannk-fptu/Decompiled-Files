/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.Visitor;

public final class ConstantMethodType
extends Constant {
    private int descriptorIndex;

    public ConstantMethodType(ConstantMethodType c) {
        this(c.getDescriptorIndex());
    }

    ConstantMethodType(DataInput file) throws IOException {
        this(file.readUnsignedShort());
    }

    public ConstantMethodType(int descriptorIndex) {
        super((byte)16);
        this.descriptorIndex = descriptorIndex;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantMethodType(this);
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        file.writeByte(super.getTag());
        file.writeShort(this.descriptorIndex);
    }

    public int getDescriptorIndex() {
        return this.descriptorIndex;
    }

    public void setDescriptorIndex(int descriptorIndex) {
        this.descriptorIndex = descriptorIndex;
    }

    @Override
    public String toString() {
        return super.toString() + "(descriptorIndex = " + this.descriptorIndex + ")";
    }
}

