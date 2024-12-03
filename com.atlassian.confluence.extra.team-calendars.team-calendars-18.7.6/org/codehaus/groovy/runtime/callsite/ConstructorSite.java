/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClassImpl;
import java.util.Map;
import org.codehaus.groovy.reflection.CachedConstructor;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.callsite.MetaClassSite;

public class ConstructorSite
extends MetaClassSite {
    final CachedConstructor constructor;
    final Class[] params;
    private final int version;

    public ConstructorSite(CallSite site, MetaClassImpl metaClass, CachedConstructor constructor, Class[] params) {
        super(site, metaClass);
        this.constructor = constructor;
        this.params = params;
        this.version = metaClass.getVersion();
    }

    @Override
    public Object callConstructor(Object receiver, Object[] args) throws Throwable {
        if (this.checkCall(receiver, args)) {
            MetaClassHelper.unwrap(args);
            try {
                return this.constructor.doConstructorInvoke(args);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return CallSiteArray.defaultCallConstructor(this, receiver, args);
    }

    protected final boolean checkCall(Object receiver, Object[] args) {
        return receiver == this.metaClass.getTheClass() && ((MetaClassImpl)this.metaClass).getVersion() == this.version && MetaClassHelper.sameClasses(this.params, args);
    }

    public static ConstructorSite createConstructorSite(CallSite site, MetaClassImpl metaClass, CachedConstructor constructor, Class[] params, Object[] args) {
        if (constructor.correctArguments(args) == args && ConstructorSite.noWrappers(args)) {
            if (ConstructorSite.noCoerce(constructor, args)) {
                return new ConstructorSiteNoUnwrap(site, metaClass, constructor, params);
            }
            return new ConstructorSiteNoUnwrapNoCoerce(site, metaClass, constructor, params);
        }
        return new ConstructorSite(site, metaClass, constructor, params);
    }

    public static class NoParamSiteInnerClass
    extends ConstructorSiteNoUnwrapNoCoerce {
        public NoParamSiteInnerClass(CallSite site, MetaClassImpl metaClass, CachedConstructor constructor, Class[] params) {
            super(site, metaClass, constructor, params);
        }

        @Override
        public final Object callConstructor(Object receiver, Object[] args) throws Throwable {
            if (this.checkCall(receiver, args)) {
                Object[] newArgs = new Object[]{args[0]};
                Object bean = this.constructor.invoke(newArgs);
                try {
                    ((MetaClassImpl)this.metaClass).setProperties(bean, (Map)args[1]);
                }
                catch (GroovyRuntimeException gre) {
                    throw ScriptBytecodeAdapter.unwrap(gre);
                }
                return bean;
            }
            return CallSiteArray.defaultCallConstructor(this, receiver, args);
        }
    }

    public static class NoParamSite
    extends ConstructorSiteNoUnwrapNoCoerce {
        private static final Object[] NO_ARGS = new Object[0];

        public NoParamSite(CallSite site, MetaClassImpl metaClass, CachedConstructor constructor, Class[] params) {
            super(site, metaClass, constructor, params);
        }

        @Override
        public final Object callConstructor(Object receiver, Object[] args) throws Throwable {
            if (this.checkCall(receiver, args)) {
                Object bean = this.constructor.invoke(NO_ARGS);
                try {
                    ((MetaClassImpl)this.metaClass).setProperties(bean, (Map)args[0]);
                }
                catch (GroovyRuntimeException gre) {
                    throw ScriptBytecodeAdapter.unwrap(gre);
                }
                return bean;
            }
            return CallSiteArray.defaultCallConstructor(this, receiver, args);
        }
    }

    public static class ConstructorSiteNoUnwrapNoCoerce
    extends ConstructorSite {
        public ConstructorSiteNoUnwrapNoCoerce(CallSite site, MetaClassImpl metaClass, CachedConstructor constructor, Class[] params) {
            super(site, metaClass, constructor, params);
        }

        @Override
        public Object callConstructor(Object receiver, Object[] args) throws Throwable {
            if (this.checkCall(receiver, args)) {
                try {
                    return this.constructor.invoke(args);
                }
                catch (GroovyRuntimeException gre) {
                    throw ScriptBytecodeAdapter.unwrap(gre);
                }
            }
            return CallSiteArray.defaultCallConstructor(this, receiver, args);
        }
    }

    public static class ConstructorSiteNoUnwrap
    extends ConstructorSite {
        public ConstructorSiteNoUnwrap(CallSite site, MetaClassImpl metaClass, CachedConstructor constructor, Class[] params) {
            super(site, metaClass, constructor, params);
        }

        @Override
        public final Object callConstructor(Object receiver, Object[] args) throws Throwable {
            if (this.checkCall(receiver, args)) {
                try {
                    return this.constructor.doConstructorInvoke(args);
                }
                catch (GroovyRuntimeException gre) {
                    throw ScriptBytecodeAdapter.unwrap(gre);
                }
            }
            return CallSiteArray.defaultCallConstructor(this, receiver, args);
        }
    }
}

