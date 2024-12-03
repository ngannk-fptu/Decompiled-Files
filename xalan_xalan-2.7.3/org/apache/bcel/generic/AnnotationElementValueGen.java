/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.AnnotationElementValue;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.generic.AnnotationEntryGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ElementValueGen;

public class AnnotationElementValueGen
extends ElementValueGen {
    private final AnnotationEntryGen a;

    public AnnotationElementValueGen(AnnotationElementValue value, ConstantPoolGen cpool, boolean copyPoolEntries) {
        super(64, cpool);
        this.a = new AnnotationEntryGen(value.getAnnotationEntry(), cpool, copyPoolEntries);
    }

    public AnnotationElementValueGen(AnnotationEntryGen a, ConstantPoolGen cpool) {
        super(64, cpool);
        this.a = a;
    }

    public AnnotationElementValueGen(int type, AnnotationEntryGen annotation, ConstantPoolGen cpool) {
        super(type, cpool);
        if (type != 64) {
            throw new IllegalArgumentException("Only element values of type annotation can be built with this ctor - type specified: " + type);
        }
        this.a = annotation;
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        dos.writeByte(super.getElementValueType());
        this.a.dump(dos);
    }

    public AnnotationEntryGen getAnnotation() {
        return this.a;
    }

    @Override
    public ElementValue getElementValue() {
        return new AnnotationElementValue(super.getElementValueType(), this.a.getAnnotation(), this.getConstantPool().getConstantPool());
    }

    @Override
    public String stringifyValue() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

