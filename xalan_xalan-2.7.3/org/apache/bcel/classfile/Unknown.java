/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;

public final class Unknown
extends Attribute {
    private byte[] bytes;
    private final String name;

    public Unknown(int nameIndex, int length, byte[] bytes, ConstantPool constantPool) {
        super((byte)-1, nameIndex, length, constantPool);
        this.bytes = bytes;
        this.name = constantPool.getConstantUtf8(nameIndex).getBytes();
    }

    Unknown(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, (byte[])null, constantPool);
        if (length > 0) {
            this.bytes = new byte[length];
            input.readFully(this.bytes);
        }
    }

    public Unknown(Unknown unknown) {
        this(unknown.getNameIndex(), unknown.getLength(), unknown.getBytes(), unknown.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        v.visitUnknown(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        Unknown c = (Unknown)this.clone();
        if (this.bytes != null) {
            c.bytes = (byte[])this.bytes.clone();
        }
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        if (super.getLength() > 0) {
            file.write(this.bytes, 0, super.getLength());
        }
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        String hex;
        if (super.getLength() == 0 || this.bytes == null) {
            return "(Unknown attribute " + this.name + ")";
        }
        int limit = 10;
        if (super.getLength() > 10) {
            byte[] tmp = Arrays.copyOf(this.bytes, 10);
            hex = Utility.toHexString(tmp) + "... (truncated)";
        } else {
            hex = Utility.toHexString(this.bytes);
        }
        return "(Unknown attribute " + this.name + ": " + hex + ")";
    }
}

