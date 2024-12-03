/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import org.aspectj.apache.bcel.classfile.AnnotationDefault;
import org.aspectj.apache.bcel.classfile.BootstrapMethods;
import org.aspectj.apache.bcel.classfile.Code;
import org.aspectj.apache.bcel.classfile.CodeException;
import org.aspectj.apache.bcel.classfile.ConstantClass;
import org.aspectj.apache.bcel.classfile.ConstantDouble;
import org.aspectj.apache.bcel.classfile.ConstantDynamic;
import org.aspectj.apache.bcel.classfile.ConstantFieldref;
import org.aspectj.apache.bcel.classfile.ConstantFloat;
import org.aspectj.apache.bcel.classfile.ConstantInteger;
import org.aspectj.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.aspectj.apache.bcel.classfile.ConstantInvokeDynamic;
import org.aspectj.apache.bcel.classfile.ConstantLong;
import org.aspectj.apache.bcel.classfile.ConstantMethodHandle;
import org.aspectj.apache.bcel.classfile.ConstantMethodType;
import org.aspectj.apache.bcel.classfile.ConstantMethodref;
import org.aspectj.apache.bcel.classfile.ConstantModule;
import org.aspectj.apache.bcel.classfile.ConstantNameAndType;
import org.aspectj.apache.bcel.classfile.ConstantPackage;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantString;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.classfile.ConstantValue;
import org.aspectj.apache.bcel.classfile.Deprecated;
import org.aspectj.apache.bcel.classfile.EnclosingMethod;
import org.aspectj.apache.bcel.classfile.ExceptionTable;
import org.aspectj.apache.bcel.classfile.Field;
import org.aspectj.apache.bcel.classfile.InnerClass;
import org.aspectj.apache.bcel.classfile.InnerClasses;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.classfile.LineNumber;
import org.aspectj.apache.bcel.classfile.LineNumberTable;
import org.aspectj.apache.bcel.classfile.LocalVariable;
import org.aspectj.apache.bcel.classfile.LocalVariableTable;
import org.aspectj.apache.bcel.classfile.LocalVariableTypeTable;
import org.aspectj.apache.bcel.classfile.Method;
import org.aspectj.apache.bcel.classfile.MethodParameters;
import org.aspectj.apache.bcel.classfile.Module;
import org.aspectj.apache.bcel.classfile.ModuleMainClass;
import org.aspectj.apache.bcel.classfile.ModulePackages;
import org.aspectj.apache.bcel.classfile.NestHost;
import org.aspectj.apache.bcel.classfile.NestMembers;
import org.aspectj.apache.bcel.classfile.Signature;
import org.aspectj.apache.bcel.classfile.SourceFile;
import org.aspectj.apache.bcel.classfile.StackMap;
import org.aspectj.apache.bcel.classfile.StackMapEntry;
import org.aspectj.apache.bcel.classfile.Synthetic;
import org.aspectj.apache.bcel.classfile.Unknown;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeInvisAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeInvisParamAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeInvisTypeAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeVisAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeVisParamAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeVisTypeAnnos;

public interface ClassVisitor {
    public void visitCode(Code var1);

    public void visitCodeException(CodeException var1);

    public void visitConstantClass(ConstantClass var1);

    public void visitConstantDouble(ConstantDouble var1);

    public void visitConstantFieldref(ConstantFieldref var1);

    public void visitConstantFloat(ConstantFloat var1);

    public void visitConstantInteger(ConstantInteger var1);

    public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref var1);

    public void visitConstantLong(ConstantLong var1);

    public void visitConstantMethodref(ConstantMethodref var1);

    public void visitConstantMethodHandle(ConstantMethodHandle var1);

    public void visitConstantNameAndType(ConstantNameAndType var1);

    public void visitConstantMethodType(ConstantMethodType var1);

    public void visitConstantInvokeDynamic(ConstantInvokeDynamic var1);

    public void visitConstantDynamic(ConstantDynamic var1);

    public void visitConstantPool(ConstantPool var1);

    public void visitConstantString(ConstantString var1);

    public void visitConstantModule(ConstantModule var1);

    public void visitConstantPackage(ConstantPackage var1);

    public void visitConstantUtf8(ConstantUtf8 var1);

    public void visitConstantValue(ConstantValue var1);

    public void visitDeprecated(Deprecated var1);

    public void visitExceptionTable(ExceptionTable var1);

    public void visitField(Field var1);

    public void visitInnerClass(InnerClass var1);

    public void visitInnerClasses(InnerClasses var1);

    public void visitJavaClass(JavaClass var1);

    public void visitLineNumber(LineNumber var1);

    public void visitLineNumberTable(LineNumberTable var1);

    public void visitLocalVariable(LocalVariable var1);

    public void visitLocalVariableTable(LocalVariableTable var1);

    public void visitMethod(Method var1);

    public void visitSignature(Signature var1);

    public void visitSourceFile(SourceFile var1);

    public void visitSynthetic(Synthetic var1);

    public void visitBootstrapMethods(BootstrapMethods var1);

    public void visitUnknown(Unknown var1);

    public void visitStackMap(StackMap var1);

    public void visitStackMapEntry(StackMapEntry var1);

    public void visitEnclosingMethod(EnclosingMethod var1);

    public void visitRuntimeVisibleAnnotations(RuntimeVisAnnos var1);

    public void visitRuntimeInvisibleAnnotations(RuntimeInvisAnnos var1);

    public void visitRuntimeVisibleParameterAnnotations(RuntimeVisParamAnnos var1);

    public void visitRuntimeInvisibleParameterAnnotations(RuntimeInvisParamAnnos var1);

    public void visitRuntimeVisibleTypeAnnotations(RuntimeVisTypeAnnos var1);

    public void visitRuntimeInvisibleTypeAnnotations(RuntimeInvisTypeAnnos var1);

    public void visitAnnotationDefault(AnnotationDefault var1);

    public void visitLocalVariableTypeTable(LocalVariableTypeTable var1);

    public void visitMethodParameters(MethodParameters var1);

    public void visitModule(Module var1);

    public void visitModulePackages(ModulePackages var1);

    public void visitModuleMainClass(ModuleMainClass var1);

    public void visitNestHost(NestHost var1);

    public void visitNestMembers(NestMembers var1);
}

