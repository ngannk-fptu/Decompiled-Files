/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ElementValueGen;

public class ElementValuePairGen {
    private final int nameIdx;
    private final ElementValueGen value;
    private final ConstantPoolGen constantPoolGen;

    public ElementValuePairGen(ElementValuePair nvp, ConstantPoolGen cpool, boolean copyPoolEntries) {
        this.constantPoolGen = cpool;
        this.nameIdx = copyPoolEntries ? cpool.addUtf8(nvp.getNameString()) : nvp.getNameIndex();
        this.value = ElementValueGen.copy(nvp.getValue(), cpool, copyPoolEntries);
    }

    protected ElementValuePairGen(int idx, ElementValueGen value, ConstantPoolGen cpool) {
        this.nameIdx = idx;
        this.value = value;
        this.constantPoolGen = cpool;
    }

    public ElementValuePairGen(String name, ElementValueGen value, ConstantPoolGen cpool) {
        this.nameIdx = cpool.addUtf8(name);
        this.value = value;
        this.constantPoolGen = cpool;
    }

    protected void dump(DataOutputStream dos) throws IOException {
        dos.writeShort(this.nameIdx);
        this.value.dump(dos);
    }

    public ElementValuePair getElementNameValuePair() {
        ElementValue immutableValue = this.value.getElementValue();
        return new ElementValuePair(this.nameIdx, immutableValue, this.constantPoolGen.getConstantPool());
    }

    public int getNameIndex() {
        return this.nameIdx;
    }

    public final String getNameString() {
        return ((ConstantUtf8)this.constantPoolGen.getConstant(this.nameIdx)).getBytes();
    }

    public final ElementValueGen getValue() {
        return this.value;
    }

    public String toString() {
        return "ElementValuePair:[" + this.getNameString() + "=" + this.value.stringifyValue() + "]";
    }
}

