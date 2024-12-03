/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import groovy.lang.GroovyRuntimeException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ParameterTypes;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;

public class CachedConstructor
extends ParameterTypes {
    CachedClass clazz;
    public final Constructor cachedConstructor;

    public CachedConstructor(CachedClass clazz, final Constructor c) {
        block3: {
            this.cachedConstructor = c;
            this.clazz = clazz;
            try {
                AccessController.doPrivileged(new PrivilegedAction(){

                    public Object run() {
                        c.setAccessible(true);
                        return null;
                    }
                });
            }
            catch (SecurityException securityException) {
            }
            catch (RuntimeException re) {
                if ("java.lang.reflect.InaccessibleObjectException".equals(re.getClass().getName())) break block3;
                throw re;
            }
        }
    }

    public CachedConstructor(Constructor c) {
        this(ReflectionCache.getCachedClass(c.getDeclaringClass()), c);
    }

    @Override
    protected Class[] getPT() {
        return this.cachedConstructor.getParameterTypes();
    }

    public static CachedConstructor find(Constructor constructor) {
        CachedConstructor[] constructors = ReflectionCache.getCachedClass(constructor.getDeclaringClass()).getConstructors();
        for (int i = 0; i < constructors.length; ++i) {
            CachedConstructor cachedConstructor = constructors[i];
            if (!cachedConstructor.cachedConstructor.equals(constructor)) continue;
            return cachedConstructor;
        }
        throw new RuntimeException("Couldn't find method: " + constructor);
    }

    public Object doConstructorInvoke(Object[] argumentArray) {
        argumentArray = this.coerceArgumentsToClasses(argumentArray);
        return this.invoke(argumentArray);
    }

    public Object invoke(Object[] argumentArray) {
        Constructor constr = this.cachedConstructor;
        try {
            return constr.newInstance(argumentArray);
        }
        catch (InvocationTargetException e) {
            throw e.getCause() instanceof RuntimeException ? (RuntimeException)e.getCause() : new InvokerInvocationException(e);
        }
        catch (IllegalArgumentException e) {
            throw CachedConstructor.createException("failed to invoke constructor: ", constr, argumentArray, e, false);
        }
        catch (IllegalAccessException e) {
            throw CachedConstructor.createException("could not access constructor: ", constr, argumentArray, e, false);
        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw CachedConstructor.createException("failed to invoke constructor: ", constr, argumentArray, e, true);
        }
    }

    private static GroovyRuntimeException createException(String init, Constructor constructor, Object[] argumentArray, Throwable e, boolean setReason) {
        return new GroovyRuntimeException(init + constructor + " with arguments: " + InvokerHelper.toString(argumentArray) + " reason: " + e, setReason ? e : null);
    }

    public int getModifiers() {
        return this.cachedConstructor.getModifiers();
    }

    public CachedClass getCachedClass() {
        return this.clazz;
    }
}

