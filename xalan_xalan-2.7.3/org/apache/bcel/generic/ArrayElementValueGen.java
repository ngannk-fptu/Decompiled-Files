/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.bcel.classfile.ArrayElementValue;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ElementValueGen;

public class ArrayElementValueGen
extends ElementValueGen {
    private final List<ElementValueGen> evalues;

    public ArrayElementValueGen(ArrayElementValue value, ConstantPoolGen cpool, boolean copyPoolEntries) {
        super(91, cpool);
        ElementValue[] in;
        this.evalues = new ArrayList<ElementValueGen>();
        for (ElementValue element : in = value.getElementValuesArray()) {
            this.evalues.add(ElementValueGen.copy(element, cpool, copyPoolEntries));
        }
    }

    public ArrayElementValueGen(ConstantPoolGen cp) {
        super(91, cp);
        this.evalues = new ArrayList<ElementValueGen>();
    }

    public ArrayElementValueGen(int type, ElementValue[] datums, ConstantPoolGen cpool) {
        super(type, cpool);
        if (type != 91) {
            throw new IllegalArgumentException("Only element values of type array can be built with this ctor - type specified: " + type);
        }
        this.evalues = new ArrayList<ElementValueGen>();
        for (ElementValue datum : datums) {
            this.evalues.add(ElementValueGen.copy(datum, cpool, true));
        }
    }

    public void addElement(ElementValueGen gen) {
        this.evalues.add(gen);
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        dos.writeByte(super.getElementValueType());
        dos.writeShort(this.evalues.size());
        for (ElementValueGen element : this.evalues) {
            element.dump(dos);
        }
    }

    @Override
    public ElementValue getElementValue() {
        ElementValue[] immutableData = new ElementValue[this.evalues.size()];
        int i = 0;
        for (ElementValueGen element : this.evalues) {
            immutableData[i++] = element.getElementValue();
        }
        return new ArrayElementValue(super.getElementValueType(), immutableData, this.getConstantPool().getConstantPool());
    }

    public List<ElementValueGen> getElementValues() {
        return this.evalues;
    }

    public int getElementValuesSize() {
        return this.evalues.size();
    }

    @Override
    public String stringifyValue() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String comma = "";
        for (ElementValueGen element : this.evalues) {
            sb.append(comma);
            comma = ",";
            sb.append(element.stringifyValue());
        }
        sb.append("]");
        return sb.toString();
    }
}

