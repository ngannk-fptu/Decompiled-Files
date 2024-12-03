/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public final class Deprecated
extends Attribute {
    private byte[] bytes;

    public Deprecated(Deprecated c) {
        this(c.getNameIndex(), c.getLength(), c.getBytes(), c.getConstantPool());
    }

    public Deprecated(int nameIndex, int length, byte[] bytes, ConstantPool constantPool) {
        super((byte)8, nameIndex, Args.require0(length, "Deprecated attribute length"), constantPool);
        this.bytes = bytes;
    }

    Deprecated(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, (byte[])null, constantPool);
        if (length > 0) {
            this.bytes = new byte[length];
            input.readFully(this.bytes);
            Deprecated.println("Deprecated attribute with length > 0");
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitDeprecated(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        Deprecated c = (Deprecated)this.clone();
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
        return Const.getAttributeName(8) + ": true";
    }
}

