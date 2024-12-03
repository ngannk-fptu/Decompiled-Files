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

public final class ConstantString
extends Constant
implements ConstantObject {
    private int stringIndex;

    public ConstantString(ConstantString c) {
        this(c.getStringIndex());
    }

    ConstantString(DataInput file) throws IOException {
        this(file.readUnsignedShort());
    }

    public ConstantString(int stringIndex) {
        super((byte)8);
        this.stringIndex = stringIndex;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantString(this);
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        file.writeByte(super.getTag());
        file.writeShort(this.stringIndex);
    }

    public String getBytes(ConstantPool cp) {
        return (String)this.getConstantValue(cp);
    }

    @Override
    public Object getConstantValue(ConstantPool cp) {
        return cp.getConstantUtf8(this.stringIndex).getBytes();
    }

    public int getStringIndex() {
        return this.stringIndex;
    }

    public void setStringIndex(int stringIndex) {
        this.stringIndex = stringIndex;
    }

    @Override
    public String toString() {
        return super.toString() + "(stringIndex = " + this.stringIndex + ")";
    }
}

