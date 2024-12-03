/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.internal.asm.util.$TraceClassVisitor
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm.$ClassReader;
import com.google.inject.internal.asm.$ClassVisitor;
import com.google.inject.internal.asm.$ClassWriter;
import com.google.inject.internal.asm.util.;
import com.google.inject.internal.cglib.core.$CodeGenerationException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class $DebuggingClassWriter
extends $ClassWriter {
    public static final String DEBUG_LOCATION_PROPERTY = "cglib.debugLocation";
    private static String debugLocation = System.getProperty("cglib.debugLocation");
    private static boolean traceEnabled;
    private String className;
    private String superName;

    public $DebuggingClassWriter(int flags) {
        super(flags);
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
                    b = $DebuggingClassWriter.super.toByteArray();
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
                            if (!traceEnabled) break block10;
                            file = new File(new File(debugLocation), dirs + ".asm");
                            out = new BufferedOutputStream(new FileOutputStream(file));
                            try {
                                $ClassReader cr = new $ClassReader(b);
                                PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                                .TraceClassVisitor tcv = new .TraceClassVisitor(null, pw);
                                cr.accept(($ClassVisitor)tcv, 0);
                                pw.flush();
                            }
                            finally {
                                ((OutputStream)out).close();
                            }
                        }
                        catch (IOException e) {
                            throw new $CodeGenerationException(e);
                        }
                    }
                }
                return b;
            }
        });
    }

    static {
        if (debugLocation != null) {
            System.err.println("CGLIB debugging enabled, writing to '" + debugLocation + "'");
            try {
                Class.forName("com.google.inject.internal.asm.util.$TraceClassVisitor");
                traceEnabled = true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }
}

