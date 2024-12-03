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

public final class ConstantInteger
extends Constant
implements ConstantObject {
    private int bytes;

    public ConstantInteger(ConstantInteger c) {
        this(c.getBytes());
    }

    ConstantInteger(DataInput file) throws IOException {
        this(file.readInt());
    }

    public ConstantInteger(int bytes) {
        super((byte)3);
        this.bytes = bytes;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantInteger(this);
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        file.writeByte(super.getTag());
        file.writeInt(this.bytes);
    }

    public int getBytes() {
        return this.bytes;
    }

    @Override
    public Object getConstantValue(ConstantPool cp) {
        return this.bytes;
    }

    public void setBytes(int bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return super.toString() + "(bytes = " + this.bytes + ")";
    }
}

