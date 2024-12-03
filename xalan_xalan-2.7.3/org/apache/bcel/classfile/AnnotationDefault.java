/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.Visitor;

public class AnnotationDefault
extends Attribute {
    private ElementValue defaultValue;

    AnnotationDefault(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, (ElementValue)null, constantPool);
        this.defaultValue = ElementValue.readElementValue(input, constantPool);
    }

    public AnnotationDefault(int nameIndex, int length, ElementValue defaultValue, ConstantPool constantPool) {
        super((byte)16, nameIndex, length, constantPool);
        this.defaultValue = defaultValue;
    }

    @Override
    public void accept(Visitor v) {
        v.visitAnnotationDefault(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        return (Attribute)this.clone();
    }

    @Override
    public final void dump(DataOutputStream dos) throws IOException {
        super.dump(dos);
        this.defaultValue.dump(dos);
    }

    public final ElementValue getDefaultValue() {
        return this.defaultValue;
    }

    public final void setDefaultValue(ElementValue defaultValue) {
        this.defaultValue = defaultValue;
    }
}

