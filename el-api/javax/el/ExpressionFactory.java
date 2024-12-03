/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.MethodExpression;
import javax.el.Util;
import javax.el.ValueExpression;

public abstract class ExpressionFactory {
    private static final boolean IS_SECURITY_ENABLED = System.getSecurityManager() != null;
    private static final String PROPERTY_NAME = "javax.el.ExpressionFactory";
    private static final String PROPERTY_FILE;
    private static final CacheValue nullTcclFactory;
    private static final Map<CacheKey, CacheValue> factoryCache;

    public static ExpressionFactory newInstance() {
        return ExpressionFactory.newInstance(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ExpressionFactory newInstance(Properties properties) {
        Class<?> clazz;
        CacheValue cacheValue;
        ExpressionFactory result = null;
        ClassLoader tccl = Util.getContextClassLoader();
        if (tccl == null) {
            cacheValue = nullTcclFactory;
        } else {
            CacheValue newCacheValue;
            CacheKey key = new CacheKey(tccl);
            cacheValue = factoryCache.get(key);
            if (cacheValue == null && (cacheValue = factoryCache.putIfAbsent(key, newCacheValue = new CacheValue())) == null) {
                cacheValue = newCacheValue;
            }
        }
        Lock readLock = cacheValue.getLock().readLock();
        readLock.lock();
        try {
            clazz = cacheValue.getFactoryClass();
        }
        finally {
            readLock.unlock();
        }
        if (clazz == null) {
            String className = null;
            try {
                Lock writeLock = cacheValue.getLock().writeLock();
                writeLock.lock();
                try {
                    className = cacheValue.getFactoryClassName();
                    if (className == null) {
                        className = ExpressionFactory.discoverClassName(tccl);
                        cacheValue.setFactoryClassName(className);
                    }
                    clazz = tccl == null ? Class.forName(className) : tccl.loadClass(className);
                    cacheValue.setFactoryClass(clazz);
                }
                finally {
                    writeLock.unlock();
                }
            }
            catch (ClassNotFoundException e) {
                throw new ELException(Util.message(null, "expressionFactory.cannotFind", className), e);
            }
        }
        try {
            Constructor<?> constructor = null;
            if (properties != null) {
                try {
                    constructor = clazz.getConstructor(Properties.class);
                }
                catch (SecurityException se) {
                    throw new ELException(se);
                }
                catch (NoSuchMethodException se) {
                    // empty catch block
                }
            }
            result = constructor == null ? (ExpressionFactory)clazz.getConstructor(new Class[0]).newInstance(new Object[0]) : (ExpressionFactory)constructor.newInstance(properties);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            Util.handleThrowable(cause);
            throw new ELException(Util.message(null, "expressionFactory.cannotCreate", clazz.getName()), e);
        }
        catch (IllegalArgumentException | ReflectiveOperationException e) {
            throw new ELException(Util.message(null, "expressionFactory.cannotCreate", clazz.getName()), e);
        }
        return result;
    }

    public abstract ValueExpression createValueExpression(ELContext var1, String var2, Class<?> var3);

    public abstract ValueExpression createValueExpression(Object var1, Class<?> var2);

    public abstract MethodExpression createMethodExpression(ELContext var1, String var2, Class<?> var3, Class<?>[] var4);

    public abstract Object coerceToType(Object var1, Class<?> var2);

    public ELResolver getStreamELResolver() {
        return null;
    }

    public Map<String, Method> getInitFunctionMap() {
        return null;
    }

    private static String discoverClassName(ClassLoader tccl) {
        String className = null;
        className = ExpressionFactory.getClassNameServices(tccl);
        if (className == null) {
            className = IS_SECURITY_ENABLED ? AccessController.doPrivileged(ExpressionFactory::getClassNameJreDir) : ExpressionFactory.getClassNameJreDir();
        }
        if (className == null) {
            className = IS_SECURITY_ENABLED ? AccessController.doPrivileged(ExpressionFactory::getClassNameSysProp) : ExpressionFactory.getClassNameSysProp();
        }
        if (className == null) {
            className = "org.apache.el.ExpressionFactoryImpl";
        }
        return className;
    }

    private static String getClassNameServices(ClassLoader tccl) {
        Object result = null;
        ServiceLoader<ExpressionFactory> serviceLoader = ServiceLoader.load(ExpressionFactory.class, tccl);
        Iterator<ExpressionFactory> iter = serviceLoader.iterator();
        while (result == null && iter.hasNext()) {
            result = iter.next();
        }
        if (result == null) {
            return null;
        }
        return result.getClass().getName();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static String getClassNameJreDir() {
        File file = new File(PROPERTY_FILE);
        if (!file.canRead()) return null;
        try (FileInputStream is2222 = new FileInputStream(file);){
            Properties props = new Properties();
            props.load(is2222);
            String value = props.getProperty(PROPERTY_NAME);
            if (value == null) return null;
            if (value.trim().length() <= 0) return null;
            String string = value.trim();
            return string;
        }
        catch (FileNotFoundException is2222) {
            return null;
        }
        catch (IOException e) {
            throw new ELException(Util.message(null, "expressionFactory.readFailed", PROPERTY_FILE), e);
        }
    }

    private static String getClassNameSysProp() {
        String value = System.getProperty(PROPERTY_NAME);
        if (value != null && value.trim().length() > 0) {
            return value.trim();
        }
        return null;
    }

    static {
        nullTcclFactory = new CacheValue();
        factoryCache = new ConcurrentHashMap<CacheKey, CacheValue>();
        PROPERTY_FILE = IS_SECURITY_ENABLED ? AccessController.doPrivileged(() -> System.getProperty("java.home") + File.separator + "lib" + File.separator + "el.properties") : System.getProperty("java.home") + File.separator + "lib" + File.separator + "el.properties";
    }

    private static class CacheValue {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private String className;
        private WeakReference<Class<?>> ref;

        CacheValue() {
        }

        public ReadWriteLock getLock() {
            return this.lock;
        }

        public String getFactoryClassName() {
            return this.className;
        }

        public void setFactoryClassName(String className) {
            this.className = className;
        }

        public Class<?> getFactoryClass() {
            return this.ref != null ? (Class)this.ref.get() : null;
        }

        public void setFactoryClass(Class<?> clazz) {
            this.ref = new WeakReference(clazz);
        }
    }

    private static class CacheKey {
        private final int hash;
        private final WeakReference<ClassLoader> ref;

        CacheKey(ClassLoader cl) {
            this.hash = cl.hashCode();
            this.ref = new WeakReference<ClassLoader>(cl);
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CacheKey)) {
                return false;
            }
            ClassLoader thisCl = (ClassLoader)this.ref.get();
            if (thisCl == null) {
                return false;
            }
            return thisCl == ((CacheKey)obj).ref.get();
        }
    }
}

