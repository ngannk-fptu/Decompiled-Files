/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.statics;

import org.apache.bcel.classfile.AnnotationDefault;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Annotations;
import org.apache.bcel.classfile.BootstrapMethods;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantDynamic;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantInvokeDynamic;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantMethodHandle;
import org.apache.bcel.classfile.ConstantMethodType;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantModule;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPackage;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.Deprecated;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.EnclosingMethod;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.LocalVariableTypeTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.MethodParameters;
import org.apache.bcel.classfile.NestMembers;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.ParameterAnnotationEntry;
import org.apache.bcel.classfile.ParameterAnnotations;
import org.apache.bcel.classfile.Signature;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.StackMap;
import org.apache.bcel.classfile.StackMapEntry;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.classfile.Unknown;
import org.apache.bcel.verifier.exc.AssertionViolatedException;

public class StringRepresentation
extends EmptyVisitor {
    private String tostring;
    private final Node n;

    public StringRepresentation(Node n) {
        this.n = n;
        n.accept(this);
    }

    public String toString() {
        if (this.tostring == null) {
            throw new AssertionViolatedException("Please adapt '" + this.getClass() + "' to deal with objects of class '" + this.n.getClass() + "'.");
        }
        return this.tostring;
    }

    private String toString(Node obj) {
        String ret;
        try {
            ret = obj.toString();
        }
        catch (RuntimeException e) {
            String s = obj.getClass().getName();
            s = s.substring(s.lastIndexOf(".") + 1);
            ret = "<<" + s + ">>";
        }
        return ret;
    }

    @Override
    public void visitAnnotation(Annotations obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitAnnotationDefault(AnnotationDefault obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitAnnotationEntry(AnnotationEntry obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitBootstrapMethods(BootstrapMethods obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitCode(Code obj) {
        this.tostring = "<CODE>";
    }

    @Override
    public void visitCodeException(CodeException obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantClass(ConstantClass obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantDouble(ConstantDouble obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantDynamic(ConstantDynamic obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantFieldref(ConstantFieldref obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantFloat(ConstantFloat obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantInteger(ConstantInteger obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantInvokeDynamic(ConstantInvokeDynamic obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantLong(ConstantLong obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantMethodHandle(ConstantMethodHandle obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantMethodref(ConstantMethodref obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantMethodType(ConstantMethodType obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantModule(ConstantModule obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantNameAndType(ConstantNameAndType obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantPackage(ConstantPackage obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantPool(ConstantPool obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantString(ConstantString obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantUtf8(ConstantUtf8 obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitConstantValue(ConstantValue obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitDeprecated(Deprecated obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitEnclosingMethod(EnclosingMethod obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitExceptionTable(ExceptionTable obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitField(Field obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitInnerClass(InnerClass obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitInnerClasses(InnerClasses obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitJavaClass(JavaClass obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitLineNumber(LineNumber obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitLineNumberTable(LineNumberTable obj) {
        this.tostring = "<LineNumberTable: " + this.toString(obj) + ">";
    }

    @Override
    public void visitLocalVariable(LocalVariable obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitLocalVariableTable(LocalVariableTable obj) {
        this.tostring = "<LocalVariableTable: " + this.toString(obj) + ">";
    }

    @Override
    public void visitLocalVariableTypeTable(LocalVariableTypeTable obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitMethod(Method obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitMethodParameters(MethodParameters obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitNestMembers(NestMembers obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitParameterAnnotation(ParameterAnnotations obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitParameterAnnotationEntry(ParameterAnnotationEntry obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitSignature(Signature obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitSourceFile(SourceFile obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitStackMap(StackMap obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitStackMapEntry(StackMapEntry obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitSynthetic(Synthetic obj) {
        this.tostring = this.toString(obj);
    }

    @Override
    public void visitUnknown(Unknown obj) {
        this.tostring = this.toString(obj);
    }
}

