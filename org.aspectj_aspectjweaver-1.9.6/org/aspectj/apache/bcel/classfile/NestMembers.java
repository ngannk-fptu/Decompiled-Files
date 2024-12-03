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

public final class NestMembers
extends Attribute {
    private int numberOfClasses;
    private int[] classes;

    public NestMembers(NestMembers c) {
        this(c.getNameIndex(), c.getLength(), c.getClasses(), c.getConstantPool());
    }

    public NestMembers(int nameIndex, int length, int[] classes, ConstantPool cp) {
        super((byte)27, nameIndex, length, cp);
        this.setClasses(classes);
    }

    NestMembers(int nameIndex, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        this(nameIndex, length, (int[])null, constant_pool);
        this.numberOfClasses = file.readUnsignedShort();
        this.classes = new int[this.numberOfClasses];
        for (int i = 0; i < this.numberOfClasses; ++i) {
            this.classes[i] = file.readUnsignedShort();
        }
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitNestMembers(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.numberOfClasses);
        for (int i = 0; i < this.numberOfClasses; ++i) {
            file.writeShort(this.classes[i]);
        }
    }

    public final int[] getClasses() {
        return this.classes;
    }

    public final void setClasses(int[] inner_classes) {
        this.classes = inner_classes;
        this.numberOfClasses = inner_classes == null ? 0 : inner_classes.length;
    }

    public final String[] getClassesNames() {
        String[] result = new String[this.numberOfClasses];
        for (int i = 0; i < this.numberOfClasses; ++i) {
            ConstantClass constantClass = (ConstantClass)this.cpool.getConstant(this.classes[i], (byte)7);
            result[i] = constantClass.getClassname(this.cpool);
        }
        return result;
    }

    @Override
    public final String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.numberOfClasses; ++i) {
            ConstantClass constantClass = (ConstantClass)this.cpool.getConstant(this.classes[i], (byte)7);
            buf.append(constantClass.getClassname(this.cpool)).append(" ");
        }
        return "NestMembers(" + buf.toString().trim() + ")";
    }
}

