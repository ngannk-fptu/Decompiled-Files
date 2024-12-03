/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.util.Objects;
import java.util.Stack;
import java.util.stream.Stream;
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
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.ParameterAnnotationEntry;
import org.apache.bcel.classfile.ParameterAnnotations;
import org.apache.bcel.classfile.Signature;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.StackMap;
import org.apache.bcel.classfile.StackMapEntry;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.classfile.Unknown;
import org.apache.bcel.classfile.Visitor;

public class DescendingVisitor
implements Visitor {
    private final JavaClass clazz;
    private final Visitor visitor;
    private final Stack<Object> stack = new Stack();

    public DescendingVisitor(JavaClass clazz, Visitor visitor) {
        this.clazz = clazz;
        this.visitor = visitor;
    }

    private <E extends Node> void accept(E[] node) {
        Stream.of(node).forEach(e -> e.accept(this));
    }

    public Object current() {
        return this.stack.peek();
    }

    public Object predecessor() {
        return this.predecessor(0);
    }

    public Object predecessor(int level) {
        int size = this.stack.size();
        if (size < 2 || level < 0) {
            return null;
        }
        return this.stack.elementAt(size - (level + 2));
    }

    public void visit() {
        this.clazz.accept(this);
    }

    @Override
    public void visitAnnotation(Annotations annotation) {
        this.stack.push(annotation);
        annotation.accept(this.visitor);
        this.accept(annotation.getAnnotationEntries());
        this.stack.pop();
    }

    @Override
    public void visitAnnotationDefault(AnnotationDefault obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitAnnotationEntry(AnnotationEntry annotationEntry) {
        this.stack.push(annotationEntry);
        annotationEntry.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitBootstrapMethods(BootstrapMethods bm) {
        this.stack.push(bm);
        bm.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitCode(Code code) {
        this.stack.push(code);
        code.accept(this.visitor);
        this.accept(code.getExceptionTable());
        this.accept(code.getAttributes());
        this.stack.pop();
    }

    @Override
    public void visitCodeException(CodeException ce) {
        this.stack.push(ce);
        ce.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantClass(ConstantClass constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantDouble(ConstantDouble constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantDynamic(ConstantDynamic obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantFieldref(ConstantFieldref constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantFloat(ConstantFloat constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantInteger(ConstantInteger constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantInvokeDynamic(ConstantInvokeDynamic constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantLong(ConstantLong constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantMethodHandle(ConstantMethodHandle obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantMethodref(ConstantMethodref constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantMethodType(ConstantMethodType obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantModule(ConstantModule obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantNameAndType(ConstantNameAndType constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantPackage(ConstantPackage obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantPool(ConstantPool cp) {
        this.stack.push(cp);
        cp.accept(this.visitor);
        Stream.of(cp.getConstantPool()).filter(Objects::nonNull).forEach(e -> e.accept(this));
        this.stack.pop();
    }

    @Override
    public void visitConstantString(ConstantString constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantUtf8(ConstantUtf8 constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitConstantValue(ConstantValue cv) {
        this.stack.push(cv);
        cv.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitDeprecated(Deprecated attribute) {
        this.stack.push(attribute);
        attribute.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitEnclosingMethod(EnclosingMethod obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitExceptionTable(ExceptionTable table) {
        this.stack.push(table);
        table.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitField(Field field) {
        this.stack.push(field);
        field.accept(this.visitor);
        this.accept(field.getAttributes());
        this.stack.pop();
    }

    @Override
    public void visitInnerClass(InnerClass inner) {
        this.stack.push(inner);
        inner.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitInnerClasses(InnerClasses ic) {
        this.stack.push(ic);
        ic.accept(this.visitor);
        this.accept(ic.getInnerClasses());
        this.stack.pop();
    }

    @Override
    public void visitJavaClass(JavaClass clazz) {
        this.stack.push(clazz);
        clazz.accept(this.visitor);
        this.accept(clazz.getFields());
        this.accept(clazz.getMethods());
        this.accept(clazz.getAttributes());
        clazz.getConstantPool().accept(this);
        this.stack.pop();
    }

    @Override
    public void visitLineNumber(LineNumber number) {
        this.stack.push(number);
        number.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitLineNumberTable(LineNumberTable table) {
        this.stack.push(table);
        table.accept(this.visitor);
        this.accept(table.getLineNumberTable());
        this.stack.pop();
    }

    @Override
    public void visitLocalVariable(LocalVariable var) {
        this.stack.push(var);
        var.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitLocalVariableTable(LocalVariableTable table) {
        this.stack.push(table);
        table.accept(this.visitor);
        this.accept(table.getLocalVariableTable());
        this.stack.pop();
    }

    @Override
    public void visitLocalVariableTypeTable(LocalVariableTypeTable obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitMethod(Method method) {
        this.stack.push(method);
        method.accept(this.visitor);
        this.accept(method.getAttributes());
        this.stack.pop();
    }

    @Override
    public void visitMethodParameter(MethodParameter obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitMethodParameters(MethodParameters obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        Stream.of(obj.getParameters()).forEach(e -> e.accept(this));
        this.stack.pop();
    }

    @Override
    public void visitModule(Module obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.accept(obj.getRequiresTable());
        this.accept(obj.getExportsTable());
        this.accept(obj.getOpensTable());
        this.accept(obj.getProvidesTable());
        this.stack.pop();
    }

    @Override
    public void visitModuleExports(ModuleExports obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitModuleMainClass(ModuleMainClass obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitModuleOpens(ModuleOpens obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitModulePackages(ModulePackages obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitModuleProvides(ModuleProvides obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitModuleRequires(ModuleRequires obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitNestHost(NestHost obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitNestMembers(NestMembers obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitParameterAnnotation(ParameterAnnotations obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitParameterAnnotationEntry(ParameterAnnotationEntry obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitSignature(Signature attribute) {
        this.stack.push(attribute);
        attribute.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitSourceFile(SourceFile attribute) {
        this.stack.push(attribute);
        attribute.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitStackMap(StackMap table) {
        this.stack.push(table);
        table.accept(this.visitor);
        this.accept(table.getStackMap());
        this.stack.pop();
    }

    @Override
    public void visitStackMapEntry(StackMapEntry var) {
        this.stack.push(var);
        var.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitSynthetic(Synthetic attribute) {
        this.stack.push(attribute);
        attribute.accept(this.visitor);
        this.stack.pop();
    }

    @Override
    public void visitUnknown(Unknown attribute) {
        this.stack.push(attribute);
        attribute.accept(this.visitor);
        this.stack.pop();
    }
}

