/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.Visitor;

public final class ConstantInterfaceMethodref
extends ConstantCP {
    public ConstantInterfaceMethodref(ConstantInterfaceMethodref c) {
        super((byte)11, c.getClassIndex(), c.getNameAndTypeIndex());
    }

    ConstantInterfaceMethodref(DataInput input) throws IOException {
        super((byte)11, input);
    }

    public ConstantInterfaceMethodref(int classIndex, int nameAndTypeIndex) {
        super((byte)11, classIndex, nameAndTypeIndex);
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantInterfaceMethodref(this);
    }
}

