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

public final class ConstantInteger
extends Constant
implements SimpleConstant {
    private int intValue;

    public ConstantInteger(int intValue) {
        super((byte)3);
        this.intValue = intValue;
    }

    ConstantInteger(DataInputStream file) throws IOException {
        this(file.readInt());
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantInteger(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeInt(this.intValue);
    }

    @Override
    public final String toString() {
        return super.toString() + "(bytes = " + this.intValue + ")";
    }

    @Override
    public Integer getValue() {
        return this.intValue;
    }

    public int getIntValue() {
        return this.intValue;
    }

    @Override
    public String getStringValue() {
        return Integer.toString(this.intValue);
    }
}

