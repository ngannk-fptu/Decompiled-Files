/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.dgmimpl.arrays;

import groovy.lang.GString;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.PojoMetaMethodSite;
import org.codehaus.groovy.runtime.dgmimpl.arrays.ArrayMetaMethod;
import org.codehaus.groovy.runtime.dgmimpl.arrays.ArrayPutAtMetaMethod;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ShortArrayPutAtMetaMethod
extends ArrayPutAtMetaMethod {
    private static final CachedClass OBJECT_CLASS = ReflectionCache.OBJECT_CLASS;
    private static final CachedClass ARR_CLASS = ReflectionCache.getCachedClass(short[].class);
    private static final CachedClass[] PARAM_CLASS_ARR = new CachedClass[]{INTEGER_CLASS, OBJECT_CLASS};

    public ShortArrayPutAtMetaMethod() {
        this.parameterTypes = PARAM_CLASS_ARR;
    }

    @Override
    public final CachedClass getDeclaringClass() {
        return ARR_CLASS;
    }

    @Override
    public Object invoke(Object object, Object[] args) {
        short[] objects = (short[])object;
        int index = ShortArrayPutAtMetaMethod.normaliseIndex((Integer)args[0], objects.length);
        Object newValue = args[1];
        if (!(newValue instanceof Short)) {
            if (newValue instanceof Character || newValue instanceof String || newValue instanceof GString) {
                Character ch = DefaultTypeTransformation.getCharFromSizeOneString(newValue);
                objects[index] = (Short)DefaultTypeTransformation.castToType(ch, Short.class);
            } else {
                objects[index] = ((Number)newValue).shortValue();
            }
        } else {
            objects[index] = (Short)args[1];
        }
        return null;
    }

    @Override
    public CallSite createPojoCallSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params, Object receiver, Object[] args) {
        if (!(args[0] instanceof Integer) || !(args[1] instanceof Short)) {
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
            if (receiver instanceof short[] && args[0] instanceof Integer && args[1] instanceof Short && this.checkPojoMetaClass()) {
                short[] objects = (short[])receiver;
                objects[ArrayMetaMethod.normaliseIndex((int)((Integer)args[0]).intValue(), (int)objects.length)] = (Short)args[1];
                return null;
            }
            return super.call(receiver, args);
        }

        @Override
        public Object call(Object receiver, Object arg1, Object arg2) throws Throwable {
            block3: {
                if (this.checkPojoMetaClass()) {
                    try {
                        short[] objects = (short[])receiver;
                        objects[ArrayMetaMethod.normaliseIndex((int)((Integer)arg1).intValue(), (int)objects.length)] = (Short)arg2;
                        return null;
                    }
                    catch (ClassCastException e) {
                        if (!(receiver instanceof short[]) || !(arg1 instanceof Integer)) break block3;
                        throw e;
                    }
                }
            }
            return super.call(receiver, arg1, arg2);
        }
    }
}

