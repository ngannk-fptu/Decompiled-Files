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

public final class ConstantLong
extends Constant
implements SimpleConstant {
    private long longValue;

    public ConstantLong(long longValue) {
        super((byte)5);
        this.longValue = longValue;
    }

    ConstantLong(DataInputStream file) throws IOException {
        this(file.readLong());
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantLong(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        file.writeByte(this.tag);
        file.writeLong(this.longValue);
    }

    @Override
    public final Long getValue() {
        return this.longValue;
    }

    @Override
    public final String getStringValue() {
        return Long.toString(this.longValue);
    }

    @Override
    public final String toString() {
        return super.toString() + "(longValue = " + this.longValue + ")";
    }
}

