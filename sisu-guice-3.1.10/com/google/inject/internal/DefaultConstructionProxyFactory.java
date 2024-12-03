/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.aopalliance.intercept.MethodInterceptor
 */
package com.google.inject.internal;

import com.google.common.collect.ImmutableMap;
import com.google.inject.internal.BytecodeGen;
import com.google.inject.internal.ConstructionProxy;
import com.google.inject.internal.ConstructionProxyFactory;
import com.google.inject.internal.cglib.core.$CodeGenerationException;
import com.google.inject.internal.cglib.reflect.$FastConstructor;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class DefaultConstructionProxyFactory<T>
implements ConstructionProxyFactory<T> {
    private final InjectionPoint injectionPoint;

    DefaultConstructionProxyFactory(InjectionPoint injectionPoint) {
        this.injectionPoint = injectionPoint;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public ConstructionProxy<T> create() {
        final Constructor constructor = (Constructor)this.injectionPoint.getMember();
        if (Modifier.isPublic(constructor.getModifiers())) {
            Class classToConstruct = constructor.getDeclaringClass();
            try {
                final $FastConstructor fastConstructor = BytecodeGen.newFastClass(classToConstruct, BytecodeGen.Visibility.forMember(constructor)).getConstructor(constructor);
                return new ConstructionProxy<T>(){

                    @Override
                    public T newInstance(Object ... arguments) throws InvocationTargetException {
                        return fastConstructor.newInstance(arguments);
                    }

                    @Override
                    public InjectionPoint getInjectionPoint() {
                        return DefaultConstructionProxyFactory.this.injectionPoint;
                    }

                    @Override
                    public Constructor<T> getConstructor() {
                        return constructor;
                    }

                    @Override
                    public ImmutableMap<Method, List<MethodInterceptor>> getMethodInterceptors() {
                        return ImmutableMap.of();
                    }
                };
            }
            catch ($CodeGenerationException $CodeGenerationException) {
                if (Modifier.isPublic(classToConstruct.getModifiers())) return new ConstructionProxy<T>(){

                    @Override
                    public T newInstance(Object ... arguments) throws InvocationTargetException {
                        try {
                            return constructor.newInstance(arguments);
                        }
                        catch (InstantiationException e) {
                            throw new AssertionError((Object)e);
                        }
                        catch (IllegalAccessException e) {
                            throw new AssertionError((Object)e);
                        }
                    }

                    @Override
                    public InjectionPoint getInjectionPoint() {
                        return DefaultConstructionProxyFactory.this.injectionPoint;
                    }

                    @Override
                    public Constructor<T> getConstructor() {
                        return constructor;
                    }

                    @Override
                    public ImmutableMap<Method, List<MethodInterceptor>> getMethodInterceptors() {
                        return ImmutableMap.of();
                    }
                };
                constructor.setAccessible(true);
                return new /* invalid duplicate definition of identical inner class */;
            }
        } else {
            constructor.setAccessible(true);
        }
        return new /* invalid duplicate definition of identical inner class */;
    }
}

