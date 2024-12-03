/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools;

import groovyjarjarasm.asm.ClassWriter;
import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class DgmConverter
implements Opcodes {
    public static void main(String[] args) throws IOException {
        boolean info;
        String targetDirectory = "target/classes/";
        boolean bl = info = args.length == 1 && args[0].equals("--info") || args.length == 2 && args[0].equals("--info");
        if (info && args.length == 2 && !(targetDirectory = args[1]).endsWith("/")) {
            targetDirectory = targetDirectory + "/";
        }
        ArrayList cachedMethodsList = new ArrayList();
        for (Class aClass : DefaultGroovyMethods.DGM_LIKE_CLASSES) {
            Collections.addAll(cachedMethodsList, ReflectionCache.getCachedClass(aClass).getMethods());
        }
        CachedMethod[] cachedMethods = cachedMethodsList.toArray(new CachedMethod[cachedMethodsList.size()]);
        ArrayList<GeneratedMetaMethod.DgmMethodRecord> records = new ArrayList<GeneratedMetaMethod.DgmMethodRecord>();
        int cur = 0;
        for (CachedMethod method : cachedMethods) {
            if (!method.isStatic() || !method.isPublic() || method.getCachedMethod().getAnnotation(Deprecated.class) != null || method.getParameterTypes().length == 0) continue;
            Class returnType = method.getReturnType();
            String className = "org/codehaus/groovy/runtime/dgm$" + cur++;
            GeneratedMetaMethod.DgmMethodRecord record = new GeneratedMetaMethod.DgmMethodRecord();
            records.add(record);
            record.methodName = method.getName();
            record.returnType = method.getReturnType();
            record.parameters = method.getNativeParameterTypes();
            record.className = className;
            ClassWriter cw = new ClassWriter(1);
            cw.visit(47, 1, className, null, "org/codehaus/groovy/reflection/GeneratedMetaMethod", null);
            DgmConverter.createConstructor(cw);
            String methodDescriptor = BytecodeHelper.getMethodDescriptor(returnType, method.getNativeParameterTypes());
            DgmConverter.createInvokeMethod(method, cw, returnType, methodDescriptor);
            DgmConverter.createDoMethodInvokeMethod(method, cw, className, returnType, methodDescriptor);
            DgmConverter.createIsValidMethodMethod(method, cw, className);
            cw.visitEnd();
            byte[] bytes = cw.toByteArray();
            FileOutputStream fileOutputStream = new FileOutputStream(targetDirectory + className + ".class");
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        GeneratedMetaMethod.DgmMethodRecord.saveDgmInfo(records, targetDirectory + "/META-INF/dgminfo");
        if (info) {
            System.out.println("Saved " + cur + " dgm records to: " + targetDirectory + "/META-INF/dgminfo");
        }
    }

    private static void createConstructor(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(1, "<init>", "(Ljava/lang/String;Lorg/codehaus/groovy/reflection/CachedClass;Ljava/lang/Class;[Ljava/lang/Class;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitVarInsn(25, 2);
        mv.visitVarInsn(25, 3);
        mv.visitVarInsn(25, 4);
        mv.visitMethodInsn(183, "org/codehaus/groovy/reflection/GeneratedMetaMethod", "<init>", "(Ljava/lang/String;Lorg/codehaus/groovy/reflection/CachedClass;Ljava/lang/Class;[Ljava/lang/Class;)V", false);
        mv.visitInsn(177);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void createIsValidMethodMethod(CachedMethod method, ClassWriter cw, String className) {
        if (method.getParamsCount() == 2 && method.getParameterTypes()[0].isNumber && method.getParameterTypes()[1].isNumber) {
            MethodVisitor mv = cw.visitMethod(1, "isValidMethod", "([Ljava/lang/Class;)Z", null, null);
            mv.visitCode();
            mv.visitVarInsn(25, 1);
            Label l0 = new Label();
            mv.visitJumpInsn(198, l0);
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(182, className, "getParameterTypes", "()[Lorg/codehaus/groovy/reflection/CachedClass;", false);
            mv.visitInsn(3);
            mv.visitInsn(50);
            mv.visitVarInsn(25, 1);
            mv.visitInsn(3);
            mv.visitInsn(50);
            mv.visitMethodInsn(182, "org/codehaus/groovy/reflection/CachedClass", "isAssignableFrom", "(Ljava/lang/Class;)Z", false);
            Label l1 = new Label();
            mv.visitJumpInsn(153, l1);
            mv.visitLabel(l0);
            mv.visitInsn(4);
            Label l2 = new Label();
            mv.visitJumpInsn(167, l2);
            mv.visitLabel(l1);
            mv.visitInsn(3);
            mv.visitLabel(l2);
            mv.visitInsn(172);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
    }

    private static void createDoMethodInvokeMethod(CachedMethod method, ClassWriter cw, String className, Class returnType, String methodDescriptor) {
        MethodVisitor mv = cw.visitMethod(17, "doMethodInvoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();
        if (method.getParamsCount() == 2 && method.getParameterTypes()[0].isNumber && method.getParameterTypes()[1].isNumber) {
            mv.visitVarInsn(25, 1);
            BytecodeHelper.doCast(mv, method.getParameterTypes()[0].getTheClass());
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(182, className, "getParameterTypes", "()[Lorg/codehaus/groovy/reflection/CachedClass;", false);
            mv.visitInsn(3);
            mv.visitInsn(50);
            mv.visitVarInsn(25, 2);
            mv.visitInsn(3);
            mv.visitInsn(50);
            mv.visitMethodInsn(182, "org/codehaus/groovy/reflection/CachedClass", "coerceArgument", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            Class type = method.getParameterTypes()[1].getTheClass();
            BytecodeHelper.doCast(mv, type);
        } else {
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(25, 2);
            mv.visitMethodInsn(182, className, "coerceArgumentsToClasses", "([Ljava/lang/Object;)[Ljava/lang/Object;", false);
            mv.visitVarInsn(58, 2);
            mv.visitVarInsn(25, 1);
            BytecodeHelper.doCast(mv, method.getParameterTypes()[0].getTheClass());
            DgmConverter.loadParameters(method, 2, mv);
        }
        mv.visitMethodInsn(184, BytecodeHelper.getClassInternalName(method.getDeclaringClass().getTheClass()), method.getName(), methodDescriptor, false);
        BytecodeHelper.box(mv, returnType);
        if (method.getReturnType() == Void.TYPE) {
            mv.visitInsn(1);
        }
        mv.visitInsn(176);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void createInvokeMethod(CachedMethod method, ClassWriter cw, Class returnType, String methodDescriptor) {
        MethodVisitor mv = cw.visitMethod(1, "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 1);
        BytecodeHelper.doCast(mv, method.getParameterTypes()[0].getTheClass());
        DgmConverter.loadParameters(method, 2, mv);
        mv.visitMethodInsn(184, BytecodeHelper.getClassInternalName(method.getDeclaringClass().getTheClass()), method.getName(), methodDescriptor, false);
        BytecodeHelper.box(mv, returnType);
        if (method.getReturnType() == Void.TYPE) {
            mv.visitInsn(1);
        }
        mv.visitInsn(176);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    protected static void loadParameters(CachedMethod method, int argumentIndex, MethodVisitor mv) {
        CachedClass[] parameters = method.getParameterTypes();
        int size = parameters.length - 1;
        for (int i = 0; i < size; ++i) {
            mv.visitVarInsn(25, argumentIndex);
            BytecodeHelper.pushConstant(mv, i);
            mv.visitInsn(50);
            Class type = parameters[i + 1].getTheClass();
            BytecodeHelper.doCast(mv, type);
        }
    }
}

