/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.ClassReader
 *  org.objectweb.asm.ClassVisitor
 *  org.objectweb.asm.ClassWriter
 */
package net.sf.cglib.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import net.sf.cglib.core.CodeGenerationException;
import net.sf.cglib.core.Constants;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class DebuggingClassWriter
extends ClassVisitor {
    public static final String DEBUG_LOCATION_PROPERTY = "cglib.debugLocation";
    private static String debugLocation = System.getProperty("cglib.debugLocation");
    private static Constructor traceCtor;
    private String className;
    private String superName;

    public DebuggingClassWriter(int flags) {
        super(Constants.ASM_API, (ClassVisitor)new ClassWriter(flags));
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
                    b = ((ClassWriter)DebuggingClassWriter.this.cv).toByteArray();
                    if (debugLocation != null) {
                        String dirs = DebuggingClassWriter.this.className.replace('.', File.separatorChar);
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
                                ClassReader cr = new ClassReader(b);
                                PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                                ClassVisitor tcv = (ClassVisitor)traceCtor.newInstance(null, pw);
                                cr.accept(tcv, 0);
                                pw.flush();
                            }
                            finally {
                                ((OutputStream)out).close();
                            }
                        }
                        catch (Exception e) {
                            throw new CodeGenerationException(e);
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
                Class<?> clazz = Class.forName("org.objectweb.asm.util.TraceClassVisitor");
                traceCtor = clazz.getConstructor(ClassVisitor.class, PrintWriter.class);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }
}

