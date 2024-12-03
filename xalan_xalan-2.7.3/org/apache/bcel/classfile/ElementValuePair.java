/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ElementValue;

public class ElementValuePair {
    static final ElementValuePair[] EMPTY_ARRAY = new ElementValuePair[0];
    private final ElementValue elementValue;
    private final ConstantPool constantPool;
    private final int elementNameIndex;

    public ElementValuePair(int elementNameIndex, ElementValue elementValue, ConstantPool constantPool) {
        this.elementValue = elementValue;
        this.elementNameIndex = elementNameIndex;
        this.constantPool = constantPool;
    }

    protected void dump(DataOutputStream dos) throws IOException {
        dos.writeShort(this.elementNameIndex);
        this.elementValue.dump(dos);
    }

    public int getNameIndex() {
        return this.elementNameIndex;
    }

    public String getNameString() {
        return this.constantPool.getConstantUtf8(this.elementNameIndex).getBytes();
    }

    public final ElementValue getValue() {
        return this.elementValue;
    }

    public String toShortString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getNameString()).append("=").append(this.getValue().toShortString());
        return result.toString();
    }
}

