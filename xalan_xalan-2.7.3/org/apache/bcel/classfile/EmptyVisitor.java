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
import org.apache.bcel.classfile.Visitor;

public class EmptyVisitor
implements Visitor {
    protected EmptyVisitor() {
    }

    @Override
    public void visitAnnotation(Annotations obj) {
    }

    @Override
    public void visitAnnotationDefault(AnnotationDefault obj) {
    }

    @Override
    public void visitAnnotationEntry(AnnotationEntry obj) {
    }

    @Override
    public void visitBootstrapMethods(BootstrapMethods obj) {
    }

    @Override
    public void visitCode(Code obj) {
    }

    @Override
    public void visitCodeException(CodeException obj) {
    }

    @Override
    public void visitConstantClass(ConstantClass obj) {
    }

    @Override
    public void visitConstantDouble(ConstantDouble obj) {
    }

    @Override
    public void visitConstantDynamic(ConstantDynamic obj) {
    }

    @Override
    public void visitConstantFieldref(ConstantFieldref obj) {
    }

    @Override
    public void visitConstantFloat(ConstantFloat obj) {
    }

    @Override
    public void visitConstantInteger(ConstantInteger obj) {
    }

    @Override
    public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {
    }

    @Override
    public void visitConstantInvokeDynamic(ConstantInvokeDynamic obj) {
    }

    @Override
    public void visitConstantLong(ConstantLong obj) {
    }

    @Override
    public void visitConstantMethodHandle(ConstantMethodHandle constantMethodHandle) {
    }

    @Override
    public void visitConstantMethodref(ConstantMethodref obj) {
    }

    @Override
    public void visitConstantMethodType(ConstantMethodType obj) {
    }

    @Override
    public void visitConstantModule(ConstantModule constantModule) {
    }

    @Override
    public void visitConstantNameAndType(ConstantNameAndType obj) {
    }

    @Override
    public void visitConstantPackage(ConstantPackage constantPackage) {
    }

    @Override
    public void visitConstantPool(ConstantPool obj) {
    }

    @Override
    public void visitConstantString(ConstantString obj) {
    }

    @Override
    public void visitConstantUtf8(ConstantUtf8 obj) {
    }

    @Override
    public void visitConstantValue(ConstantValue obj) {
    }

    @Override
    public void visitDeprecated(Deprecated obj) {
    }

    @Override
    public void visitEnclosingMethod(EnclosingMethod obj) {
    }

    @Override
    public void visitExceptionTable(ExceptionTable obj) {
    }

    @Override
    public void visitField(Field obj) {
    }

    @Override
    public void visitInnerClass(InnerClass obj) {
    }

    @Override
    public void visitInnerClasses(InnerClasses obj) {
    }

    @Override
    public void visitJavaClass(JavaClass obj) {
    }

    @Override
    public void visitLineNumber(LineNumber obj) {
    }

    @Override
    public void visitLineNumberTable(LineNumberTable obj) {
    }

    @Override
    public void visitLocalVariable(LocalVariable obj) {
    }

    @Override
    public void visitLocalVariableTable(LocalVariableTable obj) {
    }

    @Override
    public void visitLocalVariableTypeTable(LocalVariableTypeTable obj) {
    }

    @Override
    public void visitMethod(Method obj) {
    }

    @Override
    public void visitMethodParameter(MethodParameter obj) {
    }

    @Override
    public void visitMethodParameters(MethodParameters obj) {
    }

    @Override
    public void visitModule(Module obj) {
    }

    @Override
    public void visitModuleExports(ModuleExports obj) {
    }

    @Override
    public void visitModuleMainClass(ModuleMainClass obj) {
    }

    @Override
    public void visitModuleOpens(ModuleOpens obj) {
    }

    @Override
    public void visitModulePackages(ModulePackages obj) {
    }

    @Override
    public void visitModuleProvides(ModuleProvides obj) {
    }

    @Override
    public void visitModuleRequires(ModuleRequires obj) {
    }

    @Override
    public void visitNestHost(NestHost obj) {
    }

    @Override
    public void visitNestMembers(NestMembers obj) {
    }

    @Override
    public void visitParameterAnnotation(ParameterAnnotations obj) {
    }

    @Override
    public void visitParameterAnnotationEntry(ParameterAnnotationEntry parameterAnnotationEntry) {
    }

    @Override
    public void visitSignature(Signature obj) {
    }

    @Override
    public void visitSourceFile(SourceFile obj) {
    }

    @Override
    public void visitStackMap(StackMap obj) {
    }

    @Override
    public void visitStackMapEntry(StackMapEntry obj) {
    }

    @Override
    public void visitSynthetic(Synthetic obj) {
    }

    @Override
    public void visitUnknown(Unknown obj) {
    }
}

