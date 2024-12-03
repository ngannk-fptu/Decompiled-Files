/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import groovy.lang.MissingMethodException;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ParameterTypes;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteGenerator;
import org.codehaus.groovy.runtime.callsite.PogoMetaMethodSite;
import org.codehaus.groovy.runtime.callsite.PojoMetaMethodSite;
import org.codehaus.groovy.runtime.callsite.StaticMetaMethodSite;
import org.codehaus.groovy.runtime.metaclass.MethodHelper;

public class CachedMethod
extends MetaMethod
implements Comparable {
    public final CachedClass cachedClass;
    private final Method cachedMethod;
    private int hashCode;
    private static MyComparator comparator = new MyComparator();
    private SoftReference<Constructor> pogoCallSiteConstructor;
    private SoftReference<Constructor> pojoCallSiteConstructor;
    private SoftReference<Constructor> staticCallSiteConstructor;
    private boolean skipCompiled;

    public CachedMethod(CachedClass clazz, Method method) {
        this.cachedMethod = method;
        this.cachedClass = clazz;
    }

    public CachedMethod(Method method) {
        this(ReflectionCache.getCachedClass(method.getDeclaringClass()), method);
    }

    public static CachedMethod find(Method method) {
        CachedMethod[] methods = ReflectionCache.getCachedClass(method.getDeclaringClass()).getMethods();
        int i = Arrays.binarySearch(methods, method, comparator);
        if (i < 0) {
            return null;
        }
        return methods[i];
    }

    @Override
    protected Class[] getPT() {
        return this.cachedMethod.getParameterTypes();
    }

    @Override
    public String getName() {
        return this.cachedMethod.getName();
    }

    @Override
    public String getDescriptor() {
        return BytecodeHelper.getMethodDescriptor(this.getReturnType(), this.getNativeParameterTypes());
    }

    @Override
    public CachedClass getDeclaringClass() {
        return this.cachedClass;
    }

    @Override
    public final Object invoke(Object object, Object[] arguments) {
        try {
            return this.cachedMethod.invoke(object, arguments);
        }
        catch (IllegalArgumentException e) {
            throw new InvokerInvocationException(e);
        }
        catch (IllegalAccessException e) {
            throw new InvokerInvocationException(e);
        }
        catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw cause instanceof RuntimeException && !(cause instanceof MissingMethodException) ? (RuntimeException)cause : new InvokerInvocationException(e);
        }
    }

    public ParameterTypes getParamTypes() {
        return null;
    }

    @Override
    public Class getReturnType() {
        return this.cachedMethod.getReturnType();
    }

    public int getParamsCount() {
        return this.getParameterTypes().length;
    }

    @Override
    public int getModifiers() {
        return this.cachedMethod.getModifiers();
    }

    @Override
    public String getSignature() {
        return this.getName() + this.getDescriptor();
    }

    public final Method setAccessible() {
        return this.cachedMethod;
    }

    @Override
    public boolean isStatic() {
        return MethodHelper.isStatic(this.cachedMethod);
    }

    public int compareTo(Object o) {
        if (o instanceof CachedMethod) {
            return this.compareToCachedMethod((CachedMethod)o);
        }
        return this.compareToMethod((Method)o);
    }

    private int compareToCachedMethod(CachedMethod other) {
        CachedClass[] otherParams;
        if (other == null) {
            return -1;
        }
        int strComp = this.getName().compareTo(other.getName());
        if (strComp != 0) {
            return strComp;
        }
        int retComp = this.getReturnType().getName().compareTo(other.getReturnType().getName());
        if (retComp != 0) {
            return retComp;
        }
        CachedClass[] params = this.getParameterTypes();
        int pd = params.length - (otherParams = other.getParameterTypes()).length;
        if (pd != 0) {
            return pd;
        }
        for (int i = 0; i != params.length; ++i) {
            int nameComp = params[i].getName().compareTo(otherParams[i].getName());
            if (nameComp == 0) continue;
            return nameComp;
        }
        int classComp = this.cachedClass.toString().compareTo(other.getDeclaringClass().toString());
        if (classComp != 0) {
            return classComp;
        }
        throw new RuntimeException("Should never happen");
    }

    private int compareToMethod(Method other) {
        Class<?>[] mparams;
        if (other == null) {
            return -1;
        }
        int strComp = this.getName().compareTo(other.getName());
        if (strComp != 0) {
            return strComp;
        }
        int retComp = this.getReturnType().getName().compareTo(other.getReturnType().getName());
        if (retComp != 0) {
            return retComp;
        }
        CachedClass[] params = this.getParameterTypes();
        int pd = params.length - (mparams = other.getParameterTypes()).length;
        if (pd != 0) {
            return pd;
        }
        for (int i = 0; i != params.length; ++i) {
            int nameComp = params[i].getName().compareTo(mparams[i].getName());
            if (nameComp == 0) continue;
            return nameComp;
        }
        return 0;
    }

    public boolean equals(Object o) {
        return o instanceof CachedMethod && this.cachedMethod.equals(((CachedMethod)o).cachedMethod) || o instanceof Method && this.cachedMethod.equals(o);
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = this.cachedMethod.hashCode();
            if (this.hashCode == 0) {
                this.hashCode = -889274690;
            }
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        return this.cachedMethod.toString();
    }

    private static Constructor getConstructor(SoftReference<Constructor> ref) {
        if (ref == null) {
            return null;
        }
        return ref.get();
    }

    public CallSite createPogoMetaMethodSite(CallSite site, MetaClassImpl metaClass, Class[] params) {
        if (!this.skipCompiled) {
            Constructor constr = CachedMethod.getConstructor(this.pogoCallSiteConstructor);
            if (constr == null) {
                if (CallSiteGenerator.isCompilable(this)) {
                    constr = CallSiteGenerator.compilePogoMethod(this);
                }
                if (constr != null) {
                    this.pogoCallSiteConstructor = new SoftReference<Constructor>(constr);
                } else {
                    this.skipCompiled = true;
                }
            }
            if (constr != null) {
                try {
                    return (CallSite)constr.newInstance(site, metaClass, this, params, constr);
                }
                catch (Error e) {
                    this.skipCompiled = true;
                    throw e;
                }
                catch (Throwable e) {
                    this.skipCompiled = true;
                }
            }
        }
        return new PogoMetaMethodSite.PogoCachedMethodSiteNoUnwrapNoCoerce(site, metaClass, this, params);
    }

    public CallSite createPojoMetaMethodSite(CallSite site, MetaClassImpl metaClass, Class[] params) {
        if (!this.skipCompiled) {
            Constructor constr = CachedMethod.getConstructor(this.pojoCallSiteConstructor);
            if (constr == null) {
                if (CallSiteGenerator.isCompilable(this)) {
                    constr = CallSiteGenerator.compilePojoMethod(this);
                }
                if (constr != null) {
                    this.pojoCallSiteConstructor = new SoftReference<Constructor>(constr);
                } else {
                    this.skipCompiled = true;
                }
            }
            if (constr != null) {
                try {
                    return (CallSite)constr.newInstance(site, metaClass, this, params, constr);
                }
                catch (Error e) {
                    this.skipCompiled = true;
                    throw e;
                }
                catch (Throwable e) {
                    this.skipCompiled = true;
                }
            }
        }
        return new PojoMetaMethodSite.PojoCachedMethodSiteNoUnwrapNoCoerce(site, metaClass, (MetaMethod)this, params);
    }

    public CallSite createStaticMetaMethodSite(CallSite site, MetaClassImpl metaClass, Class[] params) {
        if (!this.skipCompiled) {
            Constructor constr = CachedMethod.getConstructor(this.staticCallSiteConstructor);
            if (constr == null) {
                if (CallSiteGenerator.isCompilable(this)) {
                    constr = CallSiteGenerator.compileStaticMethod(this);
                }
                if (constr != null) {
                    this.staticCallSiteConstructor = new SoftReference<Constructor>(constr);
                } else {
                    this.skipCompiled = true;
                }
            }
            if (constr != null) {
                try {
                    return (CallSite)constr.newInstance(site, metaClass, this, params, constr);
                }
                catch (Error e) {
                    this.skipCompiled = true;
                    throw e;
                }
                catch (Throwable e) {
                    this.skipCompiled = true;
                }
            }
        }
        return new StaticMetaMethodSite.StaticMetaMethodSiteNoUnwrapNoCoerce(site, metaClass, (MetaMethod)this, params);
    }

    public Method getCachedMethod() {
        return this.cachedMethod;
    }

    private static class MyComparator
    implements Comparator,
    Serializable {
        private static final long serialVersionUID = 8909277090690131302L;

        private MyComparator() {
        }

        public int compare(Object o1, Object o2) {
            if (o1 instanceof CachedMethod) {
                return ((CachedMethod)o1).compareTo(o2);
            }
            if (o2 instanceof CachedMethod) {
                return -((CachedMethod)o2).compareTo(o1);
            }
            throw new ClassCastException("One of the two comparables must be a CachedMethod");
        }
    }
}

