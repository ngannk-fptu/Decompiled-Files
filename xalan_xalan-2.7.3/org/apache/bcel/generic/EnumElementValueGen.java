/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.EnumElementValue;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ElementValueGen;
import org.apache.bcel.generic.ObjectType;

public class EnumElementValueGen
extends ElementValueGen {
    private final int typeIdx;
    private final int valueIdx;

    public EnumElementValueGen(EnumElementValue value, ConstantPoolGen cpool, boolean copyPoolEntries) {
        super(101, cpool);
        if (copyPoolEntries) {
            this.typeIdx = cpool.addUtf8(value.getEnumTypeString());
            this.valueIdx = cpool.addUtf8(value.getEnumValueString());
        } else {
            this.typeIdx = value.getTypeIndex();
            this.valueIdx = value.getValueIndex();
        }
    }

    protected EnumElementValueGen(int typeIdx, int valueIdx, ConstantPoolGen cpool) {
        super(101, cpool);
        if (super.getElementValueType() != 101) {
            throw new IllegalArgumentException("Only element values of type enum can be built with this ctor - type specified: " + super.getElementValueType());
        }
        this.typeIdx = typeIdx;
        this.valueIdx = valueIdx;
    }

    public EnumElementValueGen(ObjectType t, String value, ConstantPoolGen cpool) {
        super(101, cpool);
        this.typeIdx = cpool.addUtf8(t.getSignature());
        this.valueIdx = cpool.addUtf8(value);
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        dos.writeByte(super.getElementValueType());
        dos.writeShort(this.typeIdx);
        dos.writeShort(this.valueIdx);
    }

    @Override
    public ElementValue getElementValue() {
        System.err.println("Duplicating value: " + this.getEnumTypeString() + ":" + this.getEnumValueString());
        return new EnumElementValue(super.getElementValueType(), this.typeIdx, this.valueIdx, this.getConstantPool().getConstantPool());
    }

    public String getEnumTypeString() {
        return ((ConstantUtf8)this.getConstantPool().getConstant(this.typeIdx)).getBytes();
    }

    public String getEnumValueString() {
        return ((ConstantUtf8)this.getConstantPool().getConstant(this.valueIdx)).getBytes();
    }

    public int getTypeIndex() {
        return this.typeIdx;
    }

    public int getValueIndex() {
        return this.valueIdx;
    }

    @Override
    public String stringifyValue() {
        ConstantUtf8 cu8 = (ConstantUtf8)this.getConstantPool().getConstant(this.valueIdx);
        return cu8.getBytes();
    }
}

