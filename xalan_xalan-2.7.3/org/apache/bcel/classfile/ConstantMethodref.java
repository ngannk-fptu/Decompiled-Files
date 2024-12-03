/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.Visitor;

public final class ConstantMethodref
extends ConstantCP {
    public ConstantMethodref(ConstantMethodref c) {
        super((byte)10, c.getClassIndex(), c.getNameAndTypeIndex());
    }

    ConstantMethodref(DataInput input) throws IOException {
        super((byte)10, input);
    }

    public ConstantMethodref(int classIndex, int nameAndTypeIndex) {
        super((byte)10, classIndex, nameAndTypeIndex);
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantMethodref(this);
    }
}

