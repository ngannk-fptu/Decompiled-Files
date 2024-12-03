/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;

public final class Deprecated
extends Attribute {
    private byte[] bytes;

    public Deprecated(Deprecated c) {
        this(c.getNameIndex(), c.getLength(), c.getBytes(), c.getConstantPool());
    }

    public Deprecated(int name_index, int length, byte[] bytes, ConstantPool constant_pool) {
        super((byte)8, name_index, length, constant_pool);
        this.bytes = bytes;
    }

    Deprecated(int name_index, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        this(name_index, length, (byte[])null, constant_pool);
        if (length > 0) {
            this.bytes = new byte[length];
            file.readFully(this.bytes);
            System.err.println("Deprecated attribute with length > 0");
        }
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitDeprecated(this);
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
        return Constants.ATTRIBUTE_NAMES[8];
    }
}

