/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm.$ClassReader;
import com.google.inject.internal.cglib.core.$ClassGenerator;
import com.google.inject.internal.cglib.core.$ClassNameReader;
import com.google.inject.internal.cglib.core.$CodeGenerationException;
import com.google.inject.internal.cglib.core.$DefaultGeneratorStrategy;
import com.google.inject.internal.cglib.core.$DefaultNamingPolicy;
import com.google.inject.internal.cglib.core.$GeneratorStrategy;
import com.google.inject.internal.cglib.core.$NamingPolicy;
import com.google.inject.internal.cglib.core.$Predicate;
import com.google.inject.internal.cglib.core.$ReflectUtils;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class $AbstractClassGenerator
implements $ClassGenerator {
    private static final Object NAME_KEY = new Object();
    private static final ThreadLocal CURRENT = new ThreadLocal();
    private $GeneratorStrategy strategy = $DefaultGeneratorStrategy.INSTANCE;
    private $NamingPolicy namingPolicy = $DefaultNamingPolicy.INSTANCE;
    private Source source;
    private ClassLoader classLoader;
    private String namePrefix;
    private Object key;
    private boolean useCache = true;
    private String className;
    private boolean attemptLoad;

    protected $AbstractClassGenerator(Source source) {
        this.source = source;
    }

    protected void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    protected final String getClassName() {
        if (this.className == null) {
            this.className = this.getClassName(this.getClassLoader());
        }
        return this.className;
    }

    private String getClassName(ClassLoader loader) {
        final Set nameCache = this.getClassNameCache(loader);
        return this.namingPolicy.getClassName(this.namePrefix, this.source.name, this.key, new $Predicate(){

            public boolean evaluate(Object arg) {
                return nameCache.contains(arg);
            }
        });
    }

    private Set getClassNameCache(ClassLoader loader) {
        return (Set)((Map)this.source.cache.get(loader)).get(NAME_KEY);
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setNamingPolicy($NamingPolicy namingPolicy) {
        if (namingPolicy == null) {
            namingPolicy = $DefaultNamingPolicy.INSTANCE;
        }
        this.namingPolicy = namingPolicy;
    }

    public $NamingPolicy getNamingPolicy() {
        return this.namingPolicy;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean getUseCache() {
        return this.useCache;
    }

    public void setAttemptLoad(boolean attemptLoad) {
        this.attemptLoad = attemptLoad;
    }

    public boolean getAttemptLoad() {
        return this.attemptLoad;
    }

    public void setStrategy($GeneratorStrategy strategy) {
        if (strategy == null) {
            strategy = $DefaultGeneratorStrategy.INSTANCE;
        }
        this.strategy = strategy;
    }

    public $GeneratorStrategy getStrategy() {
        return this.strategy;
    }

    public static $AbstractClassGenerator getCurrent() {
        return ($AbstractClassGenerator)CURRENT.get();
    }

    public ClassLoader getClassLoader() {
        ClassLoader t = this.classLoader;
        if (t == null) {
            t = this.getDefaultClassLoader();
        }
        if (t == null) {
            t = this.getClass().getClassLoader();
        }
        if (t == null) {
            t = Thread.currentThread().getContextClassLoader();
        }
        if (t == null) {
            throw new IllegalStateException("Cannot determine classloader");
        }
        return t;
    }

    protected abstract ClassLoader getDefaultClassLoader();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected Object create(Object key) {
        try {
            Class gen = null;
            Source source = this.source;
            synchronized (source) {
                ClassLoader loader = this.getClassLoader();
                HashMap<Object, Object> cache2 = null;
                cache2 = (HashMap<Object, Object>)this.source.cache.get(loader);
                if (cache2 == null) {
                    cache2 = new HashMap<Object, Object>();
                    cache2.put(NAME_KEY, new HashSet());
                    this.source.cache.put(loader, cache2);
                } else if (this.useCache) {
                    Reference ref = (Reference)cache2.get(key);
                    gen = ref == null ? null : ref.get();
                }
                if (gen != null) {
                    return this.firstInstance(gen);
                }
                Object save = CURRENT.get();
                CURRENT.set(this);
                try {
                    this.key = key;
                    if (this.attemptLoad) {
                        try {
                            gen = loader.loadClass(this.getClassName());
                        }
                        catch (ClassNotFoundException e) {
                            // empty catch block
                        }
                    }
                    if (gen == null) {
                        byte[] b = this.strategy.generate(this);
                        String className = $ClassNameReader.getClassName(new $ClassReader(b));
                        this.getClassNameCache(loader).add(className);
                        gen = $ReflectUtils.defineClass(className, b, loader);
                    }
                    if (this.useCache) {
                        cache2.put(key, new WeakReference(gen));
                    }
                    Object object = this.firstInstance(gen);
                    return object;
                }
                finally {
                    CURRENT.set(save);
                }
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Error e) {
            throw e;
        }
        catch (Exception e) {
            throw new $CodeGenerationException(e);
        }
    }

    protected abstract Object firstInstance(Class var1) throws Exception;

    protected abstract Object nextInstance(Object var1) throws Exception;

    protected static class Source {
        String name;
        Map cache = new WeakHashMap();

        public Source(String name) {
            this.name = name;
        }
    }
}

