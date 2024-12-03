/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyInterceptable;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GroovyCategorySupport;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.MetaClassConstructorSite;
import org.codehaus.groovy.runtime.callsite.NullCallSite;
import org.codehaus.groovy.runtime.callsite.PerInstancePojoMetaClassSite;
import org.codehaus.groovy.runtime.callsite.PogoInterceptableSite;
import org.codehaus.groovy.runtime.callsite.PogoMetaClassSite;
import org.codehaus.groovy.runtime.callsite.PojoMetaClassSite;
import org.codehaus.groovy.runtime.callsite.StaticMetaClassSite;

public final class CallSiteArray {
    public final CallSite[] array;
    public static final Object[] NOPARAM = new Object[0];
    public final Class owner;

    public CallSiteArray(Class owner, String[] names) {
        this.owner = owner;
        this.array = new CallSite[names.length];
        for (int i = 0; i < this.array.length; ++i) {
            this.array[i] = new AbstractCallSite(this, i, names[i]);
        }
    }

    public static Object defaultCall(CallSite callSite, Object receiver, Object[] args) throws Throwable {
        return CallSiteArray.createCallSite(callSite, receiver, args).call(receiver, args);
    }

    public static Object defaultCallCurrent(CallSite callSite, GroovyObject receiver, Object[] args) throws Throwable {
        return CallSiteArray.createCallCurrentSite(callSite, receiver, args, callSite.getArray().owner).callCurrent(receiver, args);
    }

    public static Object defaultCallStatic(CallSite callSite, Class receiver, Object[] args) throws Throwable {
        return CallSiteArray.createCallStaticSite(callSite, receiver, args).callStatic(receiver, args);
    }

    public static Object defaultCallConstructor(CallSite callSite, Object receiver, Object[] args) throws Throwable {
        return CallSiteArray.createCallConstructorSite(callSite, (Class)receiver, args).callConstructor(receiver, args);
    }

    private static CallSite createCallStaticSite(CallSite callSite, final Class receiver, Object[] args) {
        AccessController.doPrivileged(new PrivilegedAction<Void>(){

            @Override
            public Void run() {
                try {
                    Class.forName(receiver.getName(), true, receiver.getClassLoader());
                }
                catch (Exception exception) {
                    // empty catch block
                }
                return null;
            }
        });
        MetaClass metaClass = InvokerHelper.getMetaClass(receiver);
        CallSite site = metaClass instanceof MetaClassImpl ? ((MetaClassImpl)metaClass).createStaticSite(callSite, args) : new StaticMetaClassSite(callSite, metaClass);
        CallSiteArray.replaceCallSite(callSite, site);
        return site;
    }

    private static CallSite createCallConstructorSite(CallSite callSite, Class receiver, Object[] args) {
        MetaClass metaClass = InvokerHelper.getMetaClass(receiver);
        CallSite site = metaClass instanceof MetaClassImpl ? ((MetaClassImpl)metaClass).createConstructorSite(callSite, args) : new MetaClassConstructorSite(callSite, metaClass);
        CallSiteArray.replaceCallSite(callSite, site);
        return site;
    }

    private static CallSite createCallCurrentSite(CallSite callSite, GroovyObject receiver, Object[] args, Class sender) {
        CallSite site;
        if (receiver instanceof GroovyInterceptable) {
            site = new PogoInterceptableSite(callSite);
        } else {
            MetaClass metaClass = receiver.getMetaClass();
            site = receiver.getClass() != metaClass.getTheClass() && !metaClass.getTheClass().isInterface() ? new PogoInterceptableSite(callSite) : (metaClass instanceof MetaClassImpl ? ((MetaClassImpl)metaClass).createPogoCallCurrentSite(callSite, sender, args) : new PogoMetaClassSite(callSite, metaClass));
        }
        CallSiteArray.replaceCallSite(callSite, site);
        return site;
    }

    private static CallSite createPojoSite(CallSite callSite, Object receiver, Object[] args) {
        Class<?> klazz = receiver.getClass();
        MetaClass metaClass = InvokerHelper.getMetaClass(receiver);
        if (!GroovyCategorySupport.hasCategoryInCurrentThread() && metaClass instanceof MetaClassImpl) {
            MetaClassImpl mci = (MetaClassImpl)metaClass;
            ClassInfo info = mci.getTheCachedClass().classInfo;
            if (info.hasPerInstanceMetaClasses()) {
                return new PerInstancePojoMetaClassSite(callSite, info);
            }
            return mci.createPojoCallSite(callSite, receiver, args);
        }
        ClassInfo info = ClassInfo.getClassInfo(klazz);
        if (info.hasPerInstanceMetaClasses()) {
            return new PerInstancePojoMetaClassSite(callSite, info);
        }
        return new PojoMetaClassSite(callSite, metaClass);
    }

    private static CallSite createPogoSite(CallSite callSite, Object receiver, Object[] args) {
        if (receiver instanceof GroovyInterceptable) {
            return new PogoInterceptableSite(callSite);
        }
        MetaClass metaClass = ((GroovyObject)receiver).getMetaClass();
        if (metaClass instanceof MetaClassImpl) {
            return ((MetaClassImpl)metaClass).createPogoCallSite(callSite, args);
        }
        return new PogoMetaClassSite(callSite, metaClass);
    }

    private static CallSite createCallSite(CallSite callSite, Object receiver, Object[] args) {
        if (receiver == null) {
            return new NullCallSite(callSite);
        }
        CallSite site = receiver instanceof Class ? CallSiteArray.createCallStaticSite(callSite, (Class)receiver, args) : (receiver instanceof GroovyObject ? CallSiteArray.createPogoSite(callSite, receiver, args) : CallSiteArray.createPojoSite(callSite, receiver, args));
        CallSiteArray.replaceCallSite(callSite, site);
        return site;
    }

    private static void replaceCallSite(CallSite oldSite, CallSite newSite) {
        oldSite.getArray().array[oldSite.getIndex()] = newSite;
    }
}

