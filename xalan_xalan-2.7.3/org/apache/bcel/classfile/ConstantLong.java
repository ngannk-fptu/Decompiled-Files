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

public final class ConstantLong
extends Constant
implements ConstantObject {
    private long bytes;

    public ConstantLong(ConstantLong c) {
        this(c.getBytes());
    }

    ConstantLong(DataInput file) throws IOException {
        this(file.readLong());
    }

    public ConstantLong(long bytes) {
        super((byte)5);
        this.bytes = bytes;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantLong(this);
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        file.writeByte(super.getTag());
        file.writeLong(this.bytes);
    }

    public long getBytes() {
        return this.bytes;
    }

    @Override
    public Object getConstantValue(ConstantPool cp) {
        return this.bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return super.toString() + "(bytes = " + this.bytes + ")";
    }
}

