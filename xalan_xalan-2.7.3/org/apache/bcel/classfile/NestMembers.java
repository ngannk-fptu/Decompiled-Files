/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
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
import org.apache.bcel.util.Args;
import org.apache.commons.lang3.ArrayUtils;

public final class NestMembers
extends Attribute {
    private int[] classes;

    NestMembers(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, (int[])null, constantPool);
        int classCount = input.readUnsignedShort();
        this.classes = new int[classCount];
        for (int i = 0; i < classCount; ++i) {
            this.classes[i] = input.readUnsignedShort();
        }
    }

    public NestMembers(int nameIndex, int length, int[] classes, ConstantPool constantPool) {
        super((byte)26, nameIndex, length, constantPool);
        this.classes = classes != null ? classes : ArrayUtils.EMPTY_INT_ARRAY;
        Args.requireU2(this.classes.length, "classes.length");
    }

    public NestMembers(NestMembers c) {
        this(c.getNameIndex(), c.getLength(), c.getClasses(), c.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        v.visitNestMembers(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        NestMembers c = (NestMembers)this.clone();
        if (this.classes.length > 0) {
            c.classes = (int[])this.classes.clone();
        }
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.classes.length);
        for (int index : this.classes) {
            file.writeShort(index);
        }
    }

    public int[] getClasses() {
        return this.classes;
    }

    public String[] getClassNames() {
        String[] names = new String[this.classes.length];
        Arrays.setAll(names, i -> Utility.pathToPackage(super.getConstantPool().getConstantString(this.classes[i], (byte)7)));
        return names;
    }

    public int getNumberClasses() {
        return this.classes.length;
    }

    public void setClasses(int[] classes) {
        this.classes = classes != null ? classes : ArrayUtils.EMPTY_INT_ARRAY;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("NestMembers(");
        buf.append(this.classes.length);
        buf.append("):\n");
        for (int index : this.classes) {
            String className = super.getConstantPool().getConstantString(index, (byte)7);
            buf.append("  ").append(Utility.compactClassName(className, false)).append("\n");
        }
        return buf.substring(0, buf.length() - 1);
    }
}

