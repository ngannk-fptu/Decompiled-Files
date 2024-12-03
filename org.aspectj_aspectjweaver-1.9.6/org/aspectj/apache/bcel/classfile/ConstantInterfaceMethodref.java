/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantCP;

public final class ConstantInterfaceMethodref
extends ConstantCP {
    ConstantInterfaceMethodref(DataInputStream file) throws IOException {
        super((byte)11, file);
    }

    public ConstantInterfaceMethodref(int classIndex, int nameAndTypeIndex) {
        super((byte)11, classIndex, nameAndTypeIndex);
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantInterfaceMethodref(this);
    }

    @Override
    public String getValue() {
        return this.toString();
    }
}

