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

public final class NestHost
extends Attribute {
    private int hostClassIndex;

    NestHost(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, 0, constantPool);
        this.hostClassIndex = input.readUnsignedShort();
    }

    public NestHost(int nameIndex, int length, int hostClassIndex, ConstantPool constantPool) {
        super((byte)26, nameIndex, length, constantPool);
        this.hostClassIndex = Args.requireU2(hostClassIndex, "hostClassIndex");
    }

    public NestHost(NestHost c) {
        this(c.getNameIndex(), c.getLength(), c.getHostClassIndex(), c.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        v.visitNestHost(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        NestHost c = (NestHost)this.clone();
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.hostClassIndex);
    }

    public int getHostClassIndex() {
        return this.hostClassIndex;
    }

    public void setHostClassIndex(int hostClassIndex) {
        this.hostClassIndex = hostClassIndex;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("NestHost: ");
        String className = super.getConstantPool().getConstantString(this.hostClassIndex, (byte)7);
        buf.append(Utility.compactClassName(className, false));
        return buf.toString();
    }
}

