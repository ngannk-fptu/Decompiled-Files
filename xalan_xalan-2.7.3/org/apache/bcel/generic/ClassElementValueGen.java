/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ClassElementValue;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ElementValueGen;
import org.apache.bcel.generic.ObjectType;

public class ClassElementValueGen
extends ElementValueGen {
    private final int idx;

    public ClassElementValueGen(ClassElementValue value, ConstantPoolGen cpool, boolean copyPoolEntries) {
        super(99, cpool);
        this.idx = copyPoolEntries ? cpool.addUtf8(value.getClassString()) : value.getIndex();
    }

    protected ClassElementValueGen(int typeIdx, ConstantPoolGen cpool) {
        super(99, cpool);
        this.idx = typeIdx;
    }

    public ClassElementValueGen(ObjectType t, ConstantPoolGen cpool) {
        super(99, cpool);
        this.idx = cpool.addUtf8(t.getSignature());
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        dos.writeByte(super.getElementValueType());
        dos.writeShort(this.idx);
    }

    public String getClassString() {
        ConstantUtf8 cu8 = (ConstantUtf8)this.getConstantPool().getConstant(this.idx);
        return cu8.getBytes();
    }

    @Override
    public ElementValue getElementValue() {
        return new ClassElementValue(super.getElementValueType(), this.idx, this.getConstantPool().getConstantPool());
    }

    public int getIndex() {
        return this.idx;
    }

    @Override
    public String stringifyValue() {
        return this.getClassString();
    }
}

