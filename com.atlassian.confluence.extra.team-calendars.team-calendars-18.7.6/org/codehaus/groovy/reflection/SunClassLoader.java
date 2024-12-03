/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import groovyjarjarasm.asm.ClassReader;
import groovyjarjarasm.asm.ClassWriter;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

public class SunClassLoader
extends ClassLoader
implements Opcodes {
    protected final Map<String, Class> knownClasses = new HashMap<String, Class>();
    protected static final SunClassLoader sunVM;

    protected SunClassLoader() throws Throwable {
        super(SunClassLoader.class.getClassLoader());
        Class<?> magic = ClassLoader.getSystemClassLoader().loadClass("sun.reflect.MagicAccessorImpl");
        this.knownClasses.put("sun.reflect.MagicAccessorImpl", magic);
        this.loadMagic();
    }

    private void loadMagic() {
        ClassWriter cw = new ClassWriter(1);
        cw.visit(48, 1, "sun/reflect/GroovyMagic", null, "sun/reflect/MagicAccessorImpl", null);
        MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, "sun/reflect/MagicAccessorImpl", "<init>", "()V", false);
        mv.visitInsn(177);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();
        this.define(cw.toByteArray(), "sun.reflect.GroovyMagic");
    }

    protected void loadFromRes(String name) throws IOException {
        InputStream asStream = SunClassLoader.class.getClassLoader().getResourceAsStream(SunClassLoader.resName(name));
        ClassReader reader = new ClassReader(asStream);
        ClassWriter cw = new ClassWriter(1);
        reader.accept(cw, 1);
        asStream.close();
        this.define(cw.toByteArray(), name);
    }

    protected static String resName(String s) {
        return s.replace('.', '/') + ".class";
    }

    protected void define(byte[] bytes, String name) {
        this.knownClasses.put(name, this.defineClass(name, bytes, 0, bytes.length));
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class aClass = this.knownClasses.get(name);
        if (aClass != null) {
            return aClass;
        }
        try {
            return super.loadClass(name, resolve);
        }
        catch (ClassNotFoundException e) {
            return this.getClass().getClassLoader().loadClass(name);
        }
    }

    public Class doesKnow(String name) {
        return this.knownClasses.get(name);
    }

    static {
        SunClassLoader res;
        try {
            res = AccessController.doPrivileged(new PrivilegedAction<SunClassLoader>(){

                @Override
                public SunClassLoader run() {
                    try {
                        return new SunClassLoader();
                    }
                    catch (Throwable e) {
                        return null;
                    }
                }
            });
        }
        catch (Throwable e) {
            res = null;
        }
        sunVM = res;
    }
}

