/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.util.introspection.ClassMap;
import org.apache.velocity.util.introspection.IntrospectorCache;

public final class IntrospectorCacheImpl
implements IntrospectorCache {
    public static final String CACHEDUMP_MSG = "IntrospectorCache detected classloader change. Dumping cache.";
    private final Log log;
    private final Map classMapCache = new HashMap();
    private final Set classNameCache = new HashSet();

    public IntrospectorCacheImpl(Log log) {
        this.log = log;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        Map map = this.classMapCache;
        synchronized (map) {
            this.classMapCache.clear();
            this.classNameCache.clear();
            this.log.debug(CACHEDUMP_MSG);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ClassMap get(Class c) {
        if (c == null) {
            throw new IllegalArgumentException("class is null!");
        }
        ClassMap classMap = (ClassMap)this.classMapCache.get(c);
        if (classMap == null) {
            Map map = this.classMapCache;
            synchronized (map) {
                if (this.classNameCache.contains(c.getName())) {
                    this.clear();
                }
            }
        }
        return classMap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ClassMap put(Class c) {
        ClassMap classMap = new ClassMap(c, this.log);
        Map map = this.classMapCache;
        synchronized (map) {
            this.classMapCache.put(c, classMap);
            this.classNameCache.add(c.getName());
        }
        return classMap;
    }
}

