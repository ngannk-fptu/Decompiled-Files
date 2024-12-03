/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import org.apache.tomcat.util.bcel.classfile.ClassFormatException;
import org.apache.tomcat.util.bcel.classfile.ConstantPool;
import org.apache.tomcat.util.bcel.classfile.ElementValue;

public class EnumElementValue
extends ElementValue {
    private final int valueIdx;

    EnumElementValue(int type, int valueIdx, ConstantPool cpool) {
        super(type, cpool);
        if (type != 101) {
            throw new ClassFormatException("Only element values of type enum can be built with this ctor - type specified: " + type);
        }
        this.valueIdx = valueIdx;
    }

    @Override
    public String stringifyValue() {
        return super.getConstantPool().getConstantUtf8(this.valueIdx).getBytes();
    }
}

