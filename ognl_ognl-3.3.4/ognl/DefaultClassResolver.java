/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import ognl.ClassResolver;

public class DefaultClassResolver
implements ClassResolver {
    private final ConcurrentHashMap<String, Class> classes = new ConcurrentHashMap(101);

    @Override
    public Class classForName(String className, Map context) throws ClassNotFoundException {
        Class result = this.classes.get(className);
        if (result != null) {
            return result;
        }
        try {
            result = this.toClassForName(className);
        }
        catch (ClassNotFoundException e) {
            if (className.indexOf(46) > -1) {
                throw e;
            }
            try {
                result = this.toClassForName("java.lang." + className);
            }
            catch (ClassNotFoundException e2) {
                throw e;
            }
        }
        this.classes.putIfAbsent(className, result);
        return result;
    }

    protected Class toClassForName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
}

