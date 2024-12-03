/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.ParameterAnnotations;
import org.apache.bcel.classfile.Visitor;

public class ParameterAnnotationEntry
implements Node {
    static final ParameterAnnotationEntry[] EMPTY_ARRAY = new ParameterAnnotationEntry[0];
    private final AnnotationEntry[] annotationTable;

    public static ParameterAnnotationEntry[] createParameterAnnotationEntries(Attribute[] attrs) {
        ArrayList accumulatedAnnotations = new ArrayList(attrs.length);
        for (Attribute attribute : attrs) {
            if (!(attribute instanceof ParameterAnnotations)) continue;
            ParameterAnnotations runtimeAnnotations = (ParameterAnnotations)attribute;
            Collections.addAll(accumulatedAnnotations, runtimeAnnotations.getParameterAnnotationEntries());
        }
        return accumulatedAnnotations.toArray(EMPTY_ARRAY);
    }

    ParameterAnnotationEntry(DataInput input, ConstantPool constantPool) throws IOException {
        int annotationTableLength = input.readUnsignedShort();
        this.annotationTable = new AnnotationEntry[annotationTableLength];
        for (int i = 0; i < annotationTableLength; ++i) {
            this.annotationTable[i] = AnnotationEntry.read(input, constantPool, false);
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitParameterAnnotationEntry(this);
    }

    public void dump(DataOutputStream dos) throws IOException {
        dos.writeShort(this.annotationTable.length);
        for (AnnotationEntry entry : this.annotationTable) {
            entry.dump(dos);
        }
    }

    public AnnotationEntry[] getAnnotationEntries() {
        return this.annotationTable;
    }
}

