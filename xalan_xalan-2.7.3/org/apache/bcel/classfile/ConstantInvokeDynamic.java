/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.Visitor;

public final class ConstantInvokeDynamic
extends ConstantCP {
    public ConstantInvokeDynamic(ConstantInvokeDynamic c) {
        this(c.getBootstrapMethodAttrIndex(), c.getNameAndTypeIndex());
    }

    ConstantInvokeDynamic(DataInput file) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort());
    }

    public ConstantInvokeDynamic(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
        super((byte)18, bootstrapMethodAttrIndex, nameAndTypeIndex);
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantInvokeDynamic(this);
    }

    public int getBootstrapMethodAttrIndex() {
        return super.getClassIndex();
    }

    @Override
    public String toString() {
        return super.toString().replace("class_index", "bootstrap_method_attr_index");
    }
}

