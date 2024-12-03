/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.Visitor;

public final class ConstantDynamic
extends ConstantCP {
    public ConstantDynamic(ConstantDynamic c) {
        this(c.getBootstrapMethodAttrIndex(), c.getNameAndTypeIndex());
    }

    ConstantDynamic(DataInput file) throws IOException {
        this(file.readShort(), file.readShort());
    }

    public ConstantDynamic(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
        super((byte)17, bootstrapMethodAttrIndex, nameAndTypeIndex);
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantDynamic(this);
    }

    public int getBootstrapMethodAttrIndex() {
        return super.getClassIndex();
    }

    @Override
    public String toString() {
        return super.toString().replace("class_index", "bootstrap_method_attr_index");
    }
}

