/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile.annotation;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.classfile.annotation.ElementValue;
import org.aspectj.apache.bcel.generic.ObjectType;

public class ClassElementValue
extends ElementValue {
    private int idx;

    protected ClassElementValue(int typeIdx, ConstantPool cpool) {
        super(99, cpool);
        this.idx = typeIdx;
    }

    public ClassElementValue(ObjectType t, ConstantPool cpool) {
        super(99, cpool);
        this.idx = cpool.addUtf8(t.getSignature());
    }

    public ClassElementValue(ClassElementValue value, ConstantPool cpool, boolean copyPoolEntries) {
        super(99, cpool);
        this.idx = copyPoolEntries ? cpool.addUtf8(value.getClassString()) : value.getIndex();
    }

    public int getIndex() {
        return this.idx;
    }

    public String getClassString() {
        ConstantUtf8 cu8 = (ConstantUtf8)this.getConstantPool().getConstant(this.idx);
        return cu8.getValue();
    }

    @Override
    public String stringifyValue() {
        return this.getClassString();
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        dos.writeByte(this.type);
        dos.writeShort(this.idx);
    }
}

