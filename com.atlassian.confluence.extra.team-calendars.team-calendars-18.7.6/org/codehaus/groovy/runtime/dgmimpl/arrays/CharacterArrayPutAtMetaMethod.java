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
import org.codehaus.groovy.runtime.dgmimpl.arrays.ArrayMetaMethod;
import org.codehaus.groovy.runtime.dgmimpl.arrays.ArrayPutAtMetaMethod;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class CharacterArrayPutAtMetaMethod
extends ArrayPutAtMetaMethod {
    private static final CachedClass OBJECT_CLASS = ReflectionCache.OBJECT_CLASS;
    private static final CachedClass ARR_CLASS = ReflectionCache.getCachedClass(char[].class);
    private static final CachedClass[] PARAM_CLASS_ARR = new CachedClass[]{INTEGER_CLASS, OBJECT_CLASS};

    public CharacterArrayPutAtMetaMethod() {
        this.parameterTypes = PARAM_CLASS_ARR;
    }

    @Override
    public final CachedClass getDeclaringClass() {
        return ARR_CLASS;
    }

    @Override
    public Object invoke(Object object, Object[] args) {
        char[] objects = (char[])object;
        int index = CharacterArrayPutAtMetaMethod.normaliseIndex((Integer)args[0], objects.length);
        objects[index] = DefaultTypeTransformation.getCharFromSizeOneString(args[1]).charValue();
        return null;
    }

    @Override
    public CallSite createPojoCallSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params, Object receiver, Object[] args) {
        if (!(args[0] instanceof Integer) || !(args[1] instanceof Character)) {
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
        public Object call(Object receiver, Object[] args) throws Throwable {
            if (receiver instanceof char[] && args[0] instanceof Integer && args[1] instanceof Character && this.checkPojoMetaClass()) {
                char[] objects = (char[])receiver;
                objects[ArrayMetaMethod.normaliseIndex((int)((Integer)args[0]).intValue(), (int)objects.length)] = ((Character)args[1]).charValue();
                return null;
            }
            return super.call(receiver, args);
        }

        @Override
        public Object call(Object receiver, Object arg1, Object arg2) throws Throwable {
            block3: {
                if (this.checkPojoMetaClass()) {
                    try {
                        char[] objects = (char[])receiver;
                        objects[ArrayMetaMethod.normaliseIndex((int)((Integer)arg1).intValue(), (int)objects.length)] = ((Character)arg2).charValue();
                        return null;
                    }
                    catch (ClassCastException e) {
                        if (!(receiver instanceof char[]) || !(arg1 instanceof Integer)) break block3;
                        throw e;
                    }
                }
            }
            return super.call(receiver, arg1, arg2);
        }
    }
}

