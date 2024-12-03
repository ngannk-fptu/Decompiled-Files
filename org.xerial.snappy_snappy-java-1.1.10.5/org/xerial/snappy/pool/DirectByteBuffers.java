/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy.pool;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;

final class DirectByteBuffers {
    static final Class<? extends ByteBuffer> DIRECT_BUFFER_CLAZZ = DirectByteBuffers.lookupClassQuietly("java.nio.DirectByteBuffer");
    static final MethodHandle CLEAN_HANDLE;

    DirectByteBuffers() {
    }

    private static Class<?> lookupClassQuietly(String string) {
        try {
            return DirectByteBuffers.class.getClassLoader().loadClass(string);
        }
        catch (Throwable throwable) {
            Logger.getLogger(DirectByteBuffers.class.getName()).log(Level.FINE, "Did not find requested class: " + string, throwable);
            return null;
        }
    }

    static boolean nonNull(Object object) {
        return object != null;
    }

    public static void releaseDirectByteBuffer(final ByteBuffer byteBuffer) {
        assert (byteBuffer != null && byteBuffer.isDirect());
        if (CLEAN_HANDLE != null && DIRECT_BUFFER_CLAZZ.isInstance(byteBuffer)) {
            try {
                PrivilegedExceptionAction<Void> privilegedExceptionAction = new PrivilegedExceptionAction<Void>(){

                    @Override
                    public Void run() throws Exception {
                        try {
                            CLEAN_HANDLE.invokeExact(byteBuffer);
                        }
                        catch (Exception exception) {
                            throw exception;
                        }
                        catch (Throwable throwable) {
                            throw new RuntimeException(throwable);
                        }
                        return null;
                    }
                };
                AccessController.doPrivileged(privilegedExceptionAction);
            }
            catch (Throwable throwable) {
                Logger.getLogger(DirectByteBuffers.class.getName()).log(Level.FINE, "Exception occurred attempting to clean up Sun specific DirectByteBuffer.", throwable);
            }
        }
    }

    static {
        MethodHandle methodHandle = null;
        try {
            PrivilegedExceptionAction<MethodHandle> privilegedExceptionAction = new PrivilegedExceptionAction<MethodHandle>(){

                @Override
                public MethodHandle run() throws Exception {
                    MethodHandle methodHandle = null;
                    if (DIRECT_BUFFER_CLAZZ != null) {
                        MethodHandles.Lookup lookup = MethodHandles.lookup();
                        try {
                            Class<?> clazz = Class.forName("sun.misc.Unsafe");
                            MethodHandle methodHandle2 = lookup.findVirtual(clazz, "invokeCleaner", MethodType.methodType(Void.TYPE, ByteBuffer.class));
                            Field field = clazz.getDeclaredField("theUnsafe");
                            field.setAccessible(true);
                            Object object = field.get(null);
                            methodHandle = methodHandle2.bindTo(object);
                        }
                        catch (Exception exception) {
                            Logger.getLogger(DirectByteBuffers.class.getName()).log(Level.FINE, "unable to use java 9 Unsafe.invokeCleaner", exception);
                            Method method = DIRECT_BUFFER_CLAZZ.getMethod("cleaner", new Class[0]);
                            method.setAccessible(true);
                            MethodHandle methodHandle3 = lookup.unreflect(method);
                            TypeDescriptor.OfField ofField = methodHandle3.type().returnType();
                            MethodHandle methodHandle4 = lookup.findVirtual((Class<?>)ofField, "clean", MethodType.methodType(Void.TYPE));
                            MethodHandle methodHandle5 = lookup.findStatic(DirectByteBuffers.class, "nonNull", MethodType.methodType(Boolean.TYPE, Object.class)).asType(MethodType.methodType(Boolean.TYPE, ofField));
                            MethodHandle methodHandle6 = MethodHandles.dropArguments(MethodHandles.constant(Void.class, null).asType(MethodType.methodType(Void.TYPE)), 0, new Class[]{ofField});
                            methodHandle = MethodHandles.filterReturnValue(methodHandle3, MethodHandles.guardWithTest(methodHandle5, methodHandle4, methodHandle6)).asType(MethodType.methodType(Void.TYPE, ByteBuffer.class));
                        }
                    }
                    return methodHandle;
                }
            };
            methodHandle = AccessController.doPrivileged(privilegedExceptionAction);
        }
        catch (Throwable throwable) {
            Logger.getLogger(DirectByteBuffers.class.getName()).log(Level.FINE, "Exception occurred attempting to lookup Sun specific DirectByteBuffer cleaner classes.", throwable);
        }
        CLEAN_HANDLE = methodHandle;
    }
}

