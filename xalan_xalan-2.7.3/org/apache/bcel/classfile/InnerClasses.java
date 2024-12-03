/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public final class InnerClasses
extends Attribute
implements Iterable<InnerClass> {
    private static final InnerClass[] EMPTY_INNER_CLASSE_ARRAY = new InnerClass[0];
    private InnerClass[] innerClasses;

    public InnerClasses(InnerClasses c) {
        this(c.getNameIndex(), c.getLength(), c.getInnerClasses(), c.getConstantPool());
    }

    InnerClasses(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, (InnerClass[])null, constantPool);
        int classCount = input.readUnsignedShort();
        this.innerClasses = new InnerClass[classCount];
        for (int i = 0; i < classCount; ++i) {
            this.innerClasses[i] = new InnerClass(input);
        }
    }

    public InnerClasses(int nameIndex, int length, InnerClass[] innerClasses, ConstantPool constantPool) {
        super((byte)6, nameIndex, length, constantPool);
        this.innerClasses = innerClasses != null ? innerClasses : EMPTY_INNER_CLASSE_ARRAY;
        Args.requireU2(this.innerClasses.length, "innerClasses.length");
    }

    @Override
    public void accept(Visitor v) {
        v.visitInnerClasses(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        InnerClasses c = (InnerClasses)this.clone();
        c.innerClasses = new InnerClass[this.innerClasses.length];
        Arrays.setAll(c.innerClasses, i -> this.innerClasses[i].copy());
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.innerClasses.length);
        for (InnerClass innerClass : this.innerClasses) {
            innerClass.dump(file);
        }
    }

    public InnerClass[] getInnerClasses() {
        return this.innerClasses;
    }

    @Override
    public Iterator<InnerClass> iterator() {
        return Stream.of(this.innerClasses).iterator();
    }

    public void setInnerClasses(InnerClass[] innerClasses) {
        this.innerClasses = innerClasses != null ? innerClasses : EMPTY_INNER_CLASSE_ARRAY;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("InnerClasses(");
        buf.append(this.innerClasses.length);
        buf.append("):\n");
        for (InnerClass innerClass : this.innerClasses) {
            buf.append(innerClass.toString(super.getConstantPool())).append("\n");
        }
        return buf.substring(0, buf.length() - 1);
    }
}

