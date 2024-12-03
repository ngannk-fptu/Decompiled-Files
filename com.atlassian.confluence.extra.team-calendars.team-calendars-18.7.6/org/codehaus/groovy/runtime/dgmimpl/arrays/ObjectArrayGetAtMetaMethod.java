/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.dgmimpl.arrays;

import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.PojoMetaMethodSite;
import org.codehaus.groovy.runtime.dgmimpl.arrays.ArrayGetAtMetaMethod;
import org.codehaus.groovy.runtime.dgmimpl.arrays.ArrayMetaMethod;

public class ObjectArrayGetAtMetaMethod
extends ArrayGetAtMetaMethod {
    private static final CachedClass OBJECT_ARR_CLASS = ReflectionCache.OBJECT_ARRAY_CLASS;

    @Override
    public Class getReturnType() {
        return Object.class;
    }

    @Override
    public final CachedClass getDeclaringClass() {
        return OBJECT_ARR_CLASS;
    }

    @Override
    public Object invoke(Object object, Object[] arguments) {
        Object[] objects = (Object[])object;
        return objects[ObjectArrayGetAtMetaMethod.normaliseIndex((Integer)arguments[0], objects.length)];
    }

    @Override
    public CallSite createPojoCallSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params, Object receiver, Object[] args) {
        if (!(args[0] instanceof Integer)) {
            return PojoMetaMethodSite.createNonAwareCallSite(site, metaClass, metaMethod, params, args);
        }
        return new MyPojoMetaMethodSite(site, metaClass, metaMethod, params);
    }

    private static class MyPojoMetaMethodSite
    extends PojoMetaMethodSite {
        public MyPojoMetaMethodSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params) {
            super(site, metaClass, metaMethod, params);
        }

        @Override
        public Object call(Object receiver, Object arg) throws Throwable {
            block3: {
                if (this.checkPojoMetaClass()) {
                    try {
                        Object[] objects = (Object[])receiver;
                        return objects[ArrayMetaMethod.normaliseIndex((Integer)arg, objects.length)];
                    }
                    catch (ClassCastException e) {
                        if (!(receiver instanceof Object[]) || !(arg instanceof Integer)) break block3;
                        throw e;
                    }
                }
            }
            return super.call(receiver, arg);
        }
    }
}

