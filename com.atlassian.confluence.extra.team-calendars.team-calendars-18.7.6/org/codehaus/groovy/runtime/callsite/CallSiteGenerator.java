/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyRuntimeException;
import groovyjarjarasm.asm.ClassWriter;
import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.android.AndroidSupport;
import org.codehaus.groovy.runtime.callsite.CallSiteClassLoader;
import org.codehaus.groovy.runtime.callsite.GroovySunClassLoader;
import org.codehaus.groovy.vmplugin.VMPluginFactory;

public class CallSiteGenerator {
    private static final String GRE = BytecodeHelper.getClassInternalName(ClassHelper.make(GroovyRuntimeException.class));

    private CallSiteGenerator() {
    }

    private static MethodVisitor writeMethod(ClassWriter cw, String name, int argumentCount, String superClass, CachedMethod cachedMethod, String receiverType, String parameterDescription, boolean useArray) {
        int i;
        MethodVisitor mv = cw.visitMethod(1, "call" + name, "(L" + receiverType + ";" + parameterDescription + ")Ljava/lang/Object;", null, null);
        mv.visitCode();
        Label tryStart = new Label();
        mv.visitLabel(tryStart);
        for (int i2 = 0; i2 < argumentCount; ++i2) {
            mv.visitVarInsn(25, i2);
        }
        mv.visitMethodInsn(182, superClass, "checkCall", "(Ljava/lang/Object;" + parameterDescription + ")Z", false);
        Label l0 = new Label();
        mv.visitJumpInsn(153, l0);
        Class callClass = cachedMethod.getDeclaringClass().getTheClass();
        boolean useInterface = callClass.isInterface();
        String type = BytecodeHelper.getClassInternalName(callClass.getName());
        String descriptor = BytecodeHelper.getMethodDescriptor(cachedMethod.getReturnType(), cachedMethod.getNativeParameterTypes());
        int invokeMethodCode = 182;
        if (cachedMethod.isStatic()) {
            invokeMethodCode = 184;
        } else {
            mv.visitVarInsn(25, 1);
            BytecodeHelper.doCast(mv, callClass);
            if (useInterface) {
                invokeMethodCode = 185;
            }
        }
        Method method = cachedMethod.setAccessible();
        Class<?>[] parameters = method.getParameterTypes();
        int size = parameters.length;
        for (i = 0; i < size; ++i) {
            if (useArray) {
                mv.visitVarInsn(25, 2);
                BytecodeHelper.pushConstant(mv, i);
                mv.visitInsn(50);
            } else {
                mv.visitVarInsn(25, i + 2);
            }
            BytecodeHelper.doCast(mv, parameters[i]);
        }
        mv.visitMethodInsn(invokeMethodCode, type, cachedMethod.getName(), descriptor, useInterface);
        BytecodeHelper.box(mv, cachedMethod.getReturnType());
        if (cachedMethod.getReturnType() == Void.TYPE) {
            mv.visitInsn(1);
        }
        mv.visitInsn(176);
        mv.visitLabel(l0);
        for (i = 0; i < argumentCount; ++i) {
            mv.visitVarInsn(25, i);
        }
        if (!useArray) {
            mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/ArrayUtil", "createArray", "(" + parameterDescription + ")[Ljava/lang/Object;", false);
        }
        mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/callsite/CallSiteArray", "defaultCall" + name, "(Lorg/codehaus/groovy/runtime/callsite/CallSite;L" + receiverType + ";[Ljava/lang/Object;)Ljava/lang/Object;", false);
        mv.visitInsn(176);
        Label tryEnd = new Label();
        mv.visitLabel(tryEnd);
        Label catchStart = new Label();
        mv.visitLabel(catchStart);
        mv.visitMethodInsn(184, "org/codehaus/groovy/runtime/ScriptBytecodeAdapter", "unwrap", "(Lgroovy/lang/GroovyRuntimeException;)Ljava/lang/Throwable;", false);
        mv.visitInsn(191);
        mv.visitTryCatchBlock(tryStart, tryEnd, catchStart, GRE);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        return mv;
    }

    public static void genCallWithFixedParams(ClassWriter cw, String name, String superClass, CachedMethod cachedMethod, String receiverType) {
        if (cachedMethod.getParamsCount() > 4) {
            return;
        }
        StringBuilder pdescb = new StringBuilder();
        int pc = cachedMethod.getParamsCount();
        for (int i = 0; i != pc; ++i) {
            pdescb.append("Ljava/lang/Object;");
        }
        CallSiteGenerator.writeMethod(cw, name, pc + 2, superClass, cachedMethod, receiverType, pdescb.toString(), false);
    }

    public static void genCallXxxWithArray(ClassWriter cw, String name, String superClass, CachedMethod cachedMethod, String receiverType) {
        CallSiteGenerator.writeMethod(cw, name, 3, superClass, cachedMethod, receiverType, "[Ljava/lang/Object;", true);
    }

    private static void genConstructor(ClassWriter cw, String superClass, String internalName) {
        MethodVisitor mv = cw.visitMethod(1, "<init>", "(Lorg/codehaus/groovy/runtime/callsite/CallSite;Lgroovy/lang/MetaClassImpl;Lgroovy/lang/MetaMethod;[Ljava/lang/Class;Ljava/lang/reflect/Constructor;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitVarInsn(25, 1);
        mv.visitVarInsn(25, 2);
        mv.visitVarInsn(25, 3);
        mv.visitVarInsn(25, 4);
        mv.visitMethodInsn(183, superClass, "<init>", "(Lorg/codehaus/groovy/runtime/callsite/CallSite;Lgroovy/lang/MetaClassImpl;Lgroovy/lang/MetaMethod;[Ljava/lang/Class;)V", false);
        mv.visitVarInsn(25, 5);
        mv.visitFieldInsn(179, internalName, "__constructor__", "Ljava/lang/reflect/Constructor;");
        mv.visitInsn(177);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void classHeader(ClassWriter cw, String internalName, String superName) {
        if (VMPluginFactory.getPlugin().getVersion() >= 8) {
            cw.visit(52, 4097, internalName, null, superName, null);
        } else {
            cw.visit(48, 4097, internalName, null, superName, null);
        }
    }

    public static byte[] genPogoMetaMethodSite(CachedMethod cachedMethod, ClassWriter cw, String name) {
        String internalName = name.replace('.', '/');
        CallSiteGenerator.classHeader(cw, internalName, "org/codehaus/groovy/runtime/callsite/PogoMetaMethodSite");
        cw.visitField(9, "__constructor__", "Ljava/lang/reflect/Constructor;", null, null);
        CallSiteGenerator.genConstructor(cw, "org/codehaus/groovy/runtime/callsite/PogoMetaMethodSite", internalName);
        CallSiteGenerator.genCallXxxWithArray(cw, "Current", "org/codehaus/groovy/runtime/callsite/PogoMetaMethodSite", cachedMethod, "groovy/lang/GroovyObject");
        CallSiteGenerator.genCallXxxWithArray(cw, "", "org/codehaus/groovy/runtime/callsite/PogoMetaMethodSite", cachedMethod, "java/lang/Object");
        CallSiteGenerator.genCallWithFixedParams(cw, "Current", "org/codehaus/groovy/runtime/callsite/PogoMetaMethodSite", cachedMethod, "groovy/lang/GroovyObject");
        CallSiteGenerator.genCallWithFixedParams(cw, "", "org/codehaus/groovy/runtime/callsite/PogoMetaMethodSite", cachedMethod, "java/lang/Object");
        cw.visitEnd();
        return cw.toByteArray();
    }

    public static byte[] genPojoMetaMethodSite(CachedMethod cachedMethod, ClassWriter cw, String name) {
        String internalName = name.replace('.', '/');
        CallSiteGenerator.classHeader(cw, internalName, "org/codehaus/groovy/runtime/callsite/PojoMetaMethodSite");
        cw.visitField(9, "__constructor__", "Ljava/lang/reflect/Constructor;", null, null);
        CallSiteGenerator.genConstructor(cw, "org/codehaus/groovy/runtime/callsite/PojoMetaMethodSite", internalName);
        CallSiteGenerator.genCallXxxWithArray(cw, "", "org/codehaus/groovy/runtime/callsite/PojoMetaMethodSite", cachedMethod, "java/lang/Object");
        CallSiteGenerator.genCallWithFixedParams(cw, "", "org/codehaus/groovy/runtime/callsite/PojoMetaMethodSite", cachedMethod, "java/lang/Object");
        cw.visitEnd();
        return cw.toByteArray();
    }

    public static byte[] genStaticMetaMethodSite(CachedMethod cachedMethod, ClassWriter cw, String name) {
        String internalName = name.replace('.', '/');
        CallSiteGenerator.classHeader(cw, internalName, "org/codehaus/groovy/runtime/callsite/StaticMetaMethodSite");
        cw.visitField(9, "__constructor__", "Ljava/lang/reflect/Constructor;", null, null);
        CallSiteGenerator.genConstructor(cw, "org/codehaus/groovy/runtime/callsite/StaticMetaMethodSite", internalName);
        CallSiteGenerator.genCallXxxWithArray(cw, "", "org/codehaus/groovy/runtime/callsite/StaticMetaMethodSite", cachedMethod, "java/lang/Object");
        CallSiteGenerator.genCallXxxWithArray(cw, "Static", "org/codehaus/groovy/runtime/callsite/StaticMetaMethodSite", cachedMethod, "java/lang/Class");
        CallSiteGenerator.genCallWithFixedParams(cw, "", "org/codehaus/groovy/runtime/callsite/StaticMetaMethodSite", cachedMethod, "java/lang/Object");
        CallSiteGenerator.genCallWithFixedParams(cw, "Static", "org/codehaus/groovy/runtime/callsite/StaticMetaMethodSite", cachedMethod, "java/lang/Class");
        cw.visitEnd();
        return cw.toByteArray();
    }

    private static ClassWriter makeClassWriter() {
        if (VMPluginFactory.getPlugin().getVersion() >= 8) {
            return new ClassWriter(3);
        }
        return new ClassWriter(1);
    }

    public static Constructor compilePogoMethod(CachedMethod cachedMethod) {
        ClassWriter cw = CallSiteGenerator.makeClassWriter();
        CachedClass declClass = cachedMethod.getDeclaringClass();
        CallSiteClassLoader callSiteLoader = declClass.getCallSiteLoader();
        String name = callSiteLoader.createClassName(cachedMethod.setAccessible());
        byte[] bytes = CallSiteGenerator.genPogoMetaMethodSite(cachedMethod, cw, name);
        return callSiteLoader.defineClassAndGetConstructor(name, bytes);
    }

    public static Constructor compilePojoMethod(CachedMethod cachedMethod) {
        ClassWriter cw = CallSiteGenerator.makeClassWriter();
        CachedClass declClass = cachedMethod.getDeclaringClass();
        CallSiteClassLoader callSiteLoader = declClass.getCallSiteLoader();
        String name = callSiteLoader.createClassName(cachedMethod.setAccessible());
        byte[] bytes = CallSiteGenerator.genPojoMetaMethodSite(cachedMethod, cw, name);
        return callSiteLoader.defineClassAndGetConstructor(name, bytes);
    }

    public static Constructor compileStaticMethod(CachedMethod cachedMethod) {
        ClassWriter cw = CallSiteGenerator.makeClassWriter();
        CachedClass declClass = cachedMethod.getDeclaringClass();
        CallSiteClassLoader callSiteLoader = declClass.getCallSiteLoader();
        String name = callSiteLoader.createClassName(cachedMethod.setAccessible());
        byte[] bytes = CallSiteGenerator.genStaticMetaMethodSite(cachedMethod, cw, name);
        return callSiteLoader.defineClassAndGetConstructor(name, bytes);
    }

    public static boolean isCompilable(CachedMethod method) {
        return (GroovySunClassLoader.sunVM != null || Modifier.isPublic(method.cachedClass.getModifiers()) && method.isPublic() && CallSiteGenerator.publicParams(method)) && !AndroidSupport.isRunningAndroid() && CallSiteGenerator.containsOnlyValidChars(method.getName());
    }

    private static boolean publicParams(CachedMethod method) {
        for (Class nativeParamType : method.getNativeParameterTypes()) {
            if (Modifier.isPublic(nativeParamType.getModifiers())) continue;
            return false;
        }
        return true;
    }

    private static boolean containsOnlyValidChars(String name) {
        String encoded = GeneratorContext.encodeAsValidClassName(name);
        return encoded.equals(name);
    }
}

