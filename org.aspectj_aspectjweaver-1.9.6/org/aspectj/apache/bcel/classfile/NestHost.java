/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantClass;
import org.aspectj.apache.bcel.classfile.ConstantPool;

public final class NestHost
extends Attribute {
    private int hostClassIndex;

    public NestHost(NestHost c) {
        this(c.getNameIndex(), c.getLength(), c.getHostClassIndex(), c.getConstantPool());
    }

    public NestHost(int nameIndex, int length, int hostClassIndex, ConstantPool cp) {
        super((byte)27, nameIndex, length, cp);
        this.hostClassIndex = hostClassIndex;
    }

    NestHost(int nameIndex, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        this(nameIndex, length, 0, constant_pool);
        this.hostClassIndex = file.readUnsignedShort();
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitNestHost(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.hostClassIndex);
    }

    public final int getHostClassIndex() {
        return this.hostClassIndex;
    }

    public final void setHostClassIndex(int hostClassIndex) {
        this.hostClassIndex = hostClassIndex;
    }

    public final String getHostClassName() {
        ConstantClass constantClass = (ConstantClass)this.cpool.getConstant(this.hostClassIndex, (byte)7);
        return constantClass.getClassname(this.cpool);
    }

    @Override
    public final String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("NestHost(");
        ConstantClass constantClass = (ConstantClass)this.cpool.getConstant(this.hostClassIndex, (byte)7);
        buf.append(constantClass.getClassname(this.cpool));
        buf.append(")");
        return buf.toString();
    }
}

