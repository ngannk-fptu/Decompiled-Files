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

public class ShortArrayGetAtMetaMethod
extends ArrayGetAtMetaMethod {
    private static final CachedClass ARR_CLASS = ReflectionCache.getCachedClass(short[].class);

    @Override
    public Class getReturnType() {
        return Short.class;
    }

    @Override
    public final CachedClass getDeclaringClass() {
        return ARR_CLASS;
    }

    @Override
    public Object invoke(Object object, Object[] args) {
        short[] objects = (short[])object;
        return objects[ShortArrayGetAtMetaMethod.normaliseIndex((Integer)args[0], objects.length)];
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
            if (receiver instanceof short[] && arg instanceof Integer && this.checkPojoMetaClass()) {
                short[] objects = (short[])receiver;
                return objects[ArrayMetaMethod.normaliseIndex((Integer)arg, objects.length)];
            }
            return super.call(receiver, arg);
        }
    }
}

