/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.aspectj.apache.bcel.classfile.ClassParser;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.util.ClassLoaderReference;
import org.aspectj.apache.bcel.util.DefaultClassLoaderReference;
import org.aspectj.apache.bcel.util.Repository;

public class ClassLoaderRepository
implements Repository {
    private static ClassLoader bootClassLoader = null;
    private ClassLoaderReference loaderRef;
    private WeakHashMap<URL, SoftReference<JavaClass>> localCache = new WeakHashMap();
    private static SoftHashMap sharedCache = new SoftHashMap(Collections.synchronizedMap(new HashMap()));
    private SoftHashMap nameMap = new SoftHashMap(new HashMap(), false);
    public static boolean useSharedCache = System.getProperty("org.aspectj.apache.bcel.useSharedCache", "true").equalsIgnoreCase("true");
    private static int cacheHitsShared = 0;
    private static int missSharedEvicted = 0;
    private long timeManipulatingURLs = 0L;
    private long timeSpentLoading = 0L;
    private int classesLoadedCount = 0;
    private int misses = 0;
    private int cacheHitsLocal = 0;
    private int missLocalEvicted = 0;

    public ClassLoaderRepository(ClassLoader loader) {
        this.loaderRef = new DefaultClassLoaderReference(loader != null ? loader : ClassLoaderRepository.getBootClassLoader());
    }

    public ClassLoaderRepository(ClassLoaderReference loaderRef) {
        this.loaderRef = loaderRef;
    }

    private static synchronized ClassLoader getBootClassLoader() {
        if (bootClassLoader == null) {
            bootClassLoader = new URLClassLoader(new URL[0]);
        }
        return bootClassLoader;
    }

    private void storeClassAsReference(URL url, JavaClass clazz) {
        if (useSharedCache) {
            clazz.setRepository(null);
            sharedCache.put(url, clazz);
        } else {
            clazz.setRepository(this);
            this.localCache.put(url, new SoftReference<JavaClass>(clazz));
        }
    }

    @Override
    public void storeClass(JavaClass clazz) {
        this.storeClassAsReference(this.toURL(clazz.getClassName()), clazz);
    }

    @Override
    public void removeClass(JavaClass clazz) {
        if (useSharedCache) {
            sharedCache.remove(this.toURL(clazz.getClassName()));
        } else {
            this.localCache.remove(this.toURL(clazz.getClassName()));
        }
    }

    @Override
    public JavaClass findClass(String className) {
        if (useSharedCache) {
            return this.findClassShared(this.toURL(className));
        }
        return this.findClassLocal(this.toURL(className));
    }

    private JavaClass findClassLocal(URL url) {
        SoftReference<JavaClass> o = this.localCache.get(url);
        if (o != null) {
            if ((o = ((Reference)o).get()) != null) {
                return (JavaClass)((Object)o);
            }
            ++this.missLocalEvicted;
        }
        return null;
    }

    private JavaClass findClassShared(URL url) {
        return (JavaClass)sharedCache.get(url);
    }

    private URL toURL(String className) {
        URL url = (URL)this.nameMap.get(className);
        if (url == null) {
            String classFile = className.replace('.', '/');
            url = this.loaderRef.getClassLoader().getResource(classFile + ".class");
            this.nameMap.put(className, url);
        }
        return url;
    }

    @Override
    public JavaClass loadClass(String className) throws ClassNotFoundException {
        long time = System.currentTimeMillis();
        URL url = this.toURL(className);
        this.timeManipulatingURLs += System.currentTimeMillis() - time;
        if (url == null) {
            throw new ClassNotFoundException(className + " not found - unable to determine URL");
        }
        JavaClass clazz = null;
        if (useSharedCache) {
            clazz = this.findClassShared(url);
            if (clazz != null) {
                ++cacheHitsShared;
                return clazz;
            }
        } else {
            clazz = this.findClassLocal(url);
            if (clazz != null) {
                ++this.cacheHitsLocal;
                return clazz;
            }
        }
        ++this.misses;
        try {
            InputStream is;
            String classFile = className.replace('.', '/');
            InputStream inputStream = is = useSharedCache ? url.openStream() : this.loaderRef.getClassLoader().getResourceAsStream(classFile + ".class");
            if (is == null) {
                throw new ClassNotFoundException(className + " not found using url " + url);
            }
            ClassParser parser = new ClassParser(is, className);
            clazz = parser.parse();
            this.storeClassAsReference(url, clazz);
            this.timeSpentLoading += System.currentTimeMillis() - time;
            ++this.classesLoadedCount;
            return clazz;
        }
        catch (IOException e) {
            throw new ClassNotFoundException(e.toString());
        }
    }

    public String report() {
        StringBuffer sb = new StringBuffer();
        sb.append("BCEL repository report.");
        if (useSharedCache) {
            sb.append(" (shared cache)");
        } else {
            sb.append(" (local cache)");
        }
        sb.append(" Total time spent loading: " + this.timeSpentLoading + "ms.");
        sb.append(" Time spent manipulating URLs: " + this.timeManipulatingURLs + "ms.");
        sb.append(" Classes loaded: " + this.classesLoadedCount + ".");
        if (useSharedCache) {
            sb.append(" Shared cache size: " + sharedCache.size());
            sb.append(" Shared cache (hits/missDueToEviction): (" + cacheHitsShared + "/" + missSharedEvicted + ").");
        } else {
            sb.append(" Local cache size: " + this.localCache.size());
            sb.append(" Local cache (hits/missDueToEviction): (" + this.cacheHitsLocal + "/" + this.missLocalEvicted + ").");
        }
        return sb.toString();
    }

    public long[] reportStats() {
        return new long[]{this.timeSpentLoading, this.timeManipulatingURLs, this.classesLoadedCount, cacheHitsShared, missSharedEvicted, this.cacheHitsLocal, this.missLocalEvicted, sharedCache.size()};
    }

    public void reset() {
        this.timeManipulatingURLs = 0L;
        this.timeSpentLoading = 0L;
        this.classesLoadedCount = 0;
        this.cacheHitsLocal = 0;
        cacheHitsShared = 0;
        missSharedEvicted = 0;
        this.missLocalEvicted = 0;
        this.misses = 0;
        this.clear();
    }

    @Override
    public JavaClass loadClass(Class clazz) throws ClassNotFoundException {
        return this.loadClass(clazz.getName());
    }

    @Override
    public void clear() {
        if (useSharedCache) {
            sharedCache.clear();
        } else {
            this.localCache.clear();
        }
    }

    public static class SoftHashMap
    extends AbstractMap {
        private Map<Object, SpecialValue> map;
        boolean recordMiss = true;
        private ReferenceQueue rq = new ReferenceQueue();

        public SoftHashMap(Map<Object, SpecialValue> map) {
            this.map = map;
        }

        public SoftHashMap() {
            this(new HashMap<Object, SpecialValue>());
        }

        public SoftHashMap(Map map, boolean b) {
            this(map);
            this.recordMiss = b;
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
                if (this.recordMiss) {
                    missSharedEvicted++;
                }
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
            this.map.clear();
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

