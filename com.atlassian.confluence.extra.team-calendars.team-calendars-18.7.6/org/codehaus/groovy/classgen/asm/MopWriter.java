/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.MethodVisitor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.WriterController;

public class MopWriter {
    public static final Factory FACTORY = new Factory(){

        @Override
        public MopWriter create(WriterController controller) {
            return new MopWriter(controller);
        }
    };
    private WriterController controller;

    public MopWriter(WriterController wc) {
        this.controller = wc;
    }

    public void createMopMethods() {
        ClassNode classNode = this.controller.getClassNode();
        if (classNode.declaresInterface(ClassHelper.GENERATED_CLOSURE_Type)) {
            return;
        }
        Set<MopKey> currentClassSignatures = MopWriter.buildCurrentClassSignatureSet(classNode.getMethods());
        this.visitMopMethodList(classNode.getMethods(), true, Collections.EMPTY_SET, Collections.EMPTY_LIST);
        this.visitMopMethodList(classNode.getSuperClass().getAllDeclaredMethods(), false, currentClassSignatures, this.controller.getSuperMethodNames());
    }

    private static Set<MopKey> buildCurrentClassSignatureSet(List<MethodNode> methods) {
        if (methods.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        HashSet<MopKey> result = new HashSet<MopKey>(methods.size());
        for (MethodNode mn : methods) {
            MopKey key = new MopKey(mn.getName(), mn.getParameters());
            result.add(key);
        }
        return result;
    }

    private void visitMopMethodList(List<MethodNode> methods, boolean isThis, Set<MopKey> useOnlyIfDeclaredHereToo, List<String> orNameMentionedHere) {
        HashMap<MopKey, MethodNode> mops = new HashMap<MopKey, MethodNode>();
        LinkedList<MethodNode> mopCalls = new LinkedList<MethodNode>();
        for (MethodNode mn : methods) {
            String name;
            MopKey key;
            boolean isPrivate;
            if ((mn.getModifiers() & 0x440) != 0 || mn.isStatic() || isThis ^ (isPrivate = Modifier.isPrivate(mn.getModifiers()))) continue;
            String methodName = mn.getName();
            if (MopWriter.isMopMethod(methodName)) {
                mops.put(new MopKey(methodName, mn.getParameters()), mn);
                continue;
            }
            if (methodName.startsWith("<") || !useOnlyIfDeclaredHereToo.contains(new MopKey(methodName, mn.getParameters())) && !orNameMentionedHere.contains(methodName) || mops.containsKey(key = new MopKey(name = MopWriter.getMopMethodName(mn, isThis), mn.getParameters()))) continue;
            mops.put(key, mn);
            mopCalls.add(mn);
        }
        this.generateMopCalls(mopCalls, isThis);
        mopCalls.clear();
        mops.clear();
    }

    public static String getMopMethodName(MethodNode method, boolean useThis) {
        int distance = 0;
        for (ClassNode declaringNode = method.getDeclaringClass(); declaringNode != null; declaringNode = declaringNode.getSuperClass()) {
            ++distance;
        }
        return (useThis ? "this" : "super") + "$" + distance + "$" + method.getName();
    }

    public static boolean isMopMethod(String methodName) {
        return (methodName.startsWith("this$") || methodName.startsWith("super$")) && !methodName.contains("$dist$");
    }

    protected void generateMopCalls(LinkedList<MethodNode> mopCalls, boolean useThis) {
        for (MethodNode method : mopCalls) {
            String name = MopWriter.getMopMethodName(method, useThis);
            Parameter[] parameters = method.getParameters();
            String methodDescriptor = BytecodeHelper.getMethodDescriptor(method.getReturnType(), method.getParameters());
            MethodVisitor mv = this.controller.getClassVisitor().visitMethod(4097, name, methodDescriptor, null, null);
            this.controller.setMethodVisitor(mv);
            mv.visitVarInsn(25, 0);
            int newRegister = 1;
            OperandStack operandStack = this.controller.getOperandStack();
            for (Parameter parameter : parameters) {
                ClassNode type = parameter.getType();
                operandStack.load(parameter.getType(), newRegister);
                ++newRegister;
                if (type != ClassHelper.double_TYPE && type != ClassHelper.long_TYPE) continue;
                ++newRegister;
            }
            operandStack.remove(parameters.length);
            ClassNode declaringClass = method.getDeclaringClass();
            int opcode = declaringClass.isInterface() ? 185 : 183;
            mv.visitMethodInsn(opcode, BytecodeHelper.getClassInternalName(declaringClass), method.getName(), methodDescriptor, opcode == 185);
            BytecodeHelper.doReturn(mv, method.getReturnType());
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            this.controller.getClassNode().addMethod(name, 4097, method.getReturnType(), parameters, null, null);
        }
    }

    public static boolean equalParameterTypes(Parameter[] p1, Parameter[] p2) {
        if (p1.length != p2.length) {
            return false;
        }
        for (int i = 0; i < p1.length; ++i) {
            if (p1[i].getType().equals(p2[i].getType())) continue;
            return false;
        }
        return true;
    }

    private static class MopKey {
        int hash = 0;
        String name;
        Parameter[] params;

        MopKey(String name, Parameter[] params) {
            this.name = name;
            this.params = params;
            this.hash = name.hashCode() << 2 + params.length;
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof MopKey)) {
                return false;
            }
            MopKey other = (MopKey)obj;
            return other.name.equals(this.name) && MopWriter.equalParameterTypes(other.params, this.params);
        }
    }

    public static interface Factory {
        public MopWriter create(WriterController var1);
    }
}

