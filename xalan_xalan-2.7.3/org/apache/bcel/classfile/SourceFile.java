/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public final class SourceFile
extends Attribute {
    private int sourceFileIndex;

    SourceFile(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, input.readUnsignedShort(), constantPool);
    }

    public SourceFile(int nameIndex, int length, int sourceFileIndex, ConstantPool constantPool) {
        super((byte)0, nameIndex, Args.require(length, 2, "SourceFile length attribute"), constantPool);
        this.sourceFileIndex = Args.requireU2(sourceFileIndex, 0, constantPool.getLength(), "SourceFile source file index");
    }

    public SourceFile(SourceFile c) {
        this(c.getNameIndex(), c.getLength(), c.getSourceFileIndex(), c.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        v.visitSourceFile(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        return (Attribute)this.clone();
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.sourceFileIndex);
    }

    public int getSourceFileIndex() {
        return this.sourceFileIndex;
    }

    public String getSourceFileName() {
        return super.getConstantPool().getConstantUtf8(this.sourceFileIndex).getBytes();
    }

    public void setSourceFileIndex(int sourceFileIndex) {
        this.sourceFileIndex = sourceFileIndex;
    }

    @Override
    public String toString() {
        return "SourceFile: " + this.getSourceFileName();
    }
}

