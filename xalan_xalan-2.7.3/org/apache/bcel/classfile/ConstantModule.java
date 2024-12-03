/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantObject;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Visitor;

public final class ConstantModule
extends Constant
implements ConstantObject {
    private int nameIndex;

    public ConstantModule(ConstantModule c) {
        this(c.getNameIndex());
    }

    ConstantModule(DataInput file) throws IOException {
        this(file.readUnsignedShort());
    }

    public ConstantModule(int nameIndex) {
        super((byte)19);
        this.nameIndex = nameIndex;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantModule(this);
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        file.writeByte(super.getTag());
        file.writeShort(this.nameIndex);
    }

    public String getBytes(ConstantPool cp) {
        return (String)this.getConstantValue(cp);
    }

    @Override
    public Object getConstantValue(ConstantPool cp) {
        return cp.getConstantUtf8(this.nameIndex).getBytes();
    }

    public int getNameIndex() {
        return this.nameIndex;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    @Override
    public String toString() {
        return super.toString() + "(nameIndex = " + this.nameIndex + ")";
    }
}

