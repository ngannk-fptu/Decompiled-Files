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
import org.aspectj.apache.bcel.classfile.InnerClass;

public final class InnerClasses
extends Attribute {
    private InnerClass[] inner_classes;
    private int number_of_classes;

    public InnerClasses(InnerClasses c) {
        this(c.getNameIndex(), c.getLength(), c.getInnerClasses(), c.getConstantPool());
    }

    public InnerClasses(int name_index, int length, InnerClass[] inner_classes, ConstantPool constant_pool) {
        super((byte)6, name_index, length, constant_pool);
        this.setInnerClasses(inner_classes);
    }

    InnerClasses(int name_index, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        this(name_index, length, (InnerClass[])null, constant_pool);
        this.number_of_classes = file.readUnsignedShort();
        this.inner_classes = new InnerClass[this.number_of_classes];
        for (int i = 0; i < this.number_of_classes; ++i) {
            this.inner_classes[i] = new InnerClass(file);
        }
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitInnerClasses(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.number_of_classes);
        for (int i = 0; i < this.number_of_classes; ++i) {
            this.inner_classes[i].dump(file);
        }
    }

    public final InnerClass[] getInnerClasses() {
        return this.inner_classes;
    }

    public final void setInnerClasses(InnerClass[] inner_classes) {
        this.inner_classes = inner_classes;
        this.number_of_classes = inner_classes == null ? 0 : inner_classes.length;
    }

    @Override
    public final String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.number_of_classes; ++i) {
            buf.append(this.inner_classes[i].toString(this.cpool) + "\n");
        }
        return buf.toString();
    }
}

