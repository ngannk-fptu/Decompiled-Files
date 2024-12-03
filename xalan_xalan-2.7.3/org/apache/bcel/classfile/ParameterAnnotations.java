/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ParameterAnnotationEntry;
import org.apache.bcel.classfile.Visitor;

public abstract class ParameterAnnotations
extends Attribute
implements Iterable<ParameterAnnotationEntry> {
    private ParameterAnnotationEntry[] parameterAnnotationTable;

    ParameterAnnotations(byte parameterAnnotationType, int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(parameterAnnotationType, nameIndex, length, (ParameterAnnotationEntry[])null, constantPool);
        int numParameters = input.readUnsignedByte();
        this.parameterAnnotationTable = new ParameterAnnotationEntry[numParameters];
        for (int i = 0; i < numParameters; ++i) {
            this.parameterAnnotationTable[i] = new ParameterAnnotationEntry(input, constantPool);
        }
    }

    public ParameterAnnotations(byte parameterAnnotationType, int nameIndex, int length, ParameterAnnotationEntry[] parameterAnnotationTable, ConstantPool constantPool) {
        super(parameterAnnotationType, nameIndex, length, constantPool);
        this.parameterAnnotationTable = parameterAnnotationTable;
    }

    @Override
    public void accept(Visitor v) {
        v.visitParameterAnnotation(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        return (Attribute)this.clone();
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        super.dump(dos);
        dos.writeByte(this.parameterAnnotationTable.length);
        for (ParameterAnnotationEntry element : this.parameterAnnotationTable) {
            element.dump(dos);
        }
    }

    public ParameterAnnotationEntry[] getParameterAnnotationEntries() {
        return this.parameterAnnotationTable;
    }

    public final ParameterAnnotationEntry[] getParameterAnnotationTable() {
        return this.parameterAnnotationTable;
    }

    @Override
    public Iterator<ParameterAnnotationEntry> iterator() {
        return Stream.of(this.parameterAnnotationTable).iterator();
    }

    public final void setParameterAnnotationTable(ParameterAnnotationEntry[] parameterAnnotationTable) {
        this.parameterAnnotationTable = parameterAnnotationTable;
    }
}

