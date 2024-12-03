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

public final class ConstantDouble
extends Constant
implements ConstantObject {
    private double bytes;

    public ConstantDouble(ConstantDouble c) {
        this(c.getBytes());
    }

    ConstantDouble(DataInput file) throws IOException {
        this(file.readDouble());
    }

    public ConstantDouble(double bytes) {
        super((byte)6);
        this.bytes = bytes;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantDouble(this);
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        file.writeByte(super.getTag());
        file.writeDouble(this.bytes);
    }

    public double getBytes() {
        return this.bytes;
    }

    @Override
    public Object getConstantValue(ConstantPool cp) {
        return this.bytes;
    }

    public void setBytes(double bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return super.toString() + "(bytes = " + this.bytes + ")";
    }
}

