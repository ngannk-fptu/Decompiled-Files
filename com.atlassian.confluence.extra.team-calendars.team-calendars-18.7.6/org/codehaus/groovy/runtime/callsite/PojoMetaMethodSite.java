/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.runtime.GroovyCategorySupport;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.NullObject;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.callsite.CallSiteAwareMetaMethod;
import org.codehaus.groovy.runtime.callsite.MetaMethodSite;

public class PojoMetaMethodSite
extends MetaMethodSite {
    protected final int version;

    public PojoMetaMethodSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params) {
        super(site, metaClass, metaMethod, params);
        this.version = metaClass.getVersion();
    }

    public Object invoke(Object receiver, Object[] args) throws Throwable {
        MetaClassHelper.unwrap(args);
        return this.metaMethod.doMethodInvoke(receiver, args);
    }

    @Override
    public Object call(Object receiver, Object[] args) throws Throwable {
        if (this.checkCall(receiver, args)) {
            return this.invoke(receiver, args);
        }
        return CallSiteArray.defaultCall(this, receiver, args);
    }

    protected final boolean checkPojoMetaClass() {
        return !GroovyCategorySupport.hasCategoryInCurrentThread() && ((MetaClassImpl)this.metaClass).getVersion() == this.version;
    }

    protected final boolean checkCall(Object receiver, Object[] args) {
        try {
            return receiver.getClass() == this.metaClass.getTheClass() && this.checkPojoMetaClass() && MetaClassHelper.sameClasses(this.params, args);
        }
        catch (NullPointerException e) {
            if (receiver == null) {
                return this.checkCall((Object)NullObject.getNullObject(), args);
            }
            throw e;
        }
    }

    protected final boolean checkCall(Object receiver) {
        try {
            return receiver.getClass() == this.metaClass.getTheClass() && this.checkPojoMetaClass() && MetaClassHelper.sameClasses(this.params);
        }
        catch (NullPointerException e) {
            if (receiver == null) {
                return this.checkCall(NullObject.getNullObject());
            }
            throw e;
        }
    }

    protected final boolean checkCall(Object receiver, Object arg1) {
        try {
            return receiver.getClass() == this.metaClass.getTheClass() && this.checkPojoMetaClass() && MetaClassHelper.sameClasses(this.params, arg1);
        }
        catch (NullPointerException e) {
            if (receiver == null) {
                return this.checkCall((Object)NullObject.getNullObject(), arg1);
            }
            throw e;
        }
    }

    protected final boolean checkCall(Object receiver, Object arg1, Object arg2) {
        try {
            return receiver.getClass() == this.metaClass.getTheClass() && this.checkPojoMetaClass() && MetaClassHelper.sameClasses(this.params, arg1, arg2);
        }
        catch (NullPointerException e) {
            if (receiver == null) {
                return this.checkCall(NullObject.getNullObject(), arg1, arg2);
            }
            throw e;
        }
    }

    protected final boolean checkCall(Object receiver, Object arg1, Object arg2, Object arg3) {
        try {
            return receiver.getClass() == this.metaClass.getTheClass() && this.checkPojoMetaClass() && MetaClassHelper.sameClasses(this.params, arg1, arg2, arg3);
        }
        catch (NullPointerException e) {
            if (receiver == null) {
                return this.checkCall(NullObject.getNullObject(), arg1, arg2, arg3);
            }
            throw e;
        }
    }

    protected final boolean checkCall(Object receiver, Object arg1, Object arg2, Object arg3, Object arg4) {
        try {
            return receiver.getClass() == this.metaClass.getTheClass() && this.checkPojoMetaClass() && MetaClassHelper.sameClasses(this.params, arg1, arg2, arg3, arg4);
        }
        catch (NullPointerException e) {
            if (receiver == null) {
                return this.checkCall(NullObject.getNullObject(), arg1, arg2, arg3, arg4);
            }
            throw e;
        }
    }

    public static CallSite createPojoMetaMethodSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params, Object receiver, Object[] args) {
        if (metaMethod instanceof CallSiteAwareMetaMethod) {
            return ((CallSiteAwareMetaMethod)metaMethod).createPojoCallSite(site, metaClass, metaMethod, params, receiver, args);
        }
        if (metaMethod.getClass() == CachedMethod.class) {
            return PojoMetaMethodSite.createCachedMethodSite(site, metaClass, (CachedMethod)metaMethod, params, args);
        }
        return PojoMetaMethodSite.createNonAwareCallSite(site, metaClass, metaMethod, params, args);
    }

    public static CallSite createCachedMethodSite(CallSite site, MetaClassImpl metaClass, CachedMethod metaMethod, Class[] params, Object[] args) {
        if (metaMethod.correctArguments(args) == args && PojoMetaMethodSite.noWrappers(args)) {
            if (PojoMetaMethodSite.noCoerce(metaMethod, args)) {
                return new PojoCachedMethodSiteNoUnwrap(site, metaClass, (MetaMethod)metaMethod, params);
            }
            return metaMethod.createPojoMetaMethodSite(site, metaClass, params);
        }
        return new PojoCachedMethodSite(site, metaClass, (MetaMethod)metaMethod, params);
    }

    public static CallSite createNonAwareCallSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params, Object[] args) {
        if (metaMethod.correctArguments(args) == args && PojoMetaMethodSite.noWrappers(args)) {
            if (PojoMetaMethodSite.noCoerce(metaMethod, args)) {
                return new PojoMetaMethodSiteNoUnwrap(site, metaClass, metaMethod, params);
            }
            return new PojoMetaMethodSiteNoUnwrapNoCoerce(site, metaClass, metaMethod, params);
        }
        return new PojoMetaMethodSite(site, metaClass, metaMethod, params);
    }

    public static class PojoMetaMethodSiteNoUnwrapNoCoerce
    extends PojoMetaMethodSite {
        public PojoMetaMethodSiteNoUnwrapNoCoerce(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params) {
            super(site, metaClass, metaMethod, params);
        }

        @Override
        public final Object invoke(Object receiver, Object[] args) throws Throwable {
            try {
                return this.metaMethod.invoke(receiver, args);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
    }

    public static class PojoMetaMethodSiteNoUnwrap
    extends PojoMetaMethodSite {
        public PojoMetaMethodSiteNoUnwrap(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params) {
            super(site, metaClass, metaMethod, params);
        }

        @Override
        public final Object invoke(Object receiver, Object[] args) throws Throwable {
            try {
                return this.metaMethod.doMethodInvoke(receiver, args);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
    }

    public static class PojoCachedMethodSiteNoUnwrapNoCoerce
    extends PojoCachedMethodSite {
        public PojoCachedMethodSiteNoUnwrapNoCoerce(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params) {
            super(site, metaClass, metaMethod, params);
        }

        @Override
        public final Object invoke(Object receiver, Object[] args) throws Throwable {
            try {
                return this.reflect.invoke(receiver, args);
            }
            catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof GroovyRuntimeException) {
                    throw ScriptBytecodeAdapter.unwrap((GroovyRuntimeException)cause);
                }
                throw cause;
            }
        }
    }

    public static class PojoCachedMethodSiteNoUnwrap
    extends PojoCachedMethodSite {
        public PojoCachedMethodSiteNoUnwrap(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params) {
            super(site, metaClass, metaMethod, params);
        }

        @Override
        public final Object invoke(Object receiver, Object[] args) throws Throwable {
            args = this.metaMethod.coerceArgumentsToClasses(args);
            try {
                return this.reflect.invoke(receiver, args);
            }
            catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof GroovyRuntimeException) {
                    throw ScriptBytecodeAdapter.unwrap((GroovyRuntimeException)cause);
                }
                throw cause;
            }
        }
    }

    public static class PojoCachedMethodSite
    extends PojoMetaMethodSite {
        final Method reflect;

        public PojoCachedMethodSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params) {
            super(site, metaClass, metaMethod, params);
            this.reflect = ((CachedMethod)metaMethod).setAccessible();
        }

        @Override
        public Object invoke(Object receiver, Object[] args) throws Throwable {
            MetaClassHelper.unwrap(args);
            args = this.metaMethod.coerceArgumentsToClasses(args);
            try {
                return this.reflect.invoke(receiver, args);
            }
            catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof GroovyRuntimeException) {
                    throw ScriptBytecodeAdapter.unwrap((GroovyRuntimeException)cause);
                }
                throw cause;
            }
        }
    }
}

