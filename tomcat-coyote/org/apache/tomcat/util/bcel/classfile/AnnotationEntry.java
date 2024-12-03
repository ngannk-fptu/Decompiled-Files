/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.bcel.classfile.ConstantPool;
import org.apache.tomcat.util.bcel.classfile.ElementValuePair;

public class AnnotationEntry {
    static final AnnotationEntry[] EMPTY_ARRAY = new AnnotationEntry[0];
    private final int typeIndex;
    private final ConstantPool constantPool;
    private final List<ElementValuePair> elementValuePairs;

    AnnotationEntry(DataInput input, ConstantPool constantPool) throws IOException {
        this.constantPool = constantPool;
        this.typeIndex = input.readUnsignedShort();
        int numElementValuePairs = input.readUnsignedShort();
        this.elementValuePairs = new ArrayList<ElementValuePair>(numElementValuePairs);
        for (int i = 0; i < numElementValuePairs; ++i) {
            this.elementValuePairs.add(new ElementValuePair(input, constantPool));
        }
    }

    public String getAnnotationType() {
        return this.constantPool.getConstantUtf8(this.typeIndex).getBytes();
    }

    public List<ElementValuePair> getElementValuePairs() {
        return this.elementValuePairs;
    }
}

