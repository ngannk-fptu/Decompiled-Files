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

public final class ConstantClass
extends Constant
implements ConstantObject {
    private int nameIndex;

    public ConstantClass(ConstantClass c) {
        this(c.getNameIndex());
    }

    ConstantClass(DataInput dataInput) throws IOException {
        this(dataInput.readUnsignedShort());
    }

    public ConstantClass(int nameIndex) {
        super((byte)7);
        this.nameIndex = nameIndex;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantClass(this);
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

