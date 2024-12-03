/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.util.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.osgi.framework.Bundle;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

public abstract class ClassUtils {
    public static final List<ClassLoader> knownNonOsgiLoaders;
    public static final Set<ClassLoader> knownNonOsgiLoadersSet;

    public static ClassLoader getFwkClassLoader() {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

                @Override
                public ClassLoader run() {
                    return Bundle.class.getClassLoader();
                }
            });
        }
        return Bundle.class.getClassLoader();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void addNonOsgiClassLoader(ClassLoader classLoader, List<ClassLoader> list, Map<ClassLoader, Boolean> map) {
        while (classLoader != null) {
            List<ClassLoader> list2 = list;
            synchronized (list2) {
                if (!map.containsKey(classLoader)) {
                    list.add(classLoader);
                    map.put(classLoader, Boolean.TRUE);
                }
            }
            classLoader = classLoader.getParent();
        }
    }

    public static Class<?>[] getClassHierarchy(Class<?> clazz, ClassSet inclusion) {
        Class[] classes = null;
        if (clazz != null) {
            LinkedHashSet composingClasses = new LinkedHashSet();
            boolean includeClasses = inclusion.equals((Object)ClassSet.CLASS_HIERARCHY) || inclusion.equals((Object)ClassSet.ALL_CLASSES);
            boolean includeInterfaces = inclusion.equals((Object)ClassSet.INTERFACES) || inclusion.equals((Object)ClassSet.ALL_CLASSES);
            Class<?> clz = clazz;
            do {
                if (includeClasses) {
                    composingClasses.add(clz);
                }
                if (!includeInterfaces) continue;
                CollectionUtils.mergeArrayIntoCollection(ClassUtils.getAllInterfaces(clz), composingClasses);
            } while ((clz = clz.getSuperclass()) != null && clz != Object.class);
            classes = composingClasses.toArray(new Class[composingClasses.size()]);
        }
        return classes == null ? new Class[]{} : classes;
    }

    public static Class<?>[] getVisibleClassHierarchy(Class<?> clazz, ClassSet inclusion, ClassLoader loader) {
        if (clazz == null) {
            return new Class[0];
        }
        return ClassUtils.getVisibleClasses(ClassUtils.getClassHierarchy(clazz, inclusion), ClassUtils.getClassLoader(clazz));
    }

    public static Class<?>[] getVisibleClassHierarchy(Class<?> clazz, ClassSet inclusion, Bundle bundle) {
        return ClassUtils.getVisibleClasses(ClassUtils.getClassHierarchy(clazz, inclusion), bundle);
    }

    public static Class<?>[] getVisibleClasses(Class<?>[] classes, ClassLoader classLoader) {
        return ClassUtils.getVisibleClasses(classes, new ClassLoaderBridge(classLoader));
    }

    public static Class<?>[] getVisibleClasses(Class<?>[] classes, Bundle bundle) {
        return ClassUtils.getVisibleClasses(classes, new ClassLoaderBridge(bundle));
    }

    private static Class<?>[] getVisibleClasses(Class<?>[] classes, ClassLoaderBridge loader) {
        if (ObjectUtils.isEmpty((Object[])classes)) {
            return classes;
        }
        LinkedHashSet classSet = new LinkedHashSet(classes.length);
        CollectionUtils.mergeArrayIntoCollection(classes, classSet);
        Iterator iter = classSet.iterator();
        while (iter.hasNext()) {
            Class clzz = (Class)iter.next();
            if (loader.canSee(clzz.getName())) continue;
            iter.remove();
        }
        return classSet.toArray(new Class[classSet.size()]);
    }

    public static Class<?>[] getAllInterfaces(Class<?> clazz) {
        Assert.notNull(clazz);
        return ClassUtils.getAllInterfaces(clazz, new LinkedHashSet(8));
    }

    private static Class<?>[] getAllInterfaces(Class<?> clazz, Set<Class<?>> interfaces) {
        Class<?>[] intfs = clazz.getInterfaces();
        CollectionUtils.mergeArrayIntoCollection(intfs, interfaces);
        for (int i = 0; i < intfs.length; ++i) {
            ClassUtils.getAllInterfaces(intfs[i], interfaces);
        }
        return interfaces.toArray(new Class[interfaces.size()]);
    }

    public static boolean isPresent(String className, Bundle bundle) {
        Assert.hasText((String)className);
        Assert.notNull((Object)bundle);
        try {
            bundle.loadClass(className);
            return true;
        }
        catch (Exception cnfe) {
            return false;
        }
    }

    public static ClassLoader getClassLoader(Class<?> clazz) {
        Assert.notNull(clazz);
        ClassLoader loader = clazz.getClassLoader();
        return loader == null ? ClassLoader.getSystemClassLoader() : loader;
    }

    public static String[] toStringArray(Class<?>[] array) {
        if (ObjectUtils.isEmpty((Object[])array)) {
            return new String[0];
        }
        String[] strings = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            strings[i] = array[i].getName();
        }
        return strings;
    }

    public static boolean containsUnrelatedClasses(Class<?>[] classes) {
        if (ObjectUtils.isEmpty((Object[])classes)) {
            return false;
        }
        Class<?> clazz = null;
        for (int i = 0; i < classes.length; ++i) {
            if (classes[i].isInterface()) continue;
            if (clazz == null) {
                clazz = classes[i];
                continue;
            }
            if (clazz.isAssignableFrom(classes[i])) {
                clazz = classes[i];
                continue;
            }
            if (classes[i].isAssignableFrom(clazz)) continue;
            return true;
        }
        return false;
    }

    public static Class<?>[] removeParents(Class<?>[] classes) {
        boolean dirty;
        if (ObjectUtils.isEmpty((Object[])classes)) {
            return new Class[0];
        }
        ArrayList clazz = new ArrayList(classes.length);
        for (int i = 0; i < classes.length; ++i) {
            clazz.add(classes[i]);
        }
        while (clazz.remove(null)) {
        }
        do {
            dirty = false;
            block3: for (int i = 0; i < clazz.size(); ++i) {
                Class currentClass = (Class)clazz.get(i);
                for (int j = 0; j < clazz.size(); ++j) {
                    if (i == j || !currentClass.isAssignableFrom((Class)clazz.get(j))) continue;
                    clazz.remove(i);
                    --i;
                    dirty = true;
                    continue block3;
                }
            }
        } while (dirty);
        return clazz.toArray(new Class[clazz.size()]);
    }

    public static void configureFactoryForClass(ProxyFactory factory, Class<?>[] classes) {
        if (ObjectUtils.isEmpty((Object[])classes)) {
            return;
        }
        for (int i = 0; i < classes.length; ++i) {
            Class<?> clazz = classes[i];
            if (clazz.isInterface()) {
                factory.addInterface(clazz);
                continue;
            }
            factory.setTargetClass(clazz);
            factory.setProxyTargetClass(true);
        }
    }

    public static Class<?>[] loadClassesIfPossible(String[] classNames, ClassLoader classLoader) {
        if (ObjectUtils.isEmpty((Object[])classNames)) {
            return new Class[0];
        }
        Assert.notNull((Object)classLoader, (String)"classLoader is required");
        LinkedHashSet classes = new LinkedHashSet(classNames.length);
        for (int i = 0; i < classNames.length; ++i) {
            try {
                classes.add(classLoader.loadClass(classNames[i]));
                continue;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    public static Class<?>[] loadClasses(String[] classNames, ClassLoader classLoader) {
        if (ObjectUtils.isEmpty((Object[])classNames)) {
            return new Class[0];
        }
        Assert.notNull((Object)classLoader, (String)"classLoader is required");
        LinkedHashSet<Class> classes = new LinkedHashSet<Class>(classNames.length);
        for (int i = 0; i < classNames.length; ++i) {
            classes.add(org.springframework.util.ClassUtils.resolveClassName((String)classNames[i], (ClassLoader)classLoader));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    public static Class<?>[] excludeClassesWithModifier(Class<?>[] classes, int modifier) {
        if (ObjectUtils.isEmpty((Object[])classes)) {
            return new Class[0];
        }
        LinkedHashSet clazzes = new LinkedHashSet(classes.length);
        for (int i = 0; i < classes.length; ++i) {
            if ((modifier & classes[i].getModifiers()) != 0) continue;
            clazzes.add(classes[i]);
        }
        return clazzes.toArray(new Class[clazzes.size()]);
    }

    public static Class<?> getParticularClass(Class<?>[] classes) {
        boolean hasSecurity = System.getSecurityManager() != null;
        for (int i = 0; i < classes.length; ++i) {
            final Class<?> clazz = classes[i];
            ClassLoader loader = null;
            loader = hasSecurity ? AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

                @Override
                public ClassLoader run() {
                    return clazz.getClassLoader();
                }
            }) : clazz.getClassLoader();
            if (loader == null || knownNonOsgiLoadersSet.contains(loader)) continue;
            return clazz;
        }
        return ObjectUtils.isEmpty((Object[])classes) ? null : classes[0];
    }

    static {
        final ConcurrentHashMap<ClassLoader, Boolean> lookupMap = new ConcurrentHashMap<ClassLoader, Boolean>(8);
        final List<ClassLoader> lookupList = Collections.synchronizedList(new ArrayList());
        final ClassLoader classLoader = ClassUtils.getFwkClassLoader();
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    ClassUtils.addNonOsgiClassLoader(classLoader, lookupList, lookupMap);
                    ClassLoader sysLoader = ClassLoader.getSystemClassLoader();
                    ClassUtils.addNonOsgiClassLoader(sysLoader, lookupList, lookupMap);
                    return null;
                }
            });
        } else {
            ClassUtils.addNonOsgiClassLoader(classLoader, lookupList, lookupMap);
            ClassLoader sysLoader = ClassLoader.getSystemClassLoader();
            ClassUtils.addNonOsgiClassLoader(sysLoader, lookupList, lookupMap);
        }
        knownNonOsgiLoaders = Collections.unmodifiableList(lookupList);
        knownNonOsgiLoadersSet = new ReadOnlySetFromMap<ClassLoader>(lookupMap);
    }

    private static class ClassLoaderBridge {
        private final Bundle bundle;
        private final ClassLoader classLoader;

        public ClassLoaderBridge(Bundle bundle) {
            Assert.notNull((Object)bundle);
            this.bundle = bundle;
            this.classLoader = null;
        }

        public ClassLoaderBridge(ClassLoader classLoader) {
            Assert.notNull((Object)classLoader);
            this.classLoader = classLoader;
            this.bundle = null;
        }

        public Class<?> loadClass(String className) throws ClassNotFoundException {
            return this.bundle == null ? this.classLoader.loadClass(className) : this.bundle.loadClass(className);
        }

        public boolean canSee(String className) {
            return this.bundle == null ? org.springframework.util.ClassUtils.isPresent((String)className, (ClassLoader)this.classLoader) : ClassUtils.isPresent(className, this.bundle);
        }
    }

    public static enum ClassSet {
        INTERFACES,
        CLASS_HIERARCHY,
        ALL_CLASSES;

    }

    private static class ReadOnlySetFromMap<E>
    implements Set<E> {
        private final Set<E> keys;

        public ReadOnlySetFromMap(Map<E, ?> lookupMap) {
            this.keys = lookupMap.keySet();
        }

        @Override
        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            return this.keys.contains(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.keys.containsAll(c);
        }

        @Override
        public boolean isEmpty() {
            return this.keys.isEmpty();
        }

        @Override
        public Iterator<E> iterator() {
            return this.keys.iterator();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return this.keys.size();
        }

        @Override
        public Object[] toArray() {
            return this.keys.toArray();
        }

        @Override
        public <T> T[] toArray(T[] array) {
            return this.keys.toArray(array);
        }

        public String toString() {
            return this.keys.toString();
        }

        @Override
        public int hashCode() {
            return this.keys.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o == this || this.keys.equals(o);
        }
    }
}

