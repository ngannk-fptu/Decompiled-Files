/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public final class Synthetic
extends Attribute {
    private byte[] bytes;

    public Synthetic(int nameIndex, int length, byte[] bytes, ConstantPool constantPool) {
        super((byte)7, nameIndex, Args.require0(length, "Synthetic attribute length"), constantPool);
        this.bytes = bytes;
    }

    Synthetic(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, (byte[])null, constantPool);
        if (length > 0) {
            this.bytes = new byte[length];
            input.readFully(this.bytes);
            Synthetic.println("Synthetic attribute with length > 0");
        }
    }

    public Synthetic(Synthetic c) {
        this(c.getNameIndex(), c.getLength(), c.getBytes(), c.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        v.visitSynthetic(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        Synthetic c = (Synthetic)this.clone();
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

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("Synthetic");
        if (super.getLength() > 0) {
            buf.append(" ").append(Utility.toHexString(this.bytes));
        }
        return buf.toString();
    }
}

