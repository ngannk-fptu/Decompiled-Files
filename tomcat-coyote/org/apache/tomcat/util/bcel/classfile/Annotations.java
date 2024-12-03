/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.classfile.AnnotationEntry;
import org.apache.tomcat.util.bcel.classfile.ConstantPool;

public class Annotations {
    static final Annotations[] EMPTY_ARRAY = new Annotations[0];
    private final AnnotationEntry[] annotationTable;

    Annotations(DataInput input, ConstantPool constantPool) throws IOException {
        int annotationTableLength = input.readUnsignedShort();
        this.annotationTable = new AnnotationEntry[annotationTableLength];
        for (int i = 0; i < annotationTableLength; ++i) {
            this.annotationTable[i] = new AnnotationEntry(input, constantPool);
        }
    }

    public AnnotationEntry[] getAnnotationEntries() {
        return this.annotationTable;
    }
}

