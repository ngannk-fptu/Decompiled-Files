/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.AttributeUtils;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.Code;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.classfile.ExceptionTable;
import org.aspectj.apache.bcel.classfile.FieldOrMethod;
import org.aspectj.apache.bcel.classfile.LineNumberTable;
import org.aspectj.apache.bcel.classfile.LocalVariableTable;
import org.aspectj.apache.bcel.classfile.Utility;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeInvisParamAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeVisParamAnnos;
import org.aspectj.apache.bcel.generic.Type;

public final class Method
extends FieldOrMethod {
    public static final AnnotationGen[][] NO_PARAMETER_ANNOTATIONS = new AnnotationGen[0][];
    public static final Method[] NoMethods = new Method[0];
    private boolean parameterAnnotationsOutOfDate = true;
    private AnnotationGen[][] unpackedParameterAnnotations;

    private Method() {
        this.parameterAnnotationsOutOfDate = true;
    }

    public Method(Method c) {
        super(c);
        this.parameterAnnotationsOutOfDate = true;
    }

    Method(DataInputStream file, ConstantPool constant_pool) throws IOException {
        super(file, constant_pool);
    }

    public Method(int access_flags, int name_index, int signature_index, Attribute[] attributes, ConstantPool constant_pool) {
        super(access_flags, name_index, signature_index, attributes, constant_pool);
        this.parameterAnnotationsOutOfDate = true;
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitMethod(this);
    }

    @Override
    public void setAttributes(Attribute[] attributes) {
        this.parameterAnnotationsOutOfDate = true;
        super.setAttributes(attributes);
    }

    public final Code getCode() {
        return AttributeUtils.getCodeAttribute(this.attributes);
    }

    public final ExceptionTable getExceptionTable() {
        return AttributeUtils.getExceptionTableAttribute(this.attributes);
    }

    public final LocalVariableTable getLocalVariableTable() {
        Code code = this.getCode();
        if (code != null) {
            return code.getLocalVariableTable();
        }
        return null;
    }

    public final LineNumberTable getLineNumberTable() {
        Code code = this.getCode();
        if (code != null) {
            return code.getLineNumberTable();
        }
        return null;
    }

    public final String toString() {
        String str;
        String access = Utility.accessToString(this.modifiers);
        ConstantUtf8 c = (ConstantUtf8)this.cpool.getConstant(this.signatureIndex, (byte)1);
        String signature = c.getValue();
        c = (ConstantUtf8)this.cpool.getConstant(this.nameIndex, (byte)1);
        String name = c.getValue();
        signature = Utility.methodSignatureToString(signature, name, access, true, this.getLocalVariableTable());
        StringBuffer buf = new StringBuffer(signature);
        for (int i = 0; i < this.attributes.length; ++i) {
            Attribute a = this.attributes[i];
            if (a instanceof Code || a instanceof ExceptionTable) continue;
            buf.append(" [" + a.toString() + "]");
        }
        ExceptionTable e = this.getExceptionTable();
        if (e != null && !(str = e.toString()).equals("")) {
            buf.append("\n\t\tthrows " + str);
        }
        return buf.toString();
    }

    public Type getReturnType() {
        return Type.getReturnType(this.getSignature());
    }

    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes(this.getSignature());
    }

    private void ensureParameterAnnotationsUnpacked() {
        if (!this.parameterAnnotationsOutOfDate) {
            return;
        }
        this.parameterAnnotationsOutOfDate = false;
        int parameterCount = this.getArgumentTypes().length;
        if (parameterCount == 0) {
            this.unpackedParameterAnnotations = NO_PARAMETER_ANNOTATIONS;
            return;
        }
        RuntimeVisParamAnnos parameterAnnotationsVis = null;
        RuntimeInvisParamAnnos parameterAnnotationsInvis = null;
        Attribute[] attrs = this.getAttributes();
        for (int i = 0; i < attrs.length; ++i) {
            Attribute attribute = attrs[i];
            if (attribute instanceof RuntimeVisParamAnnos) {
                parameterAnnotationsVis = (RuntimeVisParamAnnos)attribute;
                continue;
            }
            if (!(attribute instanceof RuntimeInvisParamAnnos)) continue;
            parameterAnnotationsInvis = (RuntimeInvisParamAnnos)attribute;
        }
        boolean foundSome = false;
        if (parameterAnnotationsInvis != null || parameterAnnotationsVis != null) {
            ArrayList<AnnotationGen[]> annotationsForEachParameter = new ArrayList<AnnotationGen[]>();
            AnnotationGen[] visibleOnes = null;
            AnnotationGen[] invisibleOnes = null;
            for (int i = 0; i < parameterCount; ++i) {
                int count = 0;
                visibleOnes = new AnnotationGen[]{};
                invisibleOnes = new AnnotationGen[]{};
                if (parameterAnnotationsVis != null) {
                    visibleOnes = parameterAnnotationsVis.getAnnotationsOnParameter(i);
                    count += visibleOnes.length;
                }
                if (parameterAnnotationsInvis != null) {
                    invisibleOnes = parameterAnnotationsInvis.getAnnotationsOnParameter(i);
                    count += invisibleOnes.length;
                }
                AnnotationGen[] complete = AnnotationGen.NO_ANNOTATIONS;
                if (count != 0) {
                    complete = new AnnotationGen[visibleOnes.length + invisibleOnes.length];
                    System.arraycopy(visibleOnes, 0, complete, 0, visibleOnes.length);
                    System.arraycopy(invisibleOnes, 0, complete, visibleOnes.length, invisibleOnes.length);
                    foundSome = true;
                }
                annotationsForEachParameter.add(complete);
            }
            if (foundSome) {
                this.unpackedParameterAnnotations = (AnnotationGen[][])annotationsForEachParameter.toArray((T[])new AnnotationGen[0][]);
                return;
            }
        }
        this.unpackedParameterAnnotations = NO_PARAMETER_ANNOTATIONS;
    }

    public AnnotationGen[] getAnnotationsOnParameter(int i) {
        this.ensureParameterAnnotationsUnpacked();
        if (this.unpackedParameterAnnotations == NO_PARAMETER_ANNOTATIONS) {
            return AnnotationGen.NO_ANNOTATIONS;
        }
        return this.unpackedParameterAnnotations[i];
    }

    public AnnotationGen[][] getParameterAnnotations() {
        this.ensureParameterAnnotationsUnpacked();
        return this.unpackedParameterAnnotations;
    }
}

