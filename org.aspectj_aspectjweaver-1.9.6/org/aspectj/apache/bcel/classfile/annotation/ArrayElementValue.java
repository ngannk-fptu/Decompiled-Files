/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile.annotation;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.annotation.ElementValue;

public class ArrayElementValue
extends ElementValue {
    private static final ElementValue[] NO_VALUES = new ElementValue[0];
    private ElementValue[] evalues = NO_VALUES;

    public ElementValue[] getElementValuesArray() {
        return this.evalues;
    }

    public int getElementValuesArraySize() {
        return this.evalues.length;
    }

    public ArrayElementValue(ConstantPool cp) {
        super(91, cp);
    }

    public ArrayElementValue(int type, ElementValue[] datums, ConstantPool cpool) {
        super(type, cpool);
        if (type != 91) {
            throw new RuntimeException("Only element values of type array can be built with this ctor");
        }
        this.evalues = datums;
    }

    public ArrayElementValue(ArrayElementValue value, ConstantPool cpool, boolean copyPoolEntries) {
        super(91, cpool);
        this.evalues = new ElementValue[value.getElementValuesArraySize()];
        ElementValue[] in = value.getElementValuesArray();
        for (int i = 0; i < in.length; ++i) {
            this.evalues[i] = ElementValue.copy(in[i], cpool, copyPoolEntries);
        }
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        dos.writeByte(this.type);
        dos.writeShort(this.evalues.length);
        for (int i = 0; i < this.evalues.length; ++i) {
            this.evalues[i].dump(dos);
        }
    }

    @Override
    public String stringifyValue() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < this.evalues.length; ++i) {
            ElementValue element = this.evalues[i];
            sb.append(element.stringifyValue());
            if (i + 1 >= this.evalues.length) continue;
            sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public void addElement(ElementValue gen) {
        ElementValue[] old = this.evalues;
        this.evalues = new ElementValue[this.evalues.length + 1];
        System.arraycopy(old, 0, this.evalues, 0, old.length);
        this.evalues[old.length] = gen;
    }
}

