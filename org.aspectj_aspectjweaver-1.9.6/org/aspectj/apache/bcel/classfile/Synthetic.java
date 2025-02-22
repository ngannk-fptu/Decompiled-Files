/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Utility;

public final class Synthetic
extends Attribute {
    private byte[] bytes;

    public Synthetic(Synthetic c) {
        this(c.getNameIndex(), c.getLength(), c.getBytes(), c.getConstantPool());
    }

    public Synthetic(int name_index, int length, byte[] bytes, ConstantPool constant_pool) {
        super((byte)7, name_index, length, constant_pool);
        this.bytes = bytes;
    }

    Synthetic(int name_index, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        this(name_index, length, (byte[])null, constant_pool);
        if (length > 0) {
            this.bytes = new byte[length];
            file.readFully(this.bytes);
            System.err.println("Synthetic attribute with length > 0");
        }
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitSynthetic(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        if (this.length > 0) {
            file.write(this.bytes, 0, this.length);
        }
    }

    public final byte[] getBytes() {
        return this.bytes;
    }

    public final void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public final String toString() {
        StringBuffer buf = new StringBuffer("Synthetic");
        if (this.length > 0) {
            buf.append(" " + Utility.toHexString(this.bytes));
        }
        return buf.toString();
    }
}

