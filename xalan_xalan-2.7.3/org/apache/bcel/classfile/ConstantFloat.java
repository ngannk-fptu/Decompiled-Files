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

public final class ConstantFloat
extends Constant
implements ConstantObject {
    private float bytes;

    public ConstantFloat(ConstantFloat c) {
        this(c.getBytes());
    }

    ConstantFloat(DataInput file) throws IOException {
        this(file.readFloat());
    }

    public ConstantFloat(float bytes) {
        super((byte)4);
        this.bytes = bytes;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantFloat(this);
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        file.writeByte(super.getTag());
        file.writeFloat(this.bytes);
    }

    public float getBytes() {
        return this.bytes;
    }

    @Override
    public Object getConstantValue(ConstantPool cp) {
        return Float.valueOf(this.bytes);
    }

    public void setBytes(float bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return super.toString() + "(bytes = " + this.bytes + ")";
    }
}

