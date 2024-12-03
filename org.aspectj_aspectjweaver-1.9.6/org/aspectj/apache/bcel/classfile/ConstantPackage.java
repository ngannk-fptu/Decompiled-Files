/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.Constant;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;

public final class ConstantPackage
extends Constant {
    private int nameIndex;

    ConstantPackage(DataInputStream file) throws IOException {
        this(file.readUnsignedShort());
    }

    public ConstantPackage(int nameIndex) {
        super((byte)20);
        this.nameIndex = nameIndex;
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantPackage(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeShort(this.nameIndex);
    }

    @Override
    public Integer getValue() {
        return this.nameIndex;
    }

    public final int getNameIndex() {
        return this.nameIndex;
    }

    @Override
    public final String toString() {
        return super.toString() + "(name_index = " + this.nameIndex + ")";
    }

    public String getPackageName(ConstantPool cpool) {
        Constant c = cpool.getConstant(this.nameIndex, (byte)1);
        return ((ConstantUtf8)c).getValue();
    }
}

