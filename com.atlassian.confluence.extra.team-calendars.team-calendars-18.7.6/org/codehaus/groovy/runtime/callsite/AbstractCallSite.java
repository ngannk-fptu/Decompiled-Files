/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaProperty;
import java.lang.reflect.Method;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedField;
import org.codehaus.groovy.reflection.ParameterTypes;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.GroovyCategorySupport;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.NullObject;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.callsite.ClassMetaClassGetPropertySite;
import org.codehaus.groovy.runtime.callsite.GetEffectivePogoFieldSite;
import org.codehaus.groovy.runtime.callsite.GetEffectivePogoPropertySite;
import org.codehaus.groovy.runtime.callsite.GetEffectivePojoFieldSite;
import org.codehaus.groovy.runtime.callsite.GetEffectivePojoPropertySite;
import org.codehaus.groovy.runtime.callsite.NullCallSite;
import org.codehaus.groovy.runtime.callsite.PogoGetPropertySite;
import org.codehaus.groovy.runtime.callsite.PogoMetaClassGetPropertySite;
import org.codehaus.groovy.runtime.callsite.PojoMetaClassGetPropertySite;
import org.codehaus.groovy.runtime.wrappers.Wrapper;

public class AbstractCallSite
implements CallSite {
    protected final int index;
    protected final String name;
    protected final CallSiteArray array;

    public AbstractCallSite(CallSiteArray array, int index, String name) {
        this.name = name;
        this.index = index;
        this.array = array;
    }

    public AbstractCallSite(CallSite prev) {
        this.name = prev.getName();
        this.index = prev.getIndex();
        this.array = prev.getArray();
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public CallSiteArray getArray() {
        return this.array;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public final Object callSafe(Object receiver, Object[] args) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return this.call(receiver, args);
    }

    @Override
    public final Object callSafe(Object receiver) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return this.call(receiver);
    }

    @Override
    public final Object callSafe(Object receiver, Object arg1) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return this.call(receiver, arg1);
    }

    @Override
    public final Object callSafe(Object receiver, Object arg1, Object arg2) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return this.call(receiver, arg1, arg2);
    }

    @Override
    public final Object callSafe(Object receiver, Object arg1, Object arg2, Object arg3) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return this.call(receiver, arg1, arg2, arg3);
    }

    @Override
    public Object callSafe(Object receiver, Object arg1, Object arg2, Object arg3, Object arg4) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return this.call(receiver, arg1, arg2, arg3, arg4);
    }

    @Override
    public Object call(Object receiver, Object[] args) throws Throwable {
        return CallSiteArray.defaultCall(this, receiver, args);
    }

    @Override
    public Object call(Object receiver) throws Throwable {
        return this.call(receiver, CallSiteArray.NOPARAM);
    }

    @Override
    public Object call(Object receiver, Object arg1) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.call(receiver, arg1);
        }
        return this.call(receiver, ArrayUtil.createArray(arg1));
    }

    @Override
    public Object call(Object receiver, Object arg1, Object arg2) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.call(receiver, arg1, arg2);
        }
        return this.call(receiver, ArrayUtil.createArray(arg1, arg2));
    }

    @Override
    public Object call(Object receiver, Object arg1, Object arg2, Object arg3) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.call(receiver, arg1, arg2, arg3);
        }
        return this.call(receiver, ArrayUtil.createArray(arg1, arg2, arg3));
    }

    @Override
    public Object call(Object receiver, Object arg1, Object arg2, Object arg3, Object arg4) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.call(receiver, arg1, arg2, arg3, arg4);
        }
        return this.call(receiver, ArrayUtil.createArray(arg1, arg2, arg3, arg4));
    }

    @Override
    public Object callCurrent(GroovyObject receiver, Object[] args) throws Throwable {
        return CallSiteArray.defaultCallCurrent(this, receiver, args);
    }

    @Override
    public Object callCurrent(GroovyObject receiver) throws Throwable {
        return this.callCurrent(receiver, CallSiteArray.NOPARAM);
    }

    @Override
    public Object callCurrent(GroovyObject receiver, Object arg1) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.callCurrent(receiver, arg1);
        }
        return this.callCurrent(receiver, ArrayUtil.createArray(arg1));
    }

    @Override
    public Object callCurrent(GroovyObject receiver, Object arg1, Object arg2) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.callCurrent(receiver, arg1, arg2);
        }
        return this.callCurrent(receiver, ArrayUtil.createArray(arg1, arg2));
    }

    @Override
    public Object callCurrent(GroovyObject receiver, Object arg1, Object arg2, Object arg3) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.callCurrent(receiver, arg1, arg2, arg3);
        }
        return this.callCurrent(receiver, ArrayUtil.createArray(arg1, arg2, arg3));
    }

    @Override
    public Object callCurrent(GroovyObject receiver, Object arg1, Object arg2, Object arg3, Object arg4) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.callCurrent(receiver, arg1, arg2, arg3, arg4);
        }
        return this.callCurrent(receiver, ArrayUtil.createArray(arg1, arg2, arg3, arg4));
    }

    @Override
    public Object callStatic(Class receiver, Object[] args) throws Throwable {
        return CallSiteArray.defaultCallStatic(this, receiver, args);
    }

    @Override
    public Object callStatic(Class receiver) throws Throwable {
        return this.callStatic(receiver, CallSiteArray.NOPARAM);
    }

    @Override
    public Object callStatic(Class receiver, Object arg1) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.callStatic(receiver, arg1);
        }
        return this.callStatic(receiver, ArrayUtil.createArray(arg1));
    }

    @Override
    public Object callStatic(Class receiver, Object arg1, Object arg2) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.callStatic(receiver, arg1, arg2);
        }
        return this.callStatic(receiver, ArrayUtil.createArray(arg1, arg2));
    }

    @Override
    public Object callStatic(Class receiver, Object arg1, Object arg2, Object arg3) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.callStatic(receiver, arg1, arg2, arg3);
        }
        return this.callStatic(receiver, ArrayUtil.createArray(arg1, arg2, arg3));
    }

    @Override
    public Object callStatic(Class receiver, Object arg1, Object arg2, Object arg3, Object arg4) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.callStatic(receiver, arg1, arg2, arg3, arg4);
        }
        return this.callStatic(receiver, ArrayUtil.createArray(arg1, arg2, arg3, arg4));
    }

    @Override
    public Object callConstructor(Object receiver, Object[] args) throws Throwable {
        return CallSiteArray.defaultCallConstructor(this, receiver, args);
    }

    @Override
    public Object callConstructor(Object receiver) throws Throwable {
        return this.callConstructor(receiver, CallSiteArray.NOPARAM);
    }

    @Override
    public Object callConstructor(Object receiver, Object arg1) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.callConstructor(receiver, arg1);
        }
        return this.callConstructor(receiver, ArrayUtil.createArray(arg1));
    }

    @Override
    public Object callConstructor(Object receiver, Object arg1, Object arg2) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.callConstructor(receiver, arg1, arg2);
        }
        return this.callConstructor(receiver, ArrayUtil.createArray(arg1, arg2));
    }

    @Override
    public Object callConstructor(Object receiver, Object arg1, Object arg2, Object arg3) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.callConstructor(receiver, arg1, arg2, arg3);
        }
        return this.callConstructor(receiver, ArrayUtil.createArray(arg1, arg2, arg3));
    }

    @Override
    public Object callConstructor(Object receiver, Object arg1, Object arg2, Object arg3, Object arg4) throws Throwable {
        CallSite stored = this.array.array[this.index];
        if (stored != this) {
            return stored.callConstructor(receiver, arg1, arg2, arg3, arg4);
        }
        return this.callConstructor(receiver, ArrayUtil.createArray(arg1, arg2, arg3, arg4));
    }

    static boolean noCoerce(ParameterTypes metaMethod, Object[] args) {
        CachedClass[] paramClasses = metaMethod.getParameterTypes();
        if (paramClasses.length != args.length) {
            return false;
        }
        for (int i = 0; i < paramClasses.length; ++i) {
            CachedClass paramClass = paramClasses[i];
            if (args[i] == null || paramClass.isDirectlyAssignable(args[i])) continue;
            return true;
        }
        return false;
    }

    static boolean noWrappers(Object[] args) {
        for (int i = 0; i != args.length; ++i) {
            if (!(args[i] instanceof Wrapper)) continue;
            return false;
        }
        return true;
    }

    @Override
    public Object callGetProperty(Object receiver) throws Throwable {
        return this.acceptGetProperty(receiver).getProperty(receiver);
    }

    @Override
    public Object callGroovyObjectGetProperty(Object receiver) throws Throwable {
        if (receiver == null) {
            try {
                return InvokerHelper.getProperty(NullObject.getNullObject(), this.name);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return this.acceptGroovyObjectGetProperty(receiver).getProperty(receiver);
    }

    public CallSite acceptGetProperty(Object receiver) {
        return this.createGetPropertySite(receiver);
    }

    public CallSite acceptGroovyObjectGetProperty(Object receiver) {
        return this.createGroovyObjectGetPropertySite(receiver);
    }

    protected final CallSite createGetPropertySite(Object receiver) {
        if (receiver == null) {
            return new NullCallSite(this);
        }
        if (receiver instanceof GroovyObject) {
            return this.createGroovyObjectGetPropertySite(receiver);
        }
        if (receiver instanceof Class) {
            return this.createClassMetaClassGetPropertySite((Class)receiver);
        }
        return this.createPojoMetaClassGetPropertySite(receiver);
    }

    protected final CallSite createGroovyObjectGetPropertySite(Object receiver) {
        Class<?> aClass = receiver.getClass();
        try {
            Method method = aClass.getMethod("getProperty", String.class);
            if (method != null && method.isSynthetic() && ((GroovyObject)receiver).getMetaClass() instanceof MetaClassImpl) {
                return this.createPogoMetaClassGetPropertySite((GroovyObject)receiver);
            }
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        if (receiver instanceof Class) {
            return this.createClassMetaClassGetPropertySite((Class)receiver);
        }
        return this.createPogoGetPropertySite(aClass);
    }

    @Override
    public Object getProperty(Object receiver) throws Throwable {
        throw new UnsupportedOperationException();
    }

    private CallSite createPojoMetaClassGetPropertySite(Object receiver) {
        MetaProperty effective;
        MetaClass metaClass = InvokerHelper.getMetaClass(receiver);
        AbstractCallSite site = metaClass.getClass() != MetaClassImpl.class || GroovyCategorySupport.hasCategoryInCurrentThread() ? new PojoMetaClassGetPropertySite(this) : ((effective = ((MetaClassImpl)metaClass).getEffectiveGetMetaProperty(receiver.getClass(), receiver, this.name, false)) != null ? (effective instanceof CachedField ? new GetEffectivePojoFieldSite(this, (MetaClassImpl)metaClass, (CachedField)effective) : new GetEffectivePojoPropertySite(this, (MetaClassImpl)metaClass, effective)) : new PojoMetaClassGetPropertySite(this));
        this.array.array[this.index] = site;
        return site;
    }

    private CallSite createClassMetaClassGetPropertySite(Class aClass) {
        ClassMetaClassGetPropertySite site = new ClassMetaClassGetPropertySite(this, aClass);
        this.array.array[this.index] = site;
        return site;
    }

    private CallSite createPogoMetaClassGetPropertySite(GroovyObject receiver) {
        MetaProperty effective;
        MetaClass metaClass = receiver.getMetaClass();
        AbstractCallSite site = metaClass.getClass() != MetaClassImpl.class || GroovyCategorySupport.hasCategoryInCurrentThread() ? new PogoMetaClassGetPropertySite(this, metaClass) : ((effective = ((MetaClassImpl)metaClass).getEffectiveGetMetaProperty(this.array.owner, receiver, this.name, false)) != null ? (effective instanceof CachedField ? new GetEffectivePogoFieldSite(this, metaClass, (CachedField)effective) : new GetEffectivePogoPropertySite(this, metaClass, effective)) : new PogoMetaClassGetPropertySite(this, metaClass));
        this.array.array[this.index] = site;
        return site;
    }

    private CallSite createPogoGetPropertySite(Class aClass) {
        PogoGetPropertySite site = new PogoGetPropertySite(this, aClass);
        this.array.array[this.index] = site;
        return site;
    }

    @Override
    public final Object callGetPropertySafe(Object receiver) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return this.callGetProperty(receiver);
    }

    @Override
    public final Object callGroovyObjectGetPropertySafe(Object receiver) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return this.callGroovyObjectGetProperty(receiver);
    }
}

