/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.util.Objects;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.FieldOrMethod;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.BCELComparator;

public final class Field
extends FieldOrMethod {
    public static final Field[] EMPTY_ARRAY = new Field[0];
    private static BCELComparator bcelComparator = new BCELComparator(){

        @Override
        public boolean equals(Object o1, Object o2) {
            Field THIS = (Field)o1;
            Field THAT = (Field)o2;
            return Objects.equals(THIS.getName(), THAT.getName()) && Objects.equals(THIS.getSignature(), THAT.getSignature());
        }

        @Override
        public int hashCode(Object o) {
            Field THIS = (Field)o;
            return THIS.getSignature().hashCode() ^ THIS.getName().hashCode();
        }
    };
    static final Field[] EMPTY_FIELD_ARRAY = new Field[0];

    public static BCELComparator getComparator() {
        return bcelComparator;
    }

    public static void setComparator(BCELComparator comparator) {
        bcelComparator = comparator;
    }

    Field(DataInput file, ConstantPool constantPool) throws IOException, ClassFormatException {
        super(file, constantPool);
    }

    public Field(Field c) {
        super(c);
    }

    public Field(int accessFlags, int nameIndex, int signatureIndex, Attribute[] attributes, ConstantPool constantPool) {
        super(accessFlags, nameIndex, signatureIndex, attributes, constantPool);
    }

    @Override
    public void accept(Visitor v) {
        v.visitField(this);
    }

    public Field copy(ConstantPool constantPool) {
        return (Field)this.copy_(constantPool);
    }

    public boolean equals(Object obj) {
        return bcelComparator.equals(this, obj);
    }

    public ConstantValue getConstantValue() {
        for (Attribute attribute : super.getAttributes()) {
            if (attribute.getTag() != 1) continue;
            return (ConstantValue)attribute;
        }
        return null;
    }

    public Type getType() {
        return Type.getReturnType(this.getSignature());
    }

    public int hashCode() {
        return bcelComparator.hashCode(this);
    }

    public String toString() {
        String access = Utility.accessToString(super.getAccessFlags());
        access = access.isEmpty() ? "" : access + " ";
        String signature = Utility.signatureToString(this.getSignature());
        String name = this.getName();
        StringBuilder buf = new StringBuilder(64);
        buf.append(access).append(signature).append(" ").append(name);
        ConstantValue cv = this.getConstantValue();
        if (cv != null) {
            buf.append(" = ").append(cv);
        }
        for (Attribute attribute : super.getAttributes()) {
            if (attribute instanceof ConstantValue) continue;
            buf.append(" [").append(attribute).append("]");
        }
        return buf.toString();
    }
}

