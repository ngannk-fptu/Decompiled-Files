/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.AttributeUtils;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantValue;
import org.aspectj.apache.bcel.classfile.FieldOrMethod;
import org.aspectj.apache.bcel.classfile.Utility;
import org.aspectj.apache.bcel.generic.Type;

public final class Field
extends FieldOrMethod {
    public static final Field[] NoFields = new Field[0];
    private Type fieldType = null;

    private Field() {
    }

    public Field(Field c) {
        super(c);
    }

    Field(DataInputStream dis, ConstantPool cpool) throws IOException {
        super(dis, cpool);
    }

    public Field(int modifiers, int nameIndex, int signatureIndex, Attribute[] attributes, ConstantPool cpool) {
        super(modifiers, nameIndex, signatureIndex, attributes, cpool);
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitField(this);
    }

    public final ConstantValue getConstantValue() {
        return AttributeUtils.getConstantValueAttribute(this.attributes);
    }

    public final String toString() {
        StringBuffer buf = new StringBuffer(Utility.accessToString(this.modifiers));
        if (buf.length() > 0) {
            buf.append(" ");
        }
        String signature = Utility.signatureToString(this.getSignature());
        buf.append(signature).append(" ").append(this.getName());
        ConstantValue cv = this.getConstantValue();
        if (cv != null) {
            buf.append(" = ").append(cv);
        }
        for (Attribute a : this.attributes) {
            if (a instanceof ConstantValue) continue;
            buf.append(" [").append(a.toString()).append("]");
        }
        return buf.toString();
    }

    public Type getType() {
        if (this.fieldType == null) {
            this.fieldType = Type.getReturnType(this.getSignature());
        }
        return this.fieldType;
    }
}

