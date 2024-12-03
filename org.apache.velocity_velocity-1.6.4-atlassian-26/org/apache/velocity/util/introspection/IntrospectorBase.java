/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import java.lang.reflect.Method;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.util.ClassConstructionException;
import org.apache.velocity.util.ClassUtils;
import org.apache.velocity.util.introspection.ClassMap;
import org.apache.velocity.util.introspection.IntrospectorCache;
import org.apache.velocity.util.introspection.IntrospectorCacheImpl;
import org.apache.velocity.util.introspection.MethodMap;

public abstract class IntrospectorBase {
    protected final Log log;
    private final IntrospectorCache introspectorCache;

    protected IntrospectorBase(Log log, RuntimeServices runtimeServices) {
        this.log = log;
        String introspectorCacheClass = runtimeServices.getConfiguration().getString("introspector.cache.classes", IntrospectorCacheImpl.class.getName());
        try {
            this.introspectorCache = ClassUtils.getNewInstance(introspectorCacheClass, IntrospectorCache.class, log);
        }
        catch (ClassConstructionException e) {
            throw new ClassConstructionException("Cannot instantiate class for introspector.cache.classes", e);
        }
    }

    public Method getMethod(Class c, String name, Object[] params) throws IllegalArgumentException, MethodMap.AmbiguousException {
        if (c == null) {
            throw new IllegalArgumentException("class object is null!");
        }
        if (params == null) {
            throw new IllegalArgumentException("params object is null!");
        }
        IntrospectorCache ic = this.getIntrospectorCache();
        ClassMap classMap = ic.get(c);
        if (classMap == null) {
            classMap = ic.put(c);
        }
        return classMap.findMethod(name, params);
    }

    protected IntrospectorCache getIntrospectorCache() {
        return this.introspectorCache;
    }
}

