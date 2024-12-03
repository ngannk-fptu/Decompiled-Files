/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm.$ClassReader;
import com.google.inject.internal.asm.$ClassVisitor;
import com.google.inject.internal.asm.$ClassWriter;
import com.google.inject.internal.cglib.core.$CodeGenerationException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class $DebuggingClassWriter
extends $ClassVisitor {
    public static final String DEBUG_LOCATION_PROPERTY = "cglib.debugLocation";
    private static String debugLocation = System.getProperty("cglib.debugLocation");
    private static Constructor traceCtor;
    private String className;
    private String superName;
    static /* synthetic */ Class class$org$objectweb$asm$ClassVisitor;
    static /* synthetic */ Class class$java$io$PrintWriter;

    public $DebuggingClassWriter(int flags) {
        super(262144, new $ClassWriter(flags));
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name.replace('/', '.');
        this.superName = superName.replace('/', '.');
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public String getClassName() {
        return this.className;
    }

    public String getSuperName() {
        return this.superName;
    }

    public byte[] toByteArray() {
        return (byte[])AccessController.doPrivileged(new PrivilegedAction(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public Object run() {
                byte[] b;
                block10: {
                    b = (($ClassWriter)$DebuggingClassWriter.this.cv).toByteArray();
                    if (debugLocation != null) {
                        String dirs = $DebuggingClassWriter.this.className.replace('.', File.separatorChar);
                        try {
                            new File(debugLocation + File.separatorChar + dirs).getParentFile().mkdirs();
                            File file = new File(new File(debugLocation), dirs + ".class");
                            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                            try {
                                ((OutputStream)out).write(b);
                            }
                            finally {
                                ((OutputStream)out).close();
                            }
                            if (traceCtor == null) break block10;
                            file = new File(new File(debugLocation), dirs + ".asm");
                            out = new BufferedOutputStream(new FileOutputStream(file));
                            try {
                                $ClassReader cr = new $ClassReader(b);
                                PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                                $ClassVisitor tcv = ($ClassVisitor)traceCtor.newInstance(null, pw);
                                cr.accept(tcv, 0);
                                pw.flush();
                            }
                            finally {
                                ((OutputStream)out).close();
                            }
                        }
                        catch (Exception e) {
                            throw new $CodeGenerationException(e);
                        }
                    }
                }
                return b;
            }
        });
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        if (debugLocation != null) {
            System.err.println("CGLIB debugging enabled, writing to '" + debugLocation + "'");
            try {
                Class<?> clazz = Class.forName("com.google.inject.internal.asm.util.$TraceClassVisitor");
                traceCtor = clazz.getConstructor(class$org$objectweb$asm$ClassVisitor == null ? (class$org$objectweb$asm$ClassVisitor = $DebuggingClassWriter.class$("com.google.inject.internal.asm.$ClassVisitor")) : class$org$objectweb$asm$ClassVisitor, class$java$io$PrintWriter == null ? (class$java$io$PrintWriter = $DebuggingClassWriter.class$("java.io.PrintWriter")) : class$java$io$PrintWriter);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }
}

