/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.spi;

import com.sun.xml.ws.resources.ContextClassloaderLocalMessages;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.WeakHashMap;

abstract class ContextClassloaderLocal<V> {
    private WeakHashMap<ClassLoader, V> CACHE = new WeakHashMap();

    ContextClassloaderLocal() {
    }

    public V get() throws Error {
        ClassLoader tccl = ContextClassloaderLocal.getContextClassLoader();
        V instance = this.CACHE.get(tccl);
        if (instance == null) {
            instance = this.createNewInstance();
            this.CACHE.put(tccl, instance);
        }
        return instance;
    }

    public void set(V instance) {
        this.CACHE.put(ContextClassloaderLocal.getContextClassLoader(), instance);
    }

    protected abstract V initialValue() throws Exception;

    private V createNewInstance() {
        try {
            return this.initialValue();
        }
        catch (Exception e) {
            throw new Error(ContextClassloaderLocalMessages.FAILED_TO_CREATE_NEW_INSTANCE(this.getClass().getName()), e);
        }
    }

    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
                return cl;
            }
        });
    }
}

