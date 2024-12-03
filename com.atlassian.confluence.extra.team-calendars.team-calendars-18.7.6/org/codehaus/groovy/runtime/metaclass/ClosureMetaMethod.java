/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.Closure;
import groovy.lang.ClosureInvokingMethod;
import groovy.lang.MetaMethod;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MethodClosure;

public class ClosureMetaMethod
extends MetaMethod
implements ClosureInvokingMethod {
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private final Closure callable;
    private final CachedMethod doCall;
    private final String name;
    private final CachedClass declaringClass;

    public ClosureMetaMethod(String name, Closure c, CachedMethod doCall) {
        this(name, c.getOwner().getClass(), c, doCall);
    }

    public ClosureMetaMethod(String name, Class declaringClass, Closure c, CachedMethod doCall) {
        super(doCall.getNativeParameterTypes());
        this.name = name;
        this.callable = c;
        this.doCall = doCall;
        this.declaringClass = ReflectionCache.getCachedClass(declaringClass);
    }

    @Override
    public int getModifiers() {
        return 1;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class getReturnType() {
        return Object.class;
    }

    @Override
    public CachedClass getDeclaringClass() {
        return this.declaringClass;
    }

    @Override
    public Object invoke(Object object, Object[] arguments) {
        Closure cloned = (Closure)this.callable.clone();
        cloned.setDelegate(object);
        arguments = this.coerceArgumentsToClasses(arguments);
        return this.doCall.invoke(cloned, arguments);
    }

    @Override
    public Closure getClosure() {
        return this.callable;
    }

    public static List<MetaMethod> createMethodList(String name, Class declaringClass, Closure closure) {
        ArrayList<MetaMethod> res = new ArrayList<MetaMethod>();
        if (closure instanceof MethodClosure) {
            MethodClosure methodClosure = (MethodClosure)closure;
            Class<?> owner = closure.getOwner();
            Class<?> ownerClass = owner instanceof Class ? owner : owner.getClass();
            for (CachedMethod method : ReflectionCache.getCachedClass(ownerClass).getMethods()) {
                if (!method.getName().equals(methodClosure.getMethod())) continue;
                MethodClosureMetaMethod metaMethod = new MethodClosureMetaMethod(name, declaringClass, closure, method);
                res.add(ClosureMetaMethod.adjustParamTypesForStdMethods(metaMethod, name));
            }
        } else if (closure instanceof GeneratedClosure) {
            for (CachedMethod method : ReflectionCache.getCachedClass(closure.getClass()).getMethods()) {
                if (!method.getName().equals("doCall")) continue;
                ClosureMetaMethod metaMethod = new ClosureMetaMethod(name, declaringClass, closure, method);
                res.add(ClosureMetaMethod.adjustParamTypesForStdMethods(metaMethod, name));
            }
        } else {
            AnonymousMetaMethod metaMethod = new AnonymousMetaMethod(closure, name, declaringClass);
            res.add(ClosureMetaMethod.adjustParamTypesForStdMethods(metaMethod, name));
        }
        return res;
    }

    private static MetaMethod adjustParamTypesForStdMethods(MetaMethod metaMethod, String methodName) {
        Class[] nativeParamTypes = metaMethod.getNativeParameterTypes();
        Class[] classArray = nativeParamTypes = nativeParamTypes != null ? nativeParamTypes : EMPTY_CLASS_ARRAY;
        if ("methodMissing".equals(methodName) && nativeParamTypes.length == 2 && nativeParamTypes[0] != String.class) {
            nativeParamTypes[0] = String.class;
        }
        return metaMethod;
    }

    public CachedMethod getDoCall() {
        return this.doCall;
    }

    public static ClosureMetaMethod copy(ClosureMetaMethod closureMethod) {
        if (closureMethod instanceof MethodClosureMetaMethod) {
            return new MethodClosureMetaMethod(closureMethod.getName(), closureMethod.getDeclaringClass().getTheClass(), closureMethod.getClosure(), closureMethod.getDoCall());
        }
        return new ClosureMetaMethod(closureMethod.getName(), closureMethod.getDeclaringClass().getTheClass(), closureMethod.getClosure(), closureMethod.getDoCall());
    }

    static class AnonymousMetaMethod
    extends MetaMethod {
        private final Closure closure;
        private final String name;
        private final Class declaringClass;

        public AnonymousMetaMethod(Closure closure, String name, Class declaringClass) {
            super(closure.getParameterTypes());
            this.closure = closure;
            this.name = name;
            this.declaringClass = declaringClass;
        }

        @Override
        public int getModifiers() {
            return 1;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Class getReturnType() {
            return Object.class;
        }

        @Override
        public CachedClass getDeclaringClass() {
            return ReflectionCache.getCachedClass(this.declaringClass);
        }

        @Override
        public Object invoke(Object object, Object[] arguments) {
            Closure cloned = (Closure)this.closure.clone();
            cloned.setDelegate(object);
            arguments = this.coerceArgumentsToClasses(arguments);
            return InvokerHelper.invokeMethod(cloned, "call", arguments);
        }
    }

    private static class MethodClosureMetaMethod
    extends ClosureMetaMethod {
        public MethodClosureMetaMethod(String name, Class declaringClass, Closure closure, CachedMethod method) {
            super(name, declaringClass, closure, method);
        }

        @Override
        public Object invoke(Object object, Object[] arguments) {
            return this.getDoCall().invoke(this.getClosure().getOwner(), arguments);
        }
    }
}

