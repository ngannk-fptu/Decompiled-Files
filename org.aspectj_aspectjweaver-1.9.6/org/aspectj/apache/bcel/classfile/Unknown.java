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
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.classfile.Utility;

public final class Unknown
extends Attribute {
    private byte[] bytes;
    private String name;

    public Unknown(Unknown c) {
        this(c.getNameIndex(), c.getLength(), c.getBytes(), c.getConstantPool());
    }

    public Unknown(int name_index, int length, byte[] bytes, ConstantPool constant_pool) {
        super((byte)-1, name_index, length, constant_pool);
        this.bytes = bytes;
        this.name = ((ConstantUtf8)constant_pool.getConstant(name_index, (byte)1)).getValue();
    }

    Unknown(int name_index, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        this(name_index, length, (byte[])null, constant_pool);
        if (length > 0) {
            this.bytes = new byte[length];
            file.readFully(this.bytes);
        }
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitUnknown(this);
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

    @Override
    public String getName() {
        return this.name;
    }

    public final void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public final String toString() {
        String hex;
        if (this.length == 0 || this.bytes == null) {
            return "(Unknown attribute " + this.name + ")";
        }
        if (this.length > 10) {
            byte[] tmp = new byte[10];
            System.arraycopy(this.bytes, 0, tmp, 0, 10);
            hex = Utility.toHexString(tmp) + "... (truncated)";
        } else {
            hex = Utility.toHexString(this.bytes);
        }
        return "(Unknown attribute " + this.name + ": " + hex + ")";
    }
}

