/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.context.support;

import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.core.OverridingClassLoader;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

class ContextTypeMatchClassLoader
extends DecoratingClassLoader
implements SmartClassLoader {
    @Nullable
    private static final Method findLoadedClassMethod;
    private final Map<String, byte[]> bytesCache = new ConcurrentHashMap<String, byte[]>(256);

    public ContextTypeMatchClassLoader(@Nullable ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return new ContextOverridingClassLoader(this.getParent()).loadClass(name);
    }

    @Override
    public boolean isClassReloadable(Class<?> clazz) {
        return clazz.getClassLoader() instanceof ContextOverridingClassLoader;
    }

    @Override
    public Class<?> publicDefineClass(String name, byte[] b, @Nullable ProtectionDomain protectionDomain) {
        return this.defineClass(name, b, 0, b.length, protectionDomain);
    }

    static {
        Method method;
        ClassLoader.registerAsParallelCapable();
        try {
            method = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
            ReflectionUtils.makeAccessible(method);
        }
        catch (Throwable ex) {
            method = null;
            LogFactory.getLog(ContextTypeMatchClassLoader.class).debug((Object)"ClassLoader.findLoadedClass not accessible -> will always override requested class", ex);
        }
        findLoadedClassMethod = method;
    }

    private class ContextOverridingClassLoader
    extends OverridingClassLoader {
        public ContextOverridingClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected boolean isEligibleForOverriding(String className) {
            if (this.isExcluded(className) || ContextTypeMatchClassLoader.this.isExcluded(className)) {
                return false;
            }
            if (findLoadedClassMethod != null) {
                for (ClassLoader parent = this.getParent(); parent != null; parent = parent.getParent()) {
                    if (ReflectionUtils.invokeMethod(findLoadedClassMethod, parent, className) == null) continue;
                    return false;
                }
            }
            return true;
        }

        @Override
        protected Class<?> loadClassForOverriding(String name) throws ClassNotFoundException {
            byte[] bytes = (byte[])ContextTypeMatchClassLoader.this.bytesCache.get(name);
            if (bytes == null) {
                bytes = this.loadBytesForClass(name);
                if (bytes != null) {
                    ContextTypeMatchClassLoader.this.bytesCache.put(name, bytes);
                } else {
                    return null;
                }
            }
            return this.defineClass(name, bytes, 0, bytes.length);
        }
    }
}

