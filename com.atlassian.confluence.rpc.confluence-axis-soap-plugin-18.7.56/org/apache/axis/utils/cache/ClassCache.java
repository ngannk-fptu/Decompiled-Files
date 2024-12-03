/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils.cache;

import java.util.Hashtable;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.cache.JavaClass;

public class ClassCache {
    Hashtable classCache = new Hashtable();

    public synchronized void registerClass(String name, Class cls) {
        if (name == null) {
            return;
        }
        JavaClass oldClass = (JavaClass)this.classCache.get(name);
        if (oldClass != null && oldClass.getJavaClass() == cls) {
            return;
        }
        this.classCache.put(name, new JavaClass(cls));
    }

    public synchronized void deregisterClass(String name) {
        this.classCache.remove(name);
    }

    public boolean isClassRegistered(String name) {
        return this.classCache != null && this.classCache.get(name) != null;
    }

    public JavaClass lookup(String className, ClassLoader cl) throws ClassNotFoundException {
        if (className == null) {
            return null;
        }
        JavaClass jc = (JavaClass)this.classCache.get(className);
        if (jc == null && cl != null) {
            Class cls = ClassUtils.forName(className, true, cl);
            jc = new JavaClass(cls);
        }
        return jc;
    }
}

