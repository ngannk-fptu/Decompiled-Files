/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.Constant;

public final class ConstantMethodType
extends Constant {
    private int descriptorIndex;

    ConstantMethodType(DataInputStream file) throws IOException {
        this(file.readUnsignedShort());
    }

    public ConstantMethodType(int descriptorIndex) {
        super((byte)16);
        this.descriptorIndex = descriptorIndex;
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeShort(this.descriptorIndex);
    }

    public final int getDescriptorIndex() {
        return this.descriptorIndex;
    }

    @Override
    public final String toString() {
        return super.toString() + "(descriptorIndex=" + this.descriptorIndex + ")";
    }

    @Override
    public String getValue() {
        return this.toString();
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantMethodType(this);
    }
}

