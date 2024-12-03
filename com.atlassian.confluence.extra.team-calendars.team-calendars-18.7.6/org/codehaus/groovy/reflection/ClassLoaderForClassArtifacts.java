/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicInteger;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.GroovySunClassLoader;

public class ClassLoaderForClassArtifacts
extends ClassLoader {
    public final SoftReference<Class> klazz;
    private final AtomicInteger classNamesCounter = new AtomicInteger(-1);

    public ClassLoaderForClassArtifacts(Class klazz) {
        super(klazz.getClassLoader());
        this.klazz = new SoftReference<Class>(klazz);
    }

    public Class define(String name, byte[] bytes) {
        Class<?> cls = this.defineClass(name, bytes, 0, bytes.length, this.klazz.get().getProtectionDomain());
        this.resolveClass(cls);
        return cls;
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        Class cls = this.findLoadedClass(name);
        if (cls != null) {
            return cls;
        }
        if (GroovySunClassLoader.sunVM != null && (cls = GroovySunClassLoader.sunVM.doesKnow(name)) != null) {
            return cls;
        }
        return super.loadClass(name);
    }

    public String createClassName(Method method) {
        String clsName = this.klazz.get().getName();
        String name = clsName.startsWith("java.") ? clsName.replace('.', '_') + "$" + method.getName() : clsName + "$" + method.getName();
        int suffix = this.classNamesCounter.getAndIncrement();
        return suffix == -1 ? name : name + "$" + suffix;
    }

    public Constructor defineClassAndGetConstructor(final String name, final byte[] bytes) {
        Class cls = AccessController.doPrivileged(new PrivilegedAction<Class>(){

            @Override
            public Class run() {
                return ClassLoaderForClassArtifacts.this.define(name, bytes);
            }
        });
        if (cls != null) {
            try {
                return cls.getConstructor(CallSite.class, MetaClassImpl.class, MetaMethod.class, Class[].class, Constructor.class);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        return null;
    }
}

