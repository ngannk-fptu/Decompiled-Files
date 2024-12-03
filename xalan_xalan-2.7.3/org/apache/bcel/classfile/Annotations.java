/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Visitor;

public abstract class Annotations
extends Attribute
implements Iterable<AnnotationEntry> {
    private AnnotationEntry[] annotationTable;
    private final boolean isRuntimeVisible;

    public Annotations(byte annotationType, int nameIndex, int length, AnnotationEntry[] annotationTable, ConstantPool constantPool, boolean isRuntimeVisible) {
        super(annotationType, nameIndex, length, constantPool);
        this.annotationTable = annotationTable;
        this.isRuntimeVisible = isRuntimeVisible;
    }

    Annotations(byte annotationType, int nameIndex, int length, DataInput input, ConstantPool constantPool, boolean isRuntimeVisible) throws IOException {
        this(annotationType, nameIndex, length, (AnnotationEntry[])null, constantPool, isRuntimeVisible);
        int annotationTableLength = input.readUnsignedShort();
        this.annotationTable = new AnnotationEntry[annotationTableLength];
        for (int i = 0; i < annotationTableLength; ++i) {
            this.annotationTable[i] = AnnotationEntry.read(input, constantPool, isRuntimeVisible);
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitAnnotation(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        return null;
    }

    public AnnotationEntry[] getAnnotationEntries() {
        return this.annotationTable;
    }

    public final int getNumAnnotations() {
        if (this.annotationTable == null) {
            return 0;
        }
        return this.annotationTable.length;
    }

    public boolean isRuntimeVisible() {
        return this.isRuntimeVisible;
    }

    @Override
    public Iterator<AnnotationEntry> iterator() {
        return Stream.of(this.annotationTable).iterator();
    }

    public final void setAnnotationTable(AnnotationEntry[] annotationTable) {
        this.annotationTable = annotationTable;
    }

    @Override
    public final String toString() {
        StringBuilder buf = new StringBuilder(Const.getAttributeName(this.getTag()));
        buf.append(":\n");
        for (int i = 0; i < this.annotationTable.length; ++i) {
            buf.append("  ").append(this.annotationTable[i]);
            if (i >= this.annotationTable.length - 1) continue;
            buf.append('\n');
        }
        return buf.toString();
    }

    protected void writeAnnotations(DataOutputStream dos) throws IOException {
        if (this.annotationTable == null) {
            return;
        }
        dos.writeShort(this.annotationTable.length);
        for (AnnotationEntry element : this.annotationTable) {
            element.dump(dos);
        }
    }
}

