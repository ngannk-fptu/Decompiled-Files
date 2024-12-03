/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.vmplugin.v7;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.vmplugin.v6.Java6;
import org.codehaus.groovy.vmplugin.v7.IndyInterface;

public class Java7
extends Java6 {
    private static final Constructor<MethodHandles.Lookup> LOOKUP_Constructor;

    @Override
    public void invalidateCallSites() {
        IndyInterface.invalidateSwitchPoints();
    }

    @Override
    public int getVersion() {
        return 7;
    }

    @Override
    public Object getInvokeSpecialHandle(final Method method, Object receiver) {
        if (LOOKUP_Constructor == null) {
            return super.getInvokeSpecialHandle(method, receiver);
        }
        if (!method.isAccessible()) {
            AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    method.setAccessible(true);
                    return null;
                }
            });
        }
        Class<?> declaringClass = method.getDeclaringClass();
        try {
            return LOOKUP_Constructor.newInstance(declaringClass, 2).unreflectSpecial(method, declaringClass).bindTo(receiver);
        }
        catch (ReflectiveOperationException e) {
            throw new GroovyBugError(e);
        }
    }

    @Override
    public Object invokeHandle(Object handle, Object[] args) throws Throwable {
        MethodHandle mh = (MethodHandle)handle;
        return mh.invokeWithArguments(args);
    }

    static {
        Constructor con = null;
        try {
            con = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
        }
        catch (NoSuchMethodException e) {
            throw new GroovyBugError(e);
        }
        try {
            if (!con.isAccessible()) {
                final Constructor tmp = con;
                AccessController.doPrivileged(new PrivilegedAction(){

                    public Object run() {
                        tmp.setAccessible(true);
                        return null;
                    }
                });
            }
        }
        catch (SecurityException se) {
            con = null;
        }
        catch (RuntimeException re) {
            if (!"java.lang.reflect.InaccessibleObjectException".equals(re.getClass().getName())) {
                throw re;
            }
            con = null;
        }
        LOOKUP_Constructor = con;
    }
}

