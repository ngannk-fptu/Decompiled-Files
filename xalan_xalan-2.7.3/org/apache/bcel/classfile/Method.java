/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.util.Objects;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.FieldOrMethod;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.ParameterAnnotationEntry;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.BCELComparator;

public final class Method
extends FieldOrMethod {
    public static final Method[] EMPTY_ARRAY = new Method[0];
    private static BCELComparator bcelComparator = new BCELComparator(){

        @Override
        public boolean equals(Object o1, Object o2) {
            Method THIS = (Method)o1;
            Method THAT = (Method)o2;
            return Objects.equals(THIS.getName(), THAT.getName()) && Objects.equals(THIS.getSignature(), THAT.getSignature());
        }

        @Override
        public int hashCode(Object o) {
            Method THIS = (Method)o;
            return THIS.getSignature().hashCode() ^ THIS.getName().hashCode();
        }
    };
    static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
    private ParameterAnnotationEntry[] parameterAnnotationEntries;

    public static BCELComparator getComparator() {
        return bcelComparator;
    }

    public static void setComparator(BCELComparator comparator) {
        bcelComparator = comparator;
    }

    public Method() {
    }

    Method(DataInput file, ConstantPool constantPool) throws IOException, ClassFormatException {
        super(file, constantPool);
    }

    public Method(int accessFlags, int nameIndex, int signatureIndex, Attribute[] attributes, ConstantPool constantPool) {
        super(accessFlags, nameIndex, signatureIndex, attributes, constantPool);
    }

    public Method(Method c) {
        super(c);
    }

    @Override
    public void accept(Visitor v) {
        v.visitMethod(this);
    }

    public Method copy(ConstantPool constantPool) {
        return (Method)this.copy_(constantPool);
    }

    public boolean equals(Object obj) {
        return bcelComparator.equals(this, obj);
    }

    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes(this.getSignature());
    }

    public Code getCode() {
        for (Attribute attribute : super.getAttributes()) {
            if (!(attribute instanceof Code)) continue;
            return (Code)attribute;
        }
        return null;
    }

    public ExceptionTable getExceptionTable() {
        for (Attribute attribute : super.getAttributes()) {
            if (!(attribute instanceof ExceptionTable)) continue;
            return (ExceptionTable)attribute;
        }
        return null;
    }

    public LineNumberTable getLineNumberTable() {
        Code code = this.getCode();
        if (code == null) {
            return null;
        }
        return code.getLineNumberTable();
    }

    public LocalVariableTable getLocalVariableTable() {
        Code code = this.getCode();
        if (code == null) {
            return null;
        }
        return code.getLocalVariableTable();
    }

    public ParameterAnnotationEntry[] getParameterAnnotationEntries() {
        if (this.parameterAnnotationEntries == null) {
            this.parameterAnnotationEntries = ParameterAnnotationEntry.createParameterAnnotationEntries(this.getAttributes());
        }
        return this.parameterAnnotationEntries;
    }

    public Type getReturnType() {
        return Type.getReturnType(this.getSignature());
    }

    public int hashCode() {
        return bcelComparator.hashCode(this);
    }

    public String toString() {
        String str;
        String access = Utility.accessToString(super.getAccessFlags());
        ConstantUtf8 c = super.getConstantPool().getConstantUtf8(super.getSignatureIndex());
        String signature = c.getBytes();
        c = super.getConstantPool().getConstantUtf8(super.getNameIndex());
        String name = c.getBytes();
        signature = Utility.methodSignatureToString(signature, name, access, true, this.getLocalVariableTable());
        StringBuilder buf = new StringBuilder(signature);
        for (Attribute attribute : super.getAttributes()) {
            if (attribute instanceof Code || attribute instanceof ExceptionTable) continue;
            buf.append(" [").append(attribute).append("]");
        }
        ExceptionTable e = this.getExceptionTable();
        if (e != null && !(str = e.toString()).isEmpty()) {
            buf.append("\n\t\tthrows ").append(str);
        }
        return buf.toString();
    }
}

