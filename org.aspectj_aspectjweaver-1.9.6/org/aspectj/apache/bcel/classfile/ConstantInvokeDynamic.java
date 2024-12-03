/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.Constant;

public final class ConstantInvokeDynamic
extends Constant {
    private final int bootstrapMethodAttrIndex;
    private final int nameAndTypeIndex;

    ConstantInvokeDynamic(DataInputStream file) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort());
    }

    public ConstantInvokeDynamic(int readUnsignedShort, int nameAndTypeIndex) {
        super((byte)18);
        this.bootstrapMethodAttrIndex = readUnsignedShort;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeShort(this.bootstrapMethodAttrIndex);
        file.writeShort(this.nameAndTypeIndex);
    }

    public final int getNameAndTypeIndex() {
        return this.nameAndTypeIndex;
    }

    public final int getBootstrapMethodAttrIndex() {
        return this.bootstrapMethodAttrIndex;
    }

    @Override
    public final String toString() {
        return super.toString() + "(bootstrapMethodAttrIndex=" + this.bootstrapMethodAttrIndex + ",nameAndTypeIndex=" + this.nameAndTypeIndex + ")";
    }

    @Override
    public String getValue() {
        return this.toString();
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantInvokeDynamic(this);
    }
}

