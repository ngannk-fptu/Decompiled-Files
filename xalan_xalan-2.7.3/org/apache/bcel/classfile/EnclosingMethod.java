/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public class EnclosingMethod
extends Attribute {
    private int classIndex;
    private int methodIndex;

    EnclosingMethod(int nameIndex, int len, DataInput input, ConstantPool cpool) throws IOException {
        this(nameIndex, len, input.readUnsignedShort(), input.readUnsignedShort(), cpool);
    }

    private EnclosingMethod(int nameIndex, int len, int classIndex, int methodIndex, ConstantPool cpool) {
        super((byte)18, nameIndex, Args.require(len, 4, "EnclosingMethod attribute length"), cpool);
        this.classIndex = Args.requireU2(classIndex, 0, cpool.getLength(), "EnclosingMethod class index");
        this.methodIndex = Args.requireU2(methodIndex, "EnclosingMethod method index");
    }

    @Override
    public void accept(Visitor v) {
        v.visitEnclosingMethod(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        return (Attribute)this.clone();
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.classIndex);
        file.writeShort(this.methodIndex);
    }

    public final ConstantClass getEnclosingClass() {
        return super.getConstantPool().getConstant(this.classIndex, (byte)7, ConstantClass.class);
    }

    public final int getEnclosingClassIndex() {
        return this.classIndex;
    }

    public final ConstantNameAndType getEnclosingMethod() {
        if (this.methodIndex == 0) {
            return null;
        }
        return super.getConstantPool().getConstant(this.methodIndex, (byte)12, ConstantNameAndType.class);
    }

    public final int getEnclosingMethodIndex() {
        return this.methodIndex;
    }

    public final void setEnclosingClassIndex(int idx) {
        this.classIndex = idx;
    }

    public final void setEnclosingMethodIndex(int idx) {
        this.methodIndex = idx;
    }
}

