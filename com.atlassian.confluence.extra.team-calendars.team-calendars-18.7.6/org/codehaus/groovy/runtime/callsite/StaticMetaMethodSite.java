/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.callsite.MetaMethodSite;

public class StaticMetaMethodSite
extends MetaMethodSite {
    private final int version;

    public StaticMetaMethodSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params) {
        super(site, metaClass, metaMethod, params);
        this.version = metaClass.getVersion();
    }

    public Object invoke(Object receiver, Object[] args) throws Throwable {
        MetaClassHelper.unwrap(args);
        try {
            return this.metaMethod.doMethodInvoke(receiver, args);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    protected final boolean checkCall(Object receiver, Object[] args) {
        return receiver == this.metaClass.getTheClass() && ((MetaClassImpl)this.metaClass).getVersion() == this.version && MetaClassHelper.sameClasses(this.params, args);
    }

    protected final boolean checkCall(Object receiver) {
        return receiver == this.metaClass.getTheClass() && ((MetaClassImpl)this.metaClass).getVersion() == this.version && MetaClassHelper.sameClasses(this.params);
    }

    protected final boolean checkCall(Object receiver, Object arg1) {
        return receiver == this.metaClass.getTheClass() && ((MetaClassImpl)this.metaClass).getVersion() == this.version && MetaClassHelper.sameClasses(this.params, arg1);
    }

    protected final boolean checkCall(Object receiver, Object arg1, Object arg2) {
        return receiver == this.metaClass.getTheClass() && ((MetaClassImpl)this.metaClass).getVersion() == this.version && MetaClassHelper.sameClasses(this.params, arg1, arg2);
    }

    protected final boolean checkCall(Object receiver, Object arg1, Object arg2, Object arg3) {
        return receiver == this.metaClass.getTheClass() && ((MetaClassImpl)this.metaClass).getVersion() == this.version && MetaClassHelper.sameClasses(this.params, arg1, arg2, arg3);
    }

    protected final boolean checkCall(Object receiver, Object arg1, Object arg2, Object arg3, Object arg4) {
        return receiver == this.metaClass.getTheClass() && ((MetaClassImpl)this.metaClass).getVersion() == this.version && MetaClassHelper.sameClasses(this.params, arg1, arg2, arg3, arg4);
    }

    @Override
    public Object call(Object receiver, Object[] args) throws Throwable {
        if (this.checkCall(receiver, args)) {
            try {
                return this.invoke(receiver, args);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return CallSiteArray.defaultCall(this, receiver, args);
    }

    @Override
    public Object callStatic(Class receiver, Object[] args) throws Throwable {
        if (this.checkCall((Object)receiver, args)) {
            return this.invoke(receiver, args);
        }
        return CallSiteArray.defaultCallStatic(this, receiver, args);
    }

    public static CallSite createStaticMetaMethodSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params, Object[] args) {
        if (metaMethod.correctArguments(args) == args && StaticMetaMethodSite.noWrappers(args)) {
            if (StaticMetaMethodSite.noCoerce(metaMethod, args)) {
                return new StaticMetaMethodSiteNoUnwrap(site, metaClass, metaMethod, params);
            }
            if (metaMethod.getClass() == CachedMethod.class) {
                return ((CachedMethod)metaMethod).createStaticMetaMethodSite(site, metaClass, params);
            }
            return new StaticMetaMethodSiteNoUnwrapNoCoerce(site, metaClass, metaMethod, params);
        }
        return new StaticMetaMethodSite(site, metaClass, metaMethod, params);
    }

    public static class StaticMetaMethodSiteNoUnwrapNoCoerce
    extends StaticMetaMethodSite {
        public StaticMetaMethodSiteNoUnwrapNoCoerce(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params) {
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

    public static class StaticMetaMethodSiteNoUnwrap
    extends StaticMetaMethodSite {
        public StaticMetaMethodSiteNoUnwrap(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params) {
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
}

