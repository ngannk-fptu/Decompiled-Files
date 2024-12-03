/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.bcel.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.StringUtils;

public class JavaWrapper {
    private final ClassLoader loader;

    private static ClassLoader getClassLoader() {
        String s = System.getProperty("bcel.classloader");
        if (StringUtils.isEmpty((CharSequence)s)) {
            throw new IllegalStateException("The property 'bcel.classloader' must be defined");
        }
        try {
            return (ClassLoader)Class.forName(s).newInstance();
        }
        catch (Exception e) {
            throw new IllegalStateException(e.toString(), e);
        }
    }

    public static void main(String[] argv) throws Exception {
        if (argv.length == 0) {
            System.out.println("Missing class name.");
            return;
        }
        String className = argv[0];
        String[] newArgv = new String[argv.length - 1];
        System.arraycopy(argv, 1, newArgv, 0, newArgv.length);
        new JavaWrapper().runMain(className, newArgv);
    }

    public JavaWrapper() {
        this(JavaWrapper.getClassLoader());
    }

    public JavaWrapper(ClassLoader loader) {
        this.loader = loader;
    }

    public void runMain(String className, String[] argv) throws ClassNotFoundException {
        Class<?> cl = this.loader.loadClass(className);
        Method method = null;
        try {
            method = cl.getMethod("main", argv.getClass());
            int m = method.getModifiers();
            Class<?> r = method.getReturnType();
            if (!Modifier.isPublic(m) || !Modifier.isStatic(m) || Modifier.isAbstract(m) || r != Void.TYPE) {
                throw new NoSuchMethodException();
            }
        }
        catch (NoSuchMethodException no) {
            System.out.println("In class " + className + ": public static void main(String[] argv) is not defined");
            return;
        }
        try {
            method.invoke(null, (Object[])argv);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

