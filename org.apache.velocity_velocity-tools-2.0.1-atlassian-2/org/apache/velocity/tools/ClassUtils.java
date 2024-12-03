/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.util.ArrayIterator
 *  org.apache.velocity.util.EnumerationIterator
 */
package org.apache.velocity.tools;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.util.ArrayIterator;
import org.apache.velocity.util.EnumerationIterator;

public class ClassUtils {
    public static final ClassUtils INSTANCE = new ClassUtils();

    private ClassUtils() {
    }

    public ClassUtils getInstance() {
        return INSTANCE;
    }

    private static final ClassLoader getThreadContextLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private static final ClassLoader getClassLoader() {
        return ClassUtils.class.getClassLoader();
    }

    private static final ClassLoader getCallerLoader(Object caller) {
        if (caller instanceof Class) {
            return ((Class)caller).getClassLoader();
        }
        return caller.getClass().getClassLoader();
    }

    public static Class getClass(String name) throws ClassNotFoundException {
        try {
            return ClassUtils.getThreadContextLoader().loadClass(name);
        }
        catch (ClassNotFoundException e) {
            try {
                return Class.forName(name);
            }
            catch (ClassNotFoundException ex) {
                return ClassUtils.getClassLoader().loadClass(name);
            }
        }
    }

    public static Object getInstance(String classname) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return ClassUtils.getClass(classname).newInstance();
    }

    public static List<URL> getResources(String name, Object caller) {
        LinkedHashSet<String> urls = new LinkedHashSet<String>();
        ClassUtils.addResources(name, urls, ClassUtils.getThreadContextLoader());
        if (!ClassUtils.addResources(name, urls, ClassUtils.getClassLoader())) {
            ClassUtils.addResource(name, urls, ClassUtils.class);
        }
        if (!ClassUtils.addResources(name, urls, ClassUtils.getCallerLoader(caller))) {
            ClassUtils.addResource(name, urls, caller.getClass());
        }
        if (!urls.isEmpty()) {
            ArrayList<URL> result = new ArrayList<URL>(urls.size());
            try {
                for (String url : urls) {
                    result.add(new URL(url));
                }
            }
            catch (MalformedURLException mue) {
                throw new IllegalStateException("A URL could not be recreated from its own toString() form", mue);
            }
            return result;
        }
        if (!name.startsWith("/")) {
            return ClassUtils.getResources("/" + name, caller);
        }
        return Collections.emptyList();
    }

    private static final void addResource(String name, Set<String> urls, Class c) {
        URL url = c.getResource(name);
        if (url != null) {
            urls.add(url.toString());
        }
    }

    private static final boolean addResources(String name, Set<String> urls, ClassLoader loader) {
        boolean foundSome = false;
        try {
            Enumeration<URL> e = loader.getResources(name);
            while (e.hasMoreElements()) {
                urls.add(e.nextElement().toString());
                foundSome = true;
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return foundSome;
    }

    public static URL getResource(String name, Object caller) {
        URL url = ClassUtils.getThreadContextLoader().getResource(name);
        if (url == null && (url = ClassUtils.getClassLoader().getResource(name)) == null && (url = ClassUtils.class.getResource(name)) == null && caller != null) {
            Class callingClass = caller.getClass();
            if (callingClass == Class.class) {
                callingClass = (Class)caller;
            }
            url = callingClass.getResource(name);
        }
        return url;
    }

    public static InputStream getResourceAsStream(String name, Object caller) {
        URL url = ClassUtils.getResource(name, caller);
        try {
            return url == null ? null : url.openStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    public static Method findMethod(Class clazz, String name, Class[] params) throws SecurityException {
        try {
            return clazz.getMethod(name, params);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return ClassUtils.findDeclaredMethod(clazz, name, params);
        }
    }

    public static Method findDeclaredMethod(Class clazz, String name, Class[] params) throws SecurityException {
        try {
            Method method = clazz.getDeclaredMethod(name, params);
            if (method != null) {
                method.setAccessible(true);
                return method;
            }
        }
        catch (NoSuchMethodException method) {
            // empty catch block
        }
        Class supclazz = clazz.getSuperclass();
        if (supclazz != null) {
            return ClassUtils.findDeclaredMethod(supclazz, name, params);
        }
        return null;
    }

    public static Object getFieldValue(String fieldPath) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalAccessException {
        int lastDot = fieldPath.lastIndexOf(46);
        String classname = fieldPath.substring(0, lastDot);
        String fieldname = fieldPath.substring(lastDot + 1, fieldPath.length());
        Class clazz = ClassUtils.getClass(classname);
        return ClassUtils.getFieldValue(clazz, fieldname);
    }

    public static Object getFieldValue(Class clazz, String fieldname) throws NoSuchFieldException, SecurityException, IllegalAccessException {
        Field field = clazz.getField(fieldname);
        int mod = field.getModifiers();
        if (!Modifier.isStatic(mod)) {
            throw new UnsupportedOperationException("Field " + fieldname + " in class " + clazz.getName() + " is not static.  Only static fields are supported.");
        }
        return field.get(null);
    }

    public static Iterator getIterator(Object obj) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (obj.getClass().isArray()) {
            return new ArrayIterator(obj);
        }
        if (obj instanceof Collection) {
            return ((Collection)obj).iterator();
        }
        if (obj instanceof Map) {
            return ((Map)obj).values().iterator();
        }
        if (obj instanceof Iterator) {
            return (Iterator)obj;
        }
        if (obj instanceof Iterable) {
            return ((Iterable)obj).iterator();
        }
        if (obj instanceof Enumeration) {
            return new EnumerationIterator((Enumeration)obj);
        }
        Method iter = obj.getClass().getMethod("iterator", new Class[0]);
        if (Iterator.class.isAssignableFrom(iter.getReturnType())) {
            return (Iterator)iter.invoke(obj, new Object[0]);
        }
        return null;
    }
}

