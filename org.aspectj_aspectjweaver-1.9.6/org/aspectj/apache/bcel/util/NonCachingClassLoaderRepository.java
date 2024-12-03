/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.aspectj.apache.bcel.classfile.ClassParser;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.util.ClassLoaderReference;
import org.aspectj.apache.bcel.util.DefaultClassLoaderReference;
import org.aspectj.apache.bcel.util.Repository;

public class NonCachingClassLoaderRepository
implements Repository {
    private static ClassLoader bootClassLoader = null;
    private final ClassLoaderReference loaderRef;
    private final Map<String, JavaClass> loadedClasses = new SoftHashMap();

    public NonCachingClassLoaderRepository(ClassLoader loader) {
        this.loaderRef = new DefaultClassLoaderReference(loader != null ? loader : NonCachingClassLoaderRepository.getBootClassLoader());
    }

    public NonCachingClassLoaderRepository(ClassLoaderReference loaderRef) {
        this.loaderRef = loaderRef;
    }

    private static synchronized ClassLoader getBootClassLoader() {
        if (bootClassLoader == null) {
            bootClassLoader = new URLClassLoader(new URL[0]);
        }
        return bootClassLoader;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void storeClass(JavaClass clazz) {
        Map<String, JavaClass> map = this.loadedClasses;
        synchronized (map) {
            this.loadedClasses.put(clazz.getClassName(), clazz);
        }
        clazz.setRepository(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeClass(JavaClass clazz) {
        Map<String, JavaClass> map = this.loadedClasses;
        synchronized (map) {
            this.loadedClasses.remove(clazz.getClassName());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public JavaClass findClass(String className) {
        Map<String, JavaClass> map = this.loadedClasses;
        synchronized (map) {
            if (this.loadedClasses.containsKey(className)) {
                return this.loadedClasses.get(className);
            }
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        Map<String, JavaClass> map = this.loadedClasses;
        synchronized (map) {
            this.loadedClasses.clear();
        }
    }

    @Override
    public JavaClass loadClass(String className) throws ClassNotFoundException {
        JavaClass javaClass = this.findClass(className);
        if (javaClass != null) {
            return javaClass;
        }
        javaClass = this.loadJavaClass(className);
        this.storeClass(javaClass);
        return javaClass;
    }

    @Override
    public JavaClass loadClass(Class clazz) throws ClassNotFoundException {
        return this.loadClass(clazz.getName());
    }

    private JavaClass loadJavaClass(String className) throws ClassNotFoundException {
        String classFile = className.replace('.', '/');
        try {
            InputStream is = this.loaderRef.getClassLoader().getResourceAsStream(classFile + ".class");
            if (is == null) {
                throw new ClassNotFoundException(className + " not found.");
            }
            ClassParser parser = new ClassParser(is, className);
            return parser.parse();
        }
        catch (IOException e) {
            throw new ClassNotFoundException(e.toString());
        }
    }

    public static class SoftHashMap
    extends AbstractMap {
        private Map<Object, SpecialValue> map;
        private ReferenceQueue rq = new ReferenceQueue();

        public SoftHashMap(Map<Object, SpecialValue> map) {
            this.map = map;
        }

        public SoftHashMap() {
            this(new HashMap<Object, SpecialValue>());
        }

        public SoftHashMap(Map<Object, SpecialValue> map, boolean b) {
            this(map);
        }

        private void processQueue() {
            SpecialValue sv = null;
            while ((sv = (SpecialValue)this.rq.poll()) != null) {
                this.map.remove(sv.key);
            }
        }

        @Override
        public Object get(Object key) {
            SpecialValue value = this.map.get(key);
            if (value == null) {
                return null;
            }
            if (value.get() == null) {
                this.map.remove(value.key);
                return null;
            }
            return value.get();
        }

        @Override
        public Object put(Object k, Object v) {
            this.processQueue();
            return this.map.put(k, new SpecialValue(k, v));
        }

        @Override
        public Set entrySet() {
            return this.map.entrySet();
        }

        @Override
        public void clear() {
            this.processQueue();
            Set<Object> keys = this.map.keySet();
            for (Object name : keys) {
                this.map.remove(name);
            }
        }

        @Override
        public int size() {
            this.processQueue();
            return this.map.size();
        }

        @Override
        public Object remove(Object k) {
            this.processQueue();
            SpecialValue value = this.map.remove(k);
            if (value == null) {
                return null;
            }
            if (value.get() != null) {
                return value.get();
            }
            return null;
        }

        class SpecialValue
        extends SoftReference {
            private final Object key;

            SpecialValue(Object k, Object v) {
                super(v, SoftHashMap.this.rq);
                this.key = k;
            }
        }
    }
}

