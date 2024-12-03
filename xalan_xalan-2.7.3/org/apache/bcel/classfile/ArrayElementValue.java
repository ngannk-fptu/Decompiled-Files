/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ElementValue;

public class ArrayElementValue
extends ElementValue {
    private final ElementValue[] elementValues;

    public ArrayElementValue(int type, ElementValue[] datums, ConstantPool cpool) {
        super(type, cpool);
        if (type != 91) {
            throw new ClassFormatException("Only element values of type array can be built with this ctor - type specified: " + type);
        }
        this.elementValues = datums;
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        dos.writeByte(super.getType());
        dos.writeShort(this.elementValues.length);
        for (ElementValue evalue : this.elementValues) {
            evalue.dump(dos);
        }
    }

    public ElementValue[] getElementValuesArray() {
        return this.elementValues;
    }

    public int getElementValuesArraySize() {
        return this.elementValues.length;
    }

    @Override
    public String stringifyValue() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < this.elementValues.length; ++i) {
            sb.append(this.elementValues[i].stringifyValue());
            if (i + 1 >= this.elementValues.length) continue;
            sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < this.elementValues.length; ++i) {
            sb.append(this.elementValues[i]);
            if (i + 1 >= this.elementValues.length) continue;
            sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }
}

