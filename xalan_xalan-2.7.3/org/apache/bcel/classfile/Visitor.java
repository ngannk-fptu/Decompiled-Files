/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

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
import org.apache.bcel.classfile.MethodParameter;
import org.apache.bcel.classfile.MethodParameters;
import org.apache.bcel.classfile.Module;
import org.apache.bcel.classfile.ModuleExports;
import org.apache.bcel.classfile.ModuleMainClass;
import org.apache.bcel.classfile.ModuleOpens;
import org.apache.bcel.classfile.ModulePackages;
import org.apache.bcel.classfile.ModuleProvides;
import org.apache.bcel.classfile.ModuleRequires;
import org.apache.bcel.classfile.NestHost;
import org.apache.bcel.classfile.NestMembers;
import org.apache.bcel.classfile.ParameterAnnotationEntry;
import org.apache.bcel.classfile.ParameterAnnotations;
import org.apache.bcel.classfile.Signature;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.StackMap;
import org.apache.bcel.classfile.StackMapEntry;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.classfile.Unknown;

public interface Visitor {
    public void visitAnnotation(Annotations var1);

    public void visitAnnotationDefault(AnnotationDefault var1);

    public void visitAnnotationEntry(AnnotationEntry var1);

    public void visitBootstrapMethods(BootstrapMethods var1);

    public void visitCode(Code var1);

    public void visitCodeException(CodeException var1);

    public void visitConstantClass(ConstantClass var1);

    public void visitConstantDouble(ConstantDouble var1);

    default public void visitConstantDynamic(ConstantDynamic constantDynamic) {
    }

    public void visitConstantFieldref(ConstantFieldref var1);

    public void visitConstantFloat(ConstantFloat var1);

    public void visitConstantInteger(ConstantInteger var1);

    public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref var1);

    public void visitConstantInvokeDynamic(ConstantInvokeDynamic var1);

    public void visitConstantLong(ConstantLong var1);

    public void visitConstantMethodHandle(ConstantMethodHandle var1);

    public void visitConstantMethodref(ConstantMethodref var1);

    public void visitConstantMethodType(ConstantMethodType var1);

    public void visitConstantModule(ConstantModule var1);

    public void visitConstantNameAndType(ConstantNameAndType var1);

    public void visitConstantPackage(ConstantPackage var1);

    public void visitConstantPool(ConstantPool var1);

    public void visitConstantString(ConstantString var1);

    public void visitConstantUtf8(ConstantUtf8 var1);

    public void visitConstantValue(ConstantValue var1);

    public void visitDeprecated(Deprecated var1);

    public void visitEnclosingMethod(EnclosingMethod var1);

    public void visitExceptionTable(ExceptionTable var1);

    public void visitField(Field var1);

    public void visitInnerClass(InnerClass var1);

    public void visitInnerClasses(InnerClasses var1);

    public void visitJavaClass(JavaClass var1);

    public void visitLineNumber(LineNumber var1);

    public void visitLineNumberTable(LineNumberTable var1);

    public void visitLocalVariable(LocalVariable var1);

    public void visitLocalVariableTable(LocalVariableTable var1);

    public void visitLocalVariableTypeTable(LocalVariableTypeTable var1);

    public void visitMethod(Method var1);

    default public void visitMethodParameter(MethodParameter obj) {
    }

    public void visitMethodParameters(MethodParameters var1);

    default public void visitModule(Module constantModule) {
    }

    default public void visitModuleExports(ModuleExports constantModule) {
    }

    default public void visitModuleMainClass(ModuleMainClass obj) {
    }

    default public void visitModuleOpens(ModuleOpens constantModule) {
    }

    default public void visitModulePackages(ModulePackages constantModule) {
    }

    default public void visitModuleProvides(ModuleProvides constantModule) {
    }

    default public void visitModuleRequires(ModuleRequires constantModule) {
    }

    default public void visitNestHost(NestHost obj) {
    }

    default public void visitNestMembers(NestMembers obj) {
    }

    public void visitParameterAnnotation(ParameterAnnotations var1);

    public void visitParameterAnnotationEntry(ParameterAnnotationEntry var1);

    public void visitSignature(Signature var1);

    public void visitSourceFile(SourceFile var1);

    public void visitStackMap(StackMap var1);

    public void visitStackMapEntry(StackMapEntry var1);

    public void visitSynthetic(Synthetic var1);

    public void visitUnknown(Unknown var1);
}

