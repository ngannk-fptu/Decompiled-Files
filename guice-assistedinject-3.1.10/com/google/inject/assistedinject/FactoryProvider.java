/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.inject.ConfigurationException
 *  com.google.inject.Inject
 *  com.google.inject.Injector
 *  com.google.inject.Key
 *  com.google.inject.Provider
 *  com.google.inject.TypeLiteral
 *  com.google.inject.internal.Annotations
 *  com.google.inject.internal.BytecodeGen
 *  com.google.inject.internal.Errors
 *  com.google.inject.internal.ErrorsException
 *  com.google.inject.spi.Dependency
 *  com.google.inject.spi.HasDependencies
 *  com.google.inject.spi.Message
 */
package com.google.inject.assistedinject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedConstructor;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.assistedinject.BindingCollector;
import com.google.inject.assistedinject.FactoryProvider2;
import com.google.inject.assistedinject.Parameter;
import com.google.inject.assistedinject.ParameterListKey;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.BytecodeGen;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.Message;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public class FactoryProvider<F>
implements Provider<F>,
HasDependencies {
    private Injector injector;
    private final TypeLiteral<F> factoryType;
    private final Map<Method, AssistedConstructor<?>> factoryMethodToConstructor;

    public static <F> Provider<F> newFactory(Class<F> factoryType, Class<?> implementationType) {
        return FactoryProvider.newFactory(TypeLiteral.get(factoryType), TypeLiteral.get(implementationType));
    }

    public static <F> Provider<F> newFactory(TypeLiteral<F> factoryType, TypeLiteral<?> implementationType) {
        Map<Method, AssistedConstructor<?>> factoryMethodToConstructor = FactoryProvider.createMethodMapping(factoryType, implementationType);
        if (!factoryMethodToConstructor.isEmpty()) {
            return new FactoryProvider<F>(factoryType, factoryMethodToConstructor);
        }
        BindingCollector collector = new BindingCollector();
        Errors errors = new Errors();
        Key implementationKey = Key.get(implementationType);
        if (implementationType != null) {
            try {
                for (Method method : factoryType.getRawType().getMethods()) {
                    Key returnType = Annotations.getKey((TypeLiteral)factoryType.getReturnType(method), (Member)method, (Annotation[])method.getAnnotations(), (Errors)errors);
                    if (implementationKey.equals((Object)returnType)) continue;
                    collector.addBinding(returnType, implementationType);
                }
            }
            catch (ErrorsException e) {
                throw new ConfigurationException((Iterable)e.getErrors().getMessages());
            }
        }
        return new FactoryProvider2(Key.get(factoryType), collector);
    }

    private FactoryProvider(TypeLiteral<F> factoryType, Map<Method, AssistedConstructor<?>> factoryMethodToConstructor) {
        this.factoryType = factoryType;
        this.factoryMethodToConstructor = factoryMethodToConstructor;
        this.checkDeclaredExceptionsMatch();
    }

    @Inject
    void setInjectorAndCheckUnboundParametersAreInjectable(Injector injector) {
        this.injector = injector;
        for (AssistedConstructor<?> c : this.factoryMethodToConstructor.values()) {
            for (Parameter p : c.getAllParameters()) {
                if (p.isProvidedByFactory() || this.paramCanBeInjected(p, injector)) continue;
                throw FactoryProvider.newConfigurationException("Parameter of type '%s' is not injectable or annotated with @Assisted for Constructor '%s'", p, c);
            }
        }
    }

    private void checkDeclaredExceptionsMatch() {
        for (Map.Entry<Method, AssistedConstructor<?>> entry : this.factoryMethodToConstructor.entrySet()) {
            for (Class<?> constructorException : entry.getValue().getDeclaredExceptions()) {
                if (this.isConstructorExceptionCompatibleWithFactoryExeception(constructorException, entry.getKey().getExceptionTypes())) continue;
                throw FactoryProvider.newConfigurationException("Constructor %s declares an exception, but no compatible exception is thrown by the factory method %s", entry.getValue(), entry.getKey());
            }
        }
    }

    private boolean isConstructorExceptionCompatibleWithFactoryExeception(Class<?> constructorException, Class<?>[] factoryExceptions) {
        for (Class<?> factoryException : factoryExceptions) {
            if (!factoryException.isAssignableFrom(constructorException)) continue;
            return true;
        }
        return false;
    }

    private boolean paramCanBeInjected(Parameter parameter, Injector injector) {
        return parameter.isBound(injector);
    }

    private static Map<Method, AssistedConstructor<?>> createMethodMapping(TypeLiteral<?> factoryType, TypeLiteral<?> implementationType) {
        ArrayList constructors = Lists.newArrayList();
        for (Constructor<?> constructor : implementationType.getRawType().getDeclaredConstructors()) {
            if (constructor.getAnnotation(AssistedInject.class) == null) continue;
            AssistedConstructor assistedConstructor = new AssistedConstructor(constructor, implementationType.getParameterTypes(constructor));
            constructors.add(assistedConstructor);
        }
        if (constructors.isEmpty()) {
            return ImmutableMap.of();
        }
        Method[] factoryMethods = factoryType.getRawType().getMethods();
        if (constructors.size() != factoryMethods.length) {
            throw FactoryProvider.newConfigurationException("Constructor mismatch: %s has %s @AssistedInject constructors, factory %s has %s creation methods", implementationType, constructors.size(), factoryType, factoryMethods.length);
        }
        HashMap paramsToConstructor = Maps.newHashMap();
        for (AssistedConstructor c : constructors) {
            if (paramsToConstructor.containsKey(c.getAssistedParameters())) {
                throw new RuntimeException("Duplicate constructor, " + c);
            }
            paramsToConstructor.put(c.getAssistedParameters(), c);
        }
        HashMap result = Maps.newHashMap();
        for (Method method : factoryMethods) {
            if (!method.getReturnType().isAssignableFrom(implementationType.getRawType())) {
                throw FactoryProvider.newConfigurationException("Return type of method %s is not assignable from %s", method, implementationType);
            }
            ArrayList parameterTypes = Lists.newArrayList();
            for (TypeLiteral parameterType : factoryType.getParameterTypes((Member)method)) {
                parameterTypes.add(parameterType.getType());
            }
            ParameterListKey methodParams = new ParameterListKey(parameterTypes);
            if (!paramsToConstructor.containsKey(methodParams)) {
                throw FactoryProvider.newConfigurationException("%s has no @AssistInject constructor that takes the @Assisted parameters %s in that order. @AssistInject constructors are %s", implementationType, methodParams, paramsToConstructor.values());
            }
            method.getParameterAnnotations();
            Annotation[][] arr$ = method.getParameterAnnotations();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; ++i$) {
                Annotation[] parameterAnnotations;
                for (Annotation parameterAnnotation : parameterAnnotations = arr$[i$]) {
                    if (parameterAnnotation.annotationType() != Assisted.class) continue;
                    throw FactoryProvider.newConfigurationException("Factory method %s has an @Assisted parameter, which is incompatible with the deprecated @AssistedInject annotation. Please replace @AssistedInject with @Inject on the %s constructor.", method, implementationType);
                }
            }
            AssistedConstructor matchingConstructor = (AssistedConstructor)paramsToConstructor.remove(methodParams);
            result.put(method, matchingConstructor);
        }
        return result;
    }

    public Set<Dependency<?>> getDependencies() {
        ArrayList dependencies = Lists.newArrayList();
        for (AssistedConstructor<?> constructor : this.factoryMethodToConstructor.values()) {
            for (Parameter parameter : constructor.getAllParameters()) {
                if (parameter.isProvidedByFactory()) continue;
                dependencies.add(Dependency.get(parameter.getPrimaryBindingKey()));
            }
        }
        return ImmutableSet.copyOf((Collection)dependencies);
    }

    public F get() {
        InvocationHandler invocationHandler = new InvocationHandler(){

            @Override
            public Object invoke(Object proxy, Method method, Object[] creationArgs) throws Throwable {
                if (method.getDeclaringClass().equals(Object.class)) {
                    return method.invoke((Object)this, creationArgs);
                }
                AssistedConstructor constructor = (AssistedConstructor)FactoryProvider.this.factoryMethodToConstructor.get(method);
                Object[] constructorArgs = this.gatherArgsForConstructor(constructor, creationArgs);
                Object objectToReturn = constructor.newInstance(constructorArgs);
                FactoryProvider.this.injector.injectMembers(objectToReturn);
                return objectToReturn;
            }

            public Object[] gatherArgsForConstructor(AssistedConstructor<?> constructor, Object[] factoryArgs) {
                int numParams = constructor.getAllParameters().size();
                int argPosition = 0;
                Object[] result = new Object[numParams];
                for (int i = 0; i < numParams; ++i) {
                    Parameter parameter = constructor.getAllParameters().get(i);
                    if (parameter.isProvidedByFactory()) {
                        result[i] = factoryArgs[argPosition];
                        ++argPosition;
                        continue;
                    }
                    result[i] = parameter.getValue(FactoryProvider.this.injector);
                }
                return result;
            }
        };
        Class factoryRawType = this.factoryType.getRawType();
        return (F)factoryRawType.cast(Proxy.newProxyInstance(BytecodeGen.getClassLoader((Class)factoryRawType), new Class[]{factoryRawType}, invocationHandler));
    }

    private static ConfigurationException newConfigurationException(String format, Object ... args) {
        return new ConfigurationException((Iterable)ImmutableSet.of((Object)new Message(Errors.format((String)format, (Object[])args))));
    }
}

