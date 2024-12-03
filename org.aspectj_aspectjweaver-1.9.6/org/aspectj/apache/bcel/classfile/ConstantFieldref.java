/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantCP;

public final class ConstantFieldref
extends ConstantCP {
    ConstantFieldref(DataInputStream file) throws IOException {
        super((byte)9, file);
    }

    public ConstantFieldref(int class_index, int name_and_type_index) {
        super((byte)9, class_index, name_and_type_index);
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantFieldref(this);
    }

    @Override
    public String getValue() {
        return this.toString();
    }
}

