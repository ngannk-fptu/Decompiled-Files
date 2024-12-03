/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ElementValue;

public class ClassElementValue
extends ElementValue {
    private final int idx;

    public ClassElementValue(int type, int idx, ConstantPool cpool) {
        super(type, cpool);
        this.idx = idx;
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        dos.writeByte(super.getType());
        dos.writeShort(this.idx);
    }

    public String getClassString() {
        return super.getConstantPool().getConstantUtf8(this.idx).getBytes();
    }

    public int getIndex() {
        return this.idx;
    }

    @Override
    public String stringifyValue() {
        return super.getConstantPool().getConstantUtf8(this.idx).getBytes();
    }
}

