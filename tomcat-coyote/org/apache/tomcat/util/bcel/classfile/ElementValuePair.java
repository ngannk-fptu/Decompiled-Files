/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.classfile.ConstantPool;
import org.apache.tomcat.util.bcel.classfile.ConstantUtf8;
import org.apache.tomcat.util.bcel.classfile.ElementValue;

public class ElementValuePair {
    private final ElementValue elementValue;
    private final ConstantPool constantPool;
    private final int elementNameIndex;

    ElementValuePair(DataInput file, ConstantPool constantPool) throws IOException {
        this.constantPool = constantPool;
        this.elementNameIndex = file.readUnsignedShort();
        this.elementValue = ElementValue.readElementValue(file, constantPool);
    }

    public String getNameString() {
        ConstantUtf8 c = (ConstantUtf8)this.constantPool.getConstant(this.elementNameIndex, (byte)1);
        return c.getBytes();
    }

    public final ElementValue getValue() {
        return this.elementValue;
    }
}

