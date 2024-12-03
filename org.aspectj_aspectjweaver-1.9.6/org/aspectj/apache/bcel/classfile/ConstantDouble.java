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

public final class ConstantDouble
extends Constant
implements SimpleConstant {
    private double value;

    public ConstantDouble(double value) {
        super((byte)6);
        this.value = value;
    }

    ConstantDouble(DataInputStream file) throws IOException {
        this(file.readDouble());
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantDouble(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeDouble(this.value);
    }

    @Override
    public final String toString() {
        return super.toString() + "(bytes = " + this.value + ")";
    }

    @Override
    public Double getValue() {
        return this.value;
    }

    @Override
    public String getStringValue() {
        return Double.toString(this.value);
    }
}

