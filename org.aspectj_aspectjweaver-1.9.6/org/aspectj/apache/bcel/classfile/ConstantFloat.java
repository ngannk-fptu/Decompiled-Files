/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.Constant;
import org.aspectj.apache.bcel.classfile.SimpleConstant;

public final class ConstantFloat
extends Constant
implements SimpleConstant {
    private float floatValue;

    public ConstantFloat(float floatValue) {
        super((byte)4);
        this.floatValue = floatValue;
    }

    ConstantFloat(DataInputStream file) throws IOException {
        this(file.readFloat());
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantFloat(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeFloat(this.floatValue);
    }

    @Override
    public final Float getValue() {
        return Float.valueOf(this.floatValue);
    }

    @Override
    public final String getStringValue() {
        return Float.toString(this.floatValue);
    }

    @Override
    public final String toString() {
        return super.toString() + "(bytes = " + this.floatValue + ")";
    }
}

