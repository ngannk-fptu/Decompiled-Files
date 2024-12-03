/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.server.impl.cdi.CDIExtension;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import jersey.repackaged.org.objectweb.asm.ClassWriter;
import jersey.repackaged.org.objectweb.asm.MethodVisitor;

public class BeanGenerator {
    private static final Logger LOGGER = Logger.getLogger(CDIExtension.class.getName());
    private String prefix;
    private Method defineClassMethod;
    private static AtomicInteger generatedClassCounter = new AtomicInteger(0);

    BeanGenerator(String prefix) {
        this.prefix = prefix;
        try {
            this.defineClassMethod = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    Class<?> classLoaderClass = Class.forName("java.lang.ClassLoader");
                    Method method = classLoaderClass.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
                    method.setAccessible(true);
                    return method;
                }
            });
        }
        catch (PrivilegedActionException e) {
            LOGGER.log(Level.SEVERE, "failed to access method ClassLoader.defineClass", e);
            throw new RuntimeException(e);
        }
    }

    Class<?> createBeanClass() {
        ClassWriter writer = new ClassWriter(0);
        String name = this.prefix + Integer.toString(generatedClassCounter.addAndGet(1));
        writer.visit(50, 1, name, null, "java/lang/Object", null);
        MethodVisitor methodVisitor = writer.visitMethod(1, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitMethodInsn(183, "java/lang/Object", "<init>", "()V");
        methodVisitor.visitInsn(177);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
        writer.visitEnd();
        byte[] bytecode = writer.toByteArray();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class result = (Class)this.defineClassMethod.invoke((Object)classLoader, name.replace("/", "."), bytecode, 0, bytecode.length);
            LOGGER.fine("Created class " + result.getName());
            return result;
        }
        catch (Throwable t) {
            LOGGER.log(Level.SEVERE, "error calling ClassLoader.defineClass", t);
            return null;
        }
    }
}

