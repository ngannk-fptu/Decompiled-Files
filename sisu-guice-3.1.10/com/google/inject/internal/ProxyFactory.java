/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.aopalliance.intercept.MethodInterceptor
 */
package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.internal.BytecodeGen;
import com.google.inject.internal.ConstructionProxy;
import com.google.inject.internal.ConstructionProxyFactory;
import com.google.inject.internal.DefaultConstructionProxyFactory;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InterceptorStackCallback;
import com.google.inject.internal.MethodAspect;
import com.google.inject.internal.cglib.core.$MethodWrapper;
import com.google.inject.internal.cglib.proxy.$Callback;
import com.google.inject.internal.cglib.proxy.$CallbackFilter;
import com.google.inject.internal.cglib.proxy.$Enhancer;
import com.google.inject.internal.cglib.proxy.$MethodInterceptor;
import com.google.inject.internal.cglib.proxy.$NoOp;
import com.google.inject.internal.cglib.reflect.$FastClass;
import com.google.inject.internal.cglib.reflect.$FastConstructor;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.aopalliance.intercept.MethodInterceptor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ProxyFactory<T>
implements ConstructionProxyFactory<T> {
    private static final Logger logger = Logger.getLogger(ProxyFactory.class.getName());
    private final InjectionPoint injectionPoint;
    private final ImmutableMap<Method, List<MethodInterceptor>> interceptors;
    private final Class<T> declaringClass;
    private final List<Method> methods;
    private final $Callback[] callbacks;
    private BytecodeGen.Visibility visibility = BytecodeGen.Visibility.PUBLIC;

    ProxyFactory(InjectionPoint injectionPoint, Iterable<MethodAspect> methodAspects) {
        this.injectionPoint = injectionPoint;
        Constructor constructor = (Constructor)injectionPoint.getMember();
        this.declaringClass = constructor.getDeclaringClass();
        ArrayList applicableAspects = Lists.newArrayList();
        for (MethodAspect methodAspect : methodAspects) {
            if (!methodAspect.matches(this.declaringClass)) continue;
            applicableAspects.add(methodAspect);
        }
        if (applicableAspects.isEmpty()) {
            this.interceptors = ImmutableMap.of();
            this.methods = ImmutableList.of();
            this.callbacks = null;
            return;
        }
        this.methods = Lists.newArrayList();
        $Enhancer.getMethods(this.declaringClass, null, this.methods);
        ArrayList methodInterceptorsPairs = Lists.newArrayList();
        for (Method method : this.methods) {
            methodInterceptorsPairs.add(new MethodInterceptorsPair(method));
        }
        boolean anyMatched = false;
        for (MethodAspect methodAspect : applicableAspects) {
            for (MethodInterceptorsPair pair : methodInterceptorsPairs) {
                if (!methodAspect.matches(pair.method)) continue;
                if (pair.method.isSynthetic()) {
                    logger.log(Level.WARNING, "Method [{0}] is synthetic and is being intercepted by {1}. This could indicate a bug.  The method may be intercepted twice, or may not be intercepted at all.", new Object[]{pair.method, methodAspect.interceptors()});
                }
                this.visibility = this.visibility.and(BytecodeGen.Visibility.forMember(pair.method));
                pair.addAll(methodAspect.interceptors());
                anyMatched = true;
            }
        }
        if (!anyMatched) {
            this.interceptors = ImmutableMap.of();
            this.callbacks = null;
            return;
        }
        ImmutableMap.Builder interceptorsMapBuilder = null;
        this.callbacks = new $Callback[this.methods.size()];
        for (int i = 0; i < this.methods.size(); ++i) {
            MethodInterceptorsPair pair = (MethodInterceptorsPair)methodInterceptorsPairs.get(i);
            if (!pair.hasInterceptors()) {
                this.callbacks[i] = $NoOp.INSTANCE;
                continue;
            }
            if (interceptorsMapBuilder == null) {
                interceptorsMapBuilder = ImmutableMap.builder();
            }
            ImmutableList deDuplicated = ImmutableSet.copyOf(pair.interceptors).asList();
            interceptorsMapBuilder.put((Object)pair.method, (Object)deDuplicated);
            this.callbacks[i] = new InterceptorStackCallback(pair.method, (List<MethodInterceptor>)deDuplicated);
        }
        this.interceptors = interceptorsMapBuilder != null ? interceptorsMapBuilder.build() : ImmutableMap.of();
    }

    public ImmutableMap<Method, List<MethodInterceptor>> getInterceptors() {
        return this.interceptors;
    }

    @Override
    public ConstructionProxy<T> create() throws ErrorsException {
        if (this.interceptors.isEmpty()) {
            return new DefaultConstructionProxyFactory(this.injectionPoint).create();
        }
        Class[] callbackTypes = new Class[this.callbacks.length];
        for (int i = 0; i < this.callbacks.length; ++i) {
            callbackTypes[i] = this.callbacks[i] == $NoOp.INSTANCE ? $NoOp.class : $MethodInterceptor.class;
        }
        try {
            $Enhancer enhancer = BytecodeGen.newEnhancer(this.declaringClass, this.visibility);
            enhancer.setCallbackFilter(new IndicesCallbackFilter(this.methods));
            enhancer.setCallbackTypes(callbackTypes);
            return new ProxyConstructor(enhancer, this.injectionPoint, this.callbacks, this.interceptors);
        }
        catch (Throwable e) {
            throw new Errors().errorEnhancingClass(this.declaringClass, e).toException();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ProxyConstructor<T>
    implements ConstructionProxy<T> {
        final Class<?> enhanced;
        final InjectionPoint injectionPoint;
        final Constructor<T> constructor;
        final $Callback[] callbacks;
        final $FastConstructor fastConstructor;
        final ImmutableMap<Method, List<MethodInterceptor>> methodInterceptors;

        ProxyConstructor($Enhancer enhancer, InjectionPoint injectionPoint, $Callback[] callbacks, ImmutableMap<Method, List<MethodInterceptor>> methodInterceptors) {
            this.enhanced = enhancer.createClass();
            this.injectionPoint = injectionPoint;
            this.constructor = (Constructor)injectionPoint.getMember();
            this.callbacks = callbacks;
            this.methodInterceptors = methodInterceptors;
            $FastClass fastClass = BytecodeGen.newFastClass(this.enhanced, BytecodeGen.Visibility.forMember(this.constructor));
            this.fastConstructor = fastClass.getConstructor(this.constructor.getParameterTypes());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public T newInstance(Object ... arguments) throws InvocationTargetException {
            $Enhancer.registerCallbacks(this.enhanced, this.callbacks);
            try {
                Object object = this.fastConstructor.newInstance(arguments);
                return (T)object;
            }
            finally {
                $Enhancer.registerCallbacks(this.enhanced, null);
            }
        }

        @Override
        public InjectionPoint getInjectionPoint() {
            return this.injectionPoint;
        }

        @Override
        public Constructor<T> getConstructor() {
            return this.constructor;
        }

        @Override
        public ImmutableMap<Method, List<MethodInterceptor>> getMethodInterceptors() {
            return this.methodInterceptors;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class IndicesCallbackFilter
    implements $CallbackFilter {
        final Map<Object, Integer> indices;
        final int hashCode;

        IndicesCallbackFilter(List<Method> methods) {
            HashMap indices = Maps.newHashMap();
            for (int i = 0; i < methods.size(); ++i) {
                indices.put($MethodWrapper.create(methods.get(i)), i);
            }
            this.indices = indices;
            this.hashCode = ((Object)indices).hashCode();
        }

        @Override
        public int accept(Method method) {
            return this.indices.get($MethodWrapper.create(method));
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof IndicesCallbackFilter && ((Object)((IndicesCallbackFilter)o).indices).equals(this.indices);
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class MethodInterceptorsPair {
        final Method method;
        List<MethodInterceptor> interceptors;

        MethodInterceptorsPair(Method method) {
            this.method = method;
        }

        void addAll(List<MethodInterceptor> interceptors) {
            if (this.interceptors == null) {
                this.interceptors = Lists.newArrayList();
            }
            this.interceptors.addAll(interceptors);
        }

        boolean hasInterceptors() {
            return this.interceptors != null;
        }
    }
}

