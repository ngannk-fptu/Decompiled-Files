/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.jfree.util.ArrayUtilities;
import org.jfree.util.Log;
import org.jfree.util.PublicCloneable;

public final class ObjectUtilities {
    public static final String THREAD_CONTEXT = "ThreadContext";
    public static final String CLASS_CONTEXT = "ClassContext";
    private static String classLoaderSource = "ThreadContext";
    private static ClassLoader classLoader;
    static /* synthetic */ Class class$org$jfree$util$ObjectUtilities;

    private ObjectUtilities() {
    }

    public static String getClassLoaderSource() {
        return classLoaderSource;
    }

    public static void setClassLoaderSource(String classLoaderSource) {
        ObjectUtilities.classLoaderSource = classLoaderSource;
    }

    public static boolean equal(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 != null) {
            return o1.equals(o2);
        }
        return false;
    }

    public static int hashCode(Object object) {
        int result = 0;
        if (object != null) {
            result = object.hashCode();
        }
        return result;
    }

    public static Object clone(Object object) throws CloneNotSupportedException {
        if (object == null) {
            throw new IllegalArgumentException("Null 'object' argument.");
        }
        if (object instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable)object;
            return pc.clone();
        }
        try {
            Method method = object.getClass().getMethod("clone", null);
            if (Modifier.isPublic(method.getModifiers())) {
                return method.invoke(object, (Object[])null);
            }
        }
        catch (NoSuchMethodException e) {
            Log.warn("Object without clone() method is impossible.");
        }
        catch (IllegalAccessException e) {
            Log.warn("Object.clone(): unable to call method.");
        }
        catch (InvocationTargetException e) {
            Log.warn("Object without clone() method is impossible.");
        }
        throw new CloneNotSupportedException("Failed to clone.");
    }

    public static Collection deepClone(Collection collection) throws CloneNotSupportedException {
        if (collection == null) {
            throw new IllegalArgumentException("Null 'collection' argument.");
        }
        Collection result = (Collection)ObjectUtilities.clone(collection);
        result.clear();
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            Object item = iterator.next();
            if (item != null) {
                result.add(ObjectUtilities.clone(item));
                continue;
            }
            result.add(null);
        }
        return result;
    }

    public static synchronized void setClassLoader(ClassLoader classLoader) {
        ObjectUtilities.classLoader = classLoader;
    }

    public static ClassLoader getClassLoader() {
        return classLoader;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ClassLoader getClassLoader(Class c) {
        ClassLoader threadLoader;
        String localClassLoaderSource;
        Class clazz = class$org$jfree$util$ObjectUtilities == null ? (class$org$jfree$util$ObjectUtilities = ObjectUtilities.class$("org.jfree.util.ObjectUtilities")) : class$org$jfree$util$ObjectUtilities;
        synchronized (clazz) {
            if (classLoader != null) {
                return classLoader;
            }
            localClassLoaderSource = classLoaderSource;
        }
        if (THREAD_CONTEXT.equals(localClassLoaderSource) && (threadLoader = Thread.currentThread().getContextClassLoader()) != null) {
            return threadLoader;
        }
        ClassLoader applicationCL = c.getClassLoader();
        if (applicationCL == null) {
            return ClassLoader.getSystemClassLoader();
        }
        return applicationCL;
    }

    public static URL getResource(String name, Class c) {
        ClassLoader cl = ObjectUtilities.getClassLoader(c);
        if (cl == null) {
            return null;
        }
        return cl.getResource(name);
    }

    public static URL getResourceRelative(String name, Class c) {
        ClassLoader cl = ObjectUtilities.getClassLoader(c);
        String cname = ObjectUtilities.convertName(name, c);
        if (cl == null) {
            return null;
        }
        return cl.getResource(cname);
    }

    private static String convertName(String name, Class c) {
        if (name.startsWith("/")) {
            return name.substring(1);
        }
        while (c.isArray()) {
            c = c.getComponentType();
        }
        String baseName = c.getName();
        int index = baseName.lastIndexOf(46);
        if (index == -1) {
            return name;
        }
        String pkgName = baseName.substring(0, index);
        return pkgName.replace('.', '/') + "/" + name;
    }

    public static InputStream getResourceAsStream(String name, Class context) {
        URL url = ObjectUtilities.getResource(name, context);
        if (url == null) {
            return null;
        }
        try {
            return url.openStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    public static InputStream getResourceRelativeAsStream(String name, Class context) {
        URL url = ObjectUtilities.getResourceRelative(name, context);
        if (url == null) {
            return null;
        }
        try {
            return url.openStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    public static Object loadAndInstantiate(String className, Class source) {
        try {
            ClassLoader loader = ObjectUtilities.getClassLoader(source);
            Class<?> c = loader.loadClass(className);
            return c.newInstance();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Object loadAndInstantiate(String className, Class source, Class type) {
        try {
            ClassLoader loader = ObjectUtilities.getClassLoader(source);
            Class<?> c = loader.loadClass(className);
            if (type.isAssignableFrom(c)) {
                return c.newInstance();
            }
        }
        catch (Exception e) {
            return null;
        }
        return null;
    }

    public static boolean isJDK14() {
        try {
            ClassLoader loader = ObjectUtilities.getClassLoader(class$org$jfree$util$ObjectUtilities == null ? (class$org$jfree$util$ObjectUtilities = ObjectUtilities.class$("org.jfree.util.ObjectUtilities")) : class$org$jfree$util$ObjectUtilities);
            if (loader != null) {
                try {
                    loader.loadClass("java.util.RandomAccess");
                    return true;
                }
                catch (ClassNotFoundException e) {
                    return false;
                }
                catch (Exception e) {}
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        try {
            String version = System.getProperty("java.vm.specification.version");
            if (version == null) {
                return false;
            }
            String[] versions = ObjectUtilities.parseVersions(version);
            String[] target = new String[]{"1", "4"};
            return ArrayUtilities.compareVersionArrays((Comparable[])versions, (Comparable[])target) >= 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static String[] parseVersions(String version) {
        if (version == null) {
            return new String[0];
        }
        ArrayList<String> versions = new ArrayList<String>();
        StringTokenizer strtok = new StringTokenizer(version, ".");
        while (strtok.hasMoreTokens()) {
            versions.add(strtok.nextToken());
        }
        return versions.toArray(new String[versions.size()]);
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

