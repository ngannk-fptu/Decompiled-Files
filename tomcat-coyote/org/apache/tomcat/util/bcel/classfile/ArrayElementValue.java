/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import org.apache.tomcat.util.bcel.classfile.ClassFormatException;
import org.apache.tomcat.util.bcel.classfile.ConstantPool;
import org.apache.tomcat.util.bcel.classfile.ElementValue;

public class ArrayElementValue
extends ElementValue {
    private final ElementValue[] elementValues;

    ArrayElementValue(int type, ElementValue[] datums, ConstantPool cpool) {
        super(type, cpool);
        if (type != 91) {
            throw new ClassFormatException("Only element values of type array can be built with this ctor - type specified: " + type);
        }
        this.elementValues = datums;
    }

    public ElementValue[] getElementValuesArray() {
        return this.elementValues;
    }

    @Override
    public String stringifyValue() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < this.elementValues.length; ++i) {
            sb.append(this.elementValues[i].stringifyValue());
            if (i + 1 >= this.elementValues.length) continue;
            sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }
}

