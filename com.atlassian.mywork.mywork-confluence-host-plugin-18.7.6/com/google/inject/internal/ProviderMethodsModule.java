/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ProviderMethod;
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Message;
import com.google.inject.util.Modules;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ProviderMethodsModule
implements Module {
    private final Object delegate;
    private final TypeLiteral<?> typeLiteral;

    private ProviderMethodsModule(Object delegate) {
        this.delegate = $Preconditions.checkNotNull(delegate, "delegate");
        this.typeLiteral = TypeLiteral.get(this.delegate.getClass());
    }

    public static Module forModule(Module module) {
        return ProviderMethodsModule.forObject(module);
    }

    public static Module forObject(Object object) {
        if (object instanceof ProviderMethodsModule) {
            return Modules.EMPTY_MODULE;
        }
        return new ProviderMethodsModule(object);
    }

    @Override
    public synchronized void configure(Binder binder) {
        for (ProviderMethod<?> providerMethod : this.getProviderMethods(binder)) {
            providerMethod.configure(binder);
        }
    }

    public List<ProviderMethod<?>> getProviderMethods(Binder binder) {
        ArrayList<ProviderMethod<?>> result = $Lists.newArrayList();
        for (Class<?> c = this.delegate.getClass(); c != Object.class; c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Provides.class)) continue;
                result.add(this.createProviderMethod(binder, method));
            }
        }
        return result;
    }

    <T> ProviderMethod<T> createProviderMethod(Binder binder, Method method) {
        Key<Object> key;
        binder = binder.withSource(method);
        Errors errors = new Errors(method);
        ArrayList<Dependency<?>> dependencies = $Lists.newArrayList();
        ArrayList<Provider<?>> parameterProviders = $Lists.newArrayList();
        List<TypeLiteral<?>> parameterTypes = this.typeLiteral.getParameterTypes(method);
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterTypes.size(); ++i) {
            key = this.getKey(errors, parameterTypes.get(i), method, parameterAnnotations[i]);
            if (key.equals(Key.get(Logger.class))) {
                Key<Logger> loggerKey = Key.get(Logger.class, UniqueAnnotations.create());
                binder.bind(loggerKey).toProvider(new LogProvider(method));
                key = loggerKey;
            }
            dependencies.add(Dependency.get(key));
            parameterProviders.add(binder.getProvider(key));
        }
        TypeLiteral<?> returnType = this.typeLiteral.getReturnType(method);
        key = this.getKey(errors, returnType, method, method.getAnnotations());
        Class<? extends Annotation> scopeAnnotation = Annotations.findScopeAnnotation(errors, method.getAnnotations());
        for (Message message : errors.getMessages()) {
            binder.addError(message);
        }
        return new ProviderMethod(key, method, this.delegate, $ImmutableSet.copyOf(dependencies), parameterProviders, scopeAnnotation);
    }

    <T> Key<T> getKey(Errors errors, TypeLiteral<T> type, Member member, Annotation[] annotations) {
        Annotation bindingAnnotation = Annotations.findBindingAnnotation(errors, member, annotations);
        return bindingAnnotation == null ? Key.get(type) : Key.get(type, bindingAnnotation);
    }

    public boolean equals(Object o) {
        return o instanceof ProviderMethodsModule && ((ProviderMethodsModule)o).delegate == this.delegate;
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class LogProvider
    implements Provider<Logger> {
        private final String name;

        public LogProvider(Method method) {
            this.name = method.getDeclaringClass().getName() + "." + method.getName();
        }

        @Override
        public Logger get() {
            return Logger.getLogger(this.name);
        }
    }
}

