/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import java.lang.reflect.Method;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.util.introspection.ClassMap;
import org.apache.velocity.util.introspection.IntrospectorCache;
import org.apache.velocity.util.introspection.IntrospectorCacheImpl;
import org.apache.velocity.util.introspection.MethodMap;

public abstract class IntrospectorBase {
    protected final Log log;
    private final IntrospectorCache introspectorCache;

    protected IntrospectorBase(Log log) {
        this.log = log;
        this.introspectorCache = new IntrospectorCacheImpl(log);
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

