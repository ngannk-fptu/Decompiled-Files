/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantCP;

public final class ConstantMethodref
extends ConstantCP {
    ConstantMethodref(DataInputStream file) throws IOException {
        super((byte)10, file);
    }

    public ConstantMethodref(int class_index, int name_and_type_index) {
        super((byte)10, class_index, name_and_type_index);
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantMethodref(this);
    }

    @Override
    public String getValue() {
        return this.toString();
    }
}

