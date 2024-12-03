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

public final class ModuleMainClass
extends Attribute {
    private int mainClassIndex;

    ModuleMainClass(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, 0, constantPool);
        this.mainClassIndex = input.readUnsignedShort();
    }

    public ModuleMainClass(int nameIndex, int length, int mainClassIndex, ConstantPool constantPool) {
        super((byte)26, nameIndex, length, constantPool);
        this.mainClassIndex = Args.requireU2(mainClassIndex, "mainClassIndex");
    }

    public ModuleMainClass(ModuleMainClass c) {
        this(c.getNameIndex(), c.getLength(), c.getHostClassIndex(), c.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        v.visitModuleMainClass(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        ModuleMainClass c = (ModuleMainClass)this.clone();
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.mainClassIndex);
    }

    public int getHostClassIndex() {
        return this.mainClassIndex;
    }

    public void setHostClassIndex(int mainClassIndex) {
        this.mainClassIndex = mainClassIndex;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("ModuleMainClass: ");
        String className = super.getConstantPool().getConstantString(this.mainClassIndex, (byte)7);
        buf.append(Utility.compactClassName(className, false));
        return buf.toString();
    }
}

