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

public final class ConstantString
extends Constant {
    private int stringIndex;

    ConstantString(DataInputStream file) throws IOException {
        this(file.readUnsignedShort());
    }

    public ConstantString(int stringIndex) {
        super((byte)8);
        this.stringIndex = stringIndex;
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantString(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeShort(this.stringIndex);
    }

    @Override
    public Integer getValue() {
        return this.stringIndex;
    }

    public final int getStringIndex() {
        return this.stringIndex;
    }

    @Override
    public final String toString() {
        return super.toString() + "(string_index = " + this.stringIndex + ")";
    }

    public String getString(ConstantPool cpool) {
        Constant c = cpool.getConstant(this.stringIndex, (byte)1);
        return ((ConstantUtf8)c).getValue();
    }
}

