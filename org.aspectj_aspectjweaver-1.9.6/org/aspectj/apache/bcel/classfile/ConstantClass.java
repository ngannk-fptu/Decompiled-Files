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

public final class ConstantClass
extends Constant {
    private int nameIndex;

    ConstantClass(DataInputStream file) throws IOException {
        super((byte)7);
        this.nameIndex = file.readUnsignedShort();
    }

    public ConstantClass(int nameIndex) {
        super((byte)7);
        this.nameIndex = nameIndex;
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantClass(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeShort(this.nameIndex);
    }

    public final int getNameIndex() {
        return this.nameIndex;
    }

    @Override
    public Integer getValue() {
        return this.nameIndex;
    }

    public String getClassname(ConstantPool cpool) {
        return cpool.getConstantUtf8(this.nameIndex).getValue();
    }

    @Override
    public final String toString() {
        return super.toString() + "(name_index = " + this.nameIndex + ")";
    }
}

