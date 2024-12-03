/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ElementValue;

public class EnumElementValue
extends ElementValue {
    private final int typeIdx;
    private final int valueIdx;

    public EnumElementValue(int type, int typeIdx, int valueIdx, ConstantPool cpool) {
        super(type, cpool);
        if (type != 101) {
            throw new ClassFormatException("Only element values of type enum can be built with this ctor - type specified: " + type);
        }
        this.typeIdx = typeIdx;
        this.valueIdx = valueIdx;
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        dos.writeByte(super.getType());
        dos.writeShort(this.typeIdx);
        dos.writeShort(this.valueIdx);
    }

    public String getEnumTypeString() {
        return super.getConstantPool().getConstantUtf8(this.typeIdx).getBytes();
    }

    public String getEnumValueString() {
        return super.getConstantPool().getConstantUtf8(this.valueIdx).getBytes();
    }

    public int getTypeIndex() {
        return this.typeIdx;
    }

    public int getValueIndex() {
        return this.valueIdx;
    }

    @Override
    public String stringifyValue() {
        return super.getConstantPool().getConstantUtf8(this.valueIdx).getBytes();
    }
}

