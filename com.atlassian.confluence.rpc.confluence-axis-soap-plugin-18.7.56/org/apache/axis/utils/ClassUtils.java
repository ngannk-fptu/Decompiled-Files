/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import org.apache.axis.utils.StringUtils;

public final class ClassUtils {
    private static ClassLoader defaultClassLoader = (class$org$apache$axis$utils$ClassUtils == null ? (class$org$apache$axis$utils$ClassUtils = ClassUtils.class$("org.apache.axis.utils.ClassUtils")) : class$org$apache$axis$utils$ClassUtils).getClassLoader();
    private static Hashtable classloaders = new Hashtable();
    static /* synthetic */ Class class$org$apache$axis$utils$ClassUtils;

    public static void setDefaultClassLoader(ClassLoader loader) {
        if (loader != null) {
            defaultClassLoader = loader;
        }
    }

    public static ClassLoader getDefaultClassLoader() {
        return defaultClassLoader;
    }

    public static void setClassLoader(String className, ClassLoader loader) {
        if (className != null && loader != null) {
            classloaders.put(className, loader);
        }
    }

    public static ClassLoader getClassLoader(String className) {
        if (className == null) {
            return null;
        }
        return (ClassLoader)classloaders.get(className);
    }

    public static void removeClassLoader(String className) {
        classloaders.remove(className);
    }

    public static Class forName(String className) throws ClassNotFoundException {
        return ClassUtils.loadClass(className);
    }

    public static Class forName(String _className, boolean init, ClassLoader _loader) throws ClassNotFoundException {
        final String className = _className;
        final ClassLoader loader = _loader;
        try {
            Object ret = AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    try {
                        return Class.forName(className, true, loader);
                    }
                    catch (Throwable e) {
                        return e;
                    }
                }
            });
            if (ret instanceof Class) {
                return (Class)ret;
            }
            if (ret instanceof ClassNotFoundException) {
                throw (ClassNotFoundException)ret;
            }
            throw new ClassNotFoundException(_className);
        }
        catch (ClassNotFoundException cnfe) {
            return ClassUtils.loadClass(className);
        }
    }

    private static Class loadClass(String _className) throws ClassNotFoundException {
        final String className = _className;
        Object ret = AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                try {
                    ClassLoader classLoader = ClassUtils.getClassLoader(className);
                    return Class.forName(className, true, classLoader);
                }
                catch (ClassNotFoundException cnfe) {
                    try {
                        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                        return Class.forName(className, true, classLoader);
                    }
                    catch (ClassNotFoundException cnfe2) {
                        try {
                            ClassLoader classLoader = (class$org$apache$axis$utils$ClassUtils == null ? (class$org$apache$axis$utils$ClassUtils = ClassUtils.class$("org.apache.axis.utils.ClassUtils")) : class$org$apache$axis$utils$ClassUtils).getClassLoader();
                            return Class.forName(className, true, classLoader);
                        }
                        catch (ClassNotFoundException cnfe3) {
                            try {
                                return defaultClassLoader.loadClass(className);
                            }
                            catch (Throwable e) {
                                return e;
                            }
                        }
                    }
                }
            }
        });
        if (ret instanceof Class) {
            return (Class)ret;
        }
        if (ret instanceof ClassNotFoundException) {
            throw (ClassNotFoundException)ret;
        }
        throw new ClassNotFoundException(_className);
    }

    public static InputStream getResourceAsStream(Class clazz, String resource, boolean checkThreadContextFirst) {
        InputStream myInputStream = null;
        if (checkThreadContextFirst && Thread.currentThread().getContextClassLoader() != null) {
            myInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        }
        if (myInputStream == null) {
            myInputStream = ClassUtils.getResourceAsStream(clazz, resource);
        }
        return myInputStream;
    }

    public static InputStream getResourceAsStream(Class clazz, String resource) {
        InputStream myInputStream = null;
        myInputStream = clazz.getClassLoader() != null ? clazz.getClassLoader().getResourceAsStream(resource) : ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
        if (myInputStream == null && Thread.currentThread().getContextClassLoader() != null) {
            myInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        }
        if (myInputStream == null) {
            myInputStream = clazz.getResourceAsStream(resource);
        }
        return myInputStream;
    }

    public static ClassLoader createClassLoader(String classpath, ClassLoader parent) throws SecurityException {
        String[] names = StringUtils.split(classpath, System.getProperty("path.separator").charAt(0));
        URL[] urls = new URL[names.length];
        try {
            for (int i = 0; i < urls.length; ++i) {
                urls[i] = new File(names[i]).toURL();
            }
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Unable to parse classpath: " + classpath);
        }
        return new URLClassLoader(urls, parent);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

