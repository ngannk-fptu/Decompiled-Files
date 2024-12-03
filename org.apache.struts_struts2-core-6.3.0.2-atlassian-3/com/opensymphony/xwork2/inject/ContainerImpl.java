/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.inject.ConstructionContext;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.DependencyException;
import com.opensymphony.xwork2.inject.ExternalContext;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.inject.InternalContext;
import com.opensymphony.xwork2.inject.InternalFactory;
import com.opensymphony.xwork2.inject.Key;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.inject.util.ReferenceCache;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ReflectPermission;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ContainerImpl
implements Container {
    final Map<Key<?>, InternalFactory<?>> factories;
    final Map<Class<?>, Set<String>> factoryNamesByType;
    final Map<Class<?>, List<Injector>> injectors = new ReferenceCache<Class<?>, List<Injector>>(){

        @Override
        protected List<Injector> create(Class<?> key) {
            ArrayList<Injector> injectors = new ArrayList<Injector>();
            ContainerImpl.this.addInjectors(key, injectors);
            return injectors;
        }
    };
    Map<Class<?>, ConstructorInjector<?>> constructors = new ReferenceCache<Class<?>, ConstructorInjector<?>>(){

        @Override
        protected ConstructorInjector<?> create(Class<?> implementation) {
            return new ConstructorInjector(ContainerImpl.this, implementation);
        }
    };
    ThreadLocal<Object[]> localContext = ThreadLocal.withInitial(() -> new Object[1]);
    final ThreadLocal<Object> localScopeStrategy = new ThreadLocal();

    ContainerImpl(Map<Key<?>, InternalFactory<?>> factories) {
        this.factories = factories;
        HashMap<Class, Set> map = new HashMap<Class, Set>();
        for (Key<?> key : factories.keySet()) {
            Set names = map.computeIfAbsent(key.getType(), k -> new HashSet());
            names.add(key.getName());
        }
        for (Map.Entry entry : map.entrySet()) {
            entry.setValue(Collections.unmodifiableSet((Set)entry.getValue()));
        }
        this.factoryNamesByType = Collections.unmodifiableMap(map);
    }

    <T> InternalFactory<? extends T> getFactory(Key<T> key) {
        return this.factories.get(key);
    }

    void addInjectors(Class<?> clazz, List<Injector> injectors) {
        if (clazz == Object.class) {
            return;
        }
        this.addInjectors(clazz.getSuperclass(), injectors);
        this.addInjectorsForFields(clazz.getDeclaredFields(), false, injectors);
        this.addInjectorsForMethods(clazz.getDeclaredMethods(), false, injectors);
    }

    void injectStatics(List<Class<?>> staticInjections) {
        ArrayList<Injector> injectors = new ArrayList<Injector>();
        for (Class<?> clazz : staticInjections) {
            this.addInjectorsForFields(clazz.getDeclaredFields(), true, injectors);
            this.addInjectorsForMethods(clazz.getDeclaredMethods(), true, injectors);
        }
        this.callInContext(context -> {
            for (Injector injector : injectors) {
                injector.inject(context, null);
            }
            return null;
        });
    }

    void addInjectorsForMethods(Method[] methods, boolean statics, List<Injector> injectors) {
        this.addInjectorsForMembers(Arrays.asList(methods), statics, injectors, MethodInjector::new);
    }

    void addInjectorsForFields(Field[] fields, boolean statics, List<Injector> injectors) {
        this.addInjectorsForMembers(Arrays.asList(fields), statics, injectors, FieldInjector::new);
    }

    <M extends Member & AnnotatedElement> void addInjectorsForMembers(List<M> members, boolean statics, List<Injector> injectors, InjectorFactory<M> injectorFactory) {
        for (Member member : members) {
            Inject inject;
            if (ContainerImpl.isStatic(member) != statics || (inject = ((AnnotatedElement)((Object)member)).getAnnotation(Inject.class)) == null) continue;
            try {
                injectors.add(injectorFactory.create(this, member, inject.value()));
            }
            catch (MissingDependencyException e) {
                if (!inject.required()) continue;
                throw new DependencyException(e);
            }
        }
    }

    private static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    private static boolean isPublicForReflection(Member member) {
        return Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(member.getDeclaringClass().getModifiers());
    }

    <M extends AccessibleObject> ParameterInjector<?>[] getParametersInjectors(M member, Annotation[][] annotations, Class<?>[] parameterTypes, String defaultName) throws MissingDependencyException {
        ArrayList parameterInjectors = new ArrayList();
        Iterator annotationsIterator = Arrays.asList(annotations).iterator();
        for (Class<?> parameterType : parameterTypes) {
            Inject annotation = this.findInject((Annotation[])annotationsIterator.next());
            String name = annotation == null ? defaultName : annotation.value();
            Key<?> key = Key.newInstance(parameterType, name);
            parameterInjectors.add(this.createParameterInjector(key, (Member)((Object)member)));
        }
        return this.toArray(parameterInjectors);
    }

    <T> ParameterInjector<T> createParameterInjector(Key<T> key, Member member) throws MissingDependencyException {
        InternalFactory<T> factory = this.getFactory(key);
        if (factory == null) {
            throw new MissingDependencyException("No mapping found for dependency " + key + " in " + member + ".");
        }
        ExternalContext<T> externalContext = ExternalContext.newInstance(member, key, this);
        return new ParameterInjector<T>(externalContext, factory);
    }

    private ParameterInjector<?>[] toArray(List<ParameterInjector<?>> parameterInjections) {
        return parameterInjections.toArray(new ParameterInjector[0]);
    }

    Inject findInject(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() != Inject.class) continue;
            return (Inject)annotation;
        }
        return null;
    }

    private static Object[] getParameters(Member member, InternalContext context, ParameterInjector<?>[] parameterInjectors) {
        if (parameterInjectors == null) {
            return null;
        }
        Object[] parameters = new Object[parameterInjectors.length];
        for (int i = 0; i < parameters.length; ++i) {
            parameters[i] = parameterInjectors[i].inject(member, context);
        }
        return parameters;
    }

    void inject(Object o, InternalContext context) {
        List<Injector> injectors = this.injectors.get(o.getClass());
        for (Injector injector : injectors) {
            injector.inject(context, o);
        }
    }

    <T> T inject(Class<T> implementation, InternalContext context) {
        try {
            ConstructorInjector<T> constructor = this.getConstructor(implementation);
            return implementation.cast(constructor.construct(context, implementation));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    <T> T getInstance(Class<T> type, String name, InternalContext context) {
        ExternalContext previous = context.getExternalContext();
        Key<T> key = Key.newInstance(type, name);
        context.setExternalContext(ExternalContext.newInstance(null, key, this));
        try {
            InternalFactory<T> o = this.getFactory(key);
            if (o != null) {
                T t = this.getFactory(key).create(context);
                return t;
            }
            T t = null;
            return t;
        }
        finally {
            context.setExternalContext(previous);
        }
    }

    <T> T getInstance(Class<T> type, InternalContext context) {
        return this.getInstance(type, "default", context);
    }

    @Override
    public void inject(Object o) {
        this.callInContext(context -> {
            this.inject(o, context);
            return null;
        });
    }

    @Override
    public <T> T inject(Class<T> implementation) {
        return (T)this.callInContext(context -> this.inject(implementation, context));
    }

    @Override
    public <T> T getInstance(Class<T> type, String name) {
        return (T)this.callInContext(context -> this.getInstance(type, name, context));
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return (T)this.callInContext(context -> this.getInstance(type, context));
    }

    @Override
    public Set<String> getInstanceNames(Class<?> type) {
        Set<String> names = this.factoryNamesByType.get(type);
        if (names == null) {
            names = Collections.emptySet();
        }
        return names;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    <T> T callInContext(ContextualCallable<T> callable) {
        Object[] reference = this.localContext.get();
        if (reference[0] == null) {
            reference[0] = new InternalContext(this);
            try {
                T t = callable.call((InternalContext)reference[0]);
                return t;
            }
            finally {
                reference[0] = null;
                this.localContext.remove();
            }
        }
        return callable.call((InternalContext)reference[0]);
    }

    <T> ConstructorInjector<T> getConstructor(Class<T> implementation) {
        return this.constructors.get(implementation);
    }

    @Override
    public void setScopeStrategy(Scope.Strategy scopeStrategy) {
        this.localScopeStrategy.set(scopeStrategy);
    }

    @Override
    public void removeScopeStrategy() {
        this.localScopeStrategy.remove();
    }

    static class MissingDependencyException
    extends Exception {
        MissingDependencyException(String message) {
            super(message);
        }
    }

    static interface Injector
    extends Serializable {
        public void inject(InternalContext var1, Object var2);
    }

    static interface ContextualCallable<T> {
        public T call(InternalContext var1);
    }

    static class ParameterInjector<T> {
        final ExternalContext<T> externalContext;
        final InternalFactory<? extends T> factory;

        public ParameterInjector(ExternalContext<T> externalContext, InternalFactory<? extends T> factory) {
            this.externalContext = externalContext;
            this.factory = factory;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        T inject(Member member, InternalContext context) {
            ExternalContext previous = context.getExternalContext();
            context.setExternalContext(this.externalContext);
            try {
                T t = this.factory.create(context);
                return t;
            }
            finally {
                context.setExternalContext(previous);
            }
        }
    }

    static class ConstructorInjector<T> {
        final Class<T> implementation;
        final List<Injector> injectors;
        final Constructor<T> constructor;
        final ParameterInjector<?>[] parameterInjectors;

        ConstructorInjector(ContainerImpl container, Class<T> implementation) {
            this.implementation = implementation;
            this.constructor = this.findConstructorIn(implementation);
            if (!ContainerImpl.isPublicForReflection(this.constructor) && !this.constructor.isAccessible()) {
                SecurityManager sm = System.getSecurityManager();
                try {
                    if (sm != null) {
                        sm.checkPermission(new ReflectPermission("suppressAccessChecks"));
                    }
                    this.constructor.setAccessible(true);
                }
                catch (AccessControlException e) {
                    throw new DependencyException("Security manager in use, could not access constructor: " + implementation.getName() + "(" + this.constructor.getName() + ")", e);
                }
            }
            MissingDependencyException exception = null;
            Inject inject = null;
            ParameterInjector<?>[] parameters = null;
            try {
                inject = this.constructor.getAnnotation(Inject.class);
                parameters = this.constructParameterInjector(inject, container, this.constructor);
            }
            catch (MissingDependencyException e) {
                exception = e;
            }
            this.parameterInjectors = parameters;
            if (exception != null && inject != null && inject.required()) {
                throw new DependencyException(exception);
            }
            this.injectors = container.injectors.get(implementation);
        }

        ParameterInjector<?>[] constructParameterInjector(Inject inject, ContainerImpl container, Constructor<T> constructor) throws MissingDependencyException {
            return constructor.getParameterTypes().length == 0 ? null : container.getParametersInjectors(constructor, constructor.getParameterAnnotations(), constructor.getParameterTypes(), inject.value());
        }

        private Constructor<T> findConstructorIn(Class<T> implementation) {
            Constructor<?>[] declaredConstructors;
            Constructor<?> found = null;
            for (Constructor<?> constructor : declaredConstructors = implementation.getDeclaredConstructors()) {
                if (constructor.getAnnotation(Inject.class) == null) continue;
                if (found != null) {
                    throw new DependencyException("More than one constructor annotated with @Inject found in " + implementation + ".");
                }
                found = constructor;
            }
            if (found != null) {
                return found;
            }
            try {
                return implementation.getDeclaredConstructor(new Class[0]);
            }
            catch (NoSuchMethodException e) {
                throw new DependencyException("Could not find a suitable constructor in " + implementation.getName() + ".");
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        Object construct(InternalContext context, Class<? super T> expectedType) {
            ConstructionContext<Object> constructionContext = context.getConstructionContext(this);
            if (constructionContext.isConstructing()) {
                return constructionContext.createProxy(expectedType);
            }
            Object t = constructionContext.getCurrentReference();
            if (t != null) {
                return t;
            }
            try {
                Object parameters;
                constructionContext.startConstruction();
                try {
                    parameters = ContainerImpl.getParameters(this.constructor, context, this.parameterInjectors);
                    t = this.constructor.newInstance(parameters);
                    constructionContext.setProxyDelegates(t);
                }
                finally {
                    constructionContext.finishConstruction();
                }
                constructionContext.setCurrentReference(t);
                for (Injector injector : this.injectors) {
                    injector.inject(context, t);
                }
                parameters = t;
                return parameters;
            }
            catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            finally {
                constructionContext.removeCurrentReference();
            }
        }
    }

    static class MethodInjector
    implements Injector {
        final Method method;
        final ParameterInjector<?>[] parameterInjectors;

        public MethodInjector(ContainerImpl container, Method method, String name) throws MissingDependencyException {
            Class<?>[] parameterTypes;
            this.method = method;
            if (!ContainerImpl.isPublicForReflection(method) && !method.isAccessible()) {
                SecurityManager sm = System.getSecurityManager();
                try {
                    if (sm != null) {
                        sm.checkPermission(new ReflectPermission("suppressAccessChecks"));
                    }
                    method.setAccessible(true);
                }
                catch (AccessControlException e) {
                    throw new DependencyException("Security manager in use, could not access method: " + name + "(" + method.getName() + ")", e);
                }
            }
            if ((parameterTypes = method.getParameterTypes()).length == 0) {
                throw new DependencyException(method + " has no parameters to inject.");
            }
            this.parameterInjectors = container.getParametersInjectors(method, method.getParameterAnnotations(), parameterTypes, name);
        }

        @Override
        public void inject(InternalContext context, Object o) {
            try {
                this.method.invoke(o, ContainerImpl.getParameters(this.method, context, this.parameterInjectors));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class FieldInjector
    implements Injector {
        final Field field;
        final InternalFactory<?> factory;
        final ExternalContext<?> externalContext;

        public FieldInjector(ContainerImpl container, Field field, String name) throws MissingDependencyException {
            this.field = field;
            if (!ContainerImpl.isPublicForReflection(field) && !field.isAccessible()) {
                SecurityManager sm = System.getSecurityManager();
                try {
                    if (sm != null) {
                        sm.checkPermission(new ReflectPermission("suppressAccessChecks"));
                    }
                    field.setAccessible(true);
                }
                catch (AccessControlException e) {
                    throw new DependencyException("Security manager in use, could not access field: " + field.getDeclaringClass().getName() + "(" + field.getName() + ")", e);
                }
            }
            Key<?> key = Key.newInstance(field.getType(), name);
            this.factory = container.getFactory(key);
            if (this.factory == null) {
                throw new MissingDependencyException("No mapping found for dependency " + key + " in " + field + ".");
            }
            this.externalContext = ExternalContext.newInstance(field, key, container);
        }

        @Override
        public void inject(InternalContext context, Object o) {
            ExternalContext previous = context.getExternalContext();
            context.setExternalContext(this.externalContext);
            try {
                this.field.set(o, this.factory.create(context));
            }
            catch (IllegalAccessException e) {
                throw new AssertionError((Object)e);
            }
            finally {
                context.setExternalContext(previous);
            }
        }
    }

    static interface InjectorFactory<M extends Member & AnnotatedElement> {
        public Injector create(ContainerImpl var1, M var2, String var3) throws MissingDependencyException;
    }
}

