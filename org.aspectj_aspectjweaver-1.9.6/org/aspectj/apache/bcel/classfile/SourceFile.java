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

public final class SourceFile
extends Attribute {
    private int sourcefile_index;

    public SourceFile(SourceFile c) {
        this(c.getNameIndex(), c.getLength(), c.getSourceFileIndex(), c.getConstantPool());
    }

    SourceFile(int name_index, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        this(name_index, length, file.readUnsignedShort(), constant_pool);
    }

    public SourceFile(int name_index, int length, int sourcefile_index, ConstantPool constant_pool) {
        super((byte)0, name_index, length, constant_pool);
        this.sourcefile_index = sourcefile_index;
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitSourceFile(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.sourcefile_index);
    }

    public final int getSourceFileIndex() {
        return this.sourcefile_index;
    }

    public final void setSourceFileIndex(int sourcefile_index) {
        this.sourcefile_index = sourcefile_index;
    }

    public final String getSourceFileName() {
        ConstantUtf8 c = (ConstantUtf8)this.cpool.getConstant(this.sourcefile_index, (byte)1);
        return c.getValue();
    }

    @Override
    public final String toString() {
        return "SourceFile(" + this.getSourceFileName() + ")";
    }
}

