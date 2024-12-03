/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.common;

import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SystemProperties;

public class SystemCache {
    private static SystemCache INSTANCE = SystemCache.initCache();
    private ThreadLocal<SoftReference> tl_saxLoaders = new ThreadLocal();

    public static synchronized void set(SystemCache instance) {
        INSTANCE = instance;
    }

    public static synchronized SystemCache get() {
        return INSTANCE;
    }

    public SchemaTypeLoader getFromTypeLoaderCache(ClassLoader cl) {
        return null;
    }

    public void addToTypeLoaderCache(SchemaTypeLoader stl, ClassLoader cl) {
    }

    public void clearThreadLocals() {
        this.tl_saxLoaders.remove();
    }

    public Object getSaxLoader() {
        SoftReference s = this.tl_saxLoaders.get();
        return s == null ? null : s.get();
    }

    public void setSaxLoader(Object saxLoader) {
        this.tl_saxLoaders.set(new SoftReference<Object>(saxLoader));
    }

    private static SystemCache initCache() {
        String cacheClass = SystemProperties.getProperty("xmlbean.systemcacheimpl");
        if (cacheClass == null) {
            return new SystemCache();
        }
        String errPrefix = "Could not instantiate class " + cacheClass + " as specified by \"xmlbean.systemcacheimpl\". ";
        try {
            return (SystemCache)Class.forName(cacheClass).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassCastException cce) {
            throw new ClassCastException(errPrefix + "Class does not derive from SystemCache.");
        }
        catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(errPrefix + "Class was not found.", cnfe);
        }
        catch (InstantiationException | NoSuchMethodException | InvocationTargetException ie) {
            throw new RuntimeException(errPrefix + "An empty constructor may be missing.", ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException(errPrefix + "A public empty constructor may be missing.", iae);
        }
    }
}

