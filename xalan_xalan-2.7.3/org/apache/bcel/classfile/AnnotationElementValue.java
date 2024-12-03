/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ElementValue;

public class AnnotationElementValue
extends ElementValue {
    private final AnnotationEntry annotationEntry;

    public AnnotationElementValue(int type, AnnotationEntry annotationEntry, ConstantPool cpool) {
        super(type, cpool);
        if (type != 64) {
            throw new ClassFormatException("Only element values of type annotation can be built with this ctor - type specified: " + type);
        }
        this.annotationEntry = annotationEntry;
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        dos.writeByte(super.getType());
        this.annotationEntry.dump(dos);
    }

    public AnnotationEntry getAnnotationEntry() {
        return this.annotationEntry;
    }

    @Override
    public String stringifyValue() {
        return this.annotationEntry.toString();
    }

    @Override
    public String toString() {
        return this.stringifyValue();
    }
}

