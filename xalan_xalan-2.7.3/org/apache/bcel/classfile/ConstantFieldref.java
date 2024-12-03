/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.Visitor;

public final class ConstantFieldref
extends ConstantCP {
    public ConstantFieldref(ConstantFieldref c) {
        super((byte)9, c.getClassIndex(), c.getNameAndTypeIndex());
    }

    ConstantFieldref(DataInput input) throws IOException {
        super((byte)9, input);
    }

    public ConstantFieldref(int classIndex, int nameAndTypeIndex) {
        super((byte)9, classIndex, nameAndTypeIndex);
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantFieldref(this);
    }
}

