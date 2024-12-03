/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.inject.AbstractModule
 *  com.google.inject.Binder
 *  com.google.inject.Binding
 *  com.google.inject.ConfigurationException
 *  com.google.inject.Inject
 *  com.google.inject.Injector
 *  com.google.inject.Key
 *  com.google.inject.Module
 *  com.google.inject.Provider
 *  com.google.inject.ProvisionException
 *  com.google.inject.Scopes
 *  com.google.inject.TypeLiteral
 *  com.google.inject.internal.Annotations
 *  com.google.inject.internal.BytecodeGen
 *  com.google.inject.internal.Errors
 *  com.google.inject.internal.ErrorsException
 *  com.google.inject.internal.UniqueAnnotations
 *  com.google.inject.internal.util.Classes
 *  com.google.inject.spi.BindingTargetVisitor
 *  com.google.inject.spi.Dependency
 *  com.google.inject.spi.HasDependencies
 *  com.google.inject.spi.InjectionPoint
 *  com.google.inject.spi.Message
 *  com.google.inject.spi.ProviderInstanceBinding
 *  com.google.inject.spi.ProviderWithExtensionVisitor
 *  com.google.inject.spi.Toolable
 *  com.google.inject.util.Providers
 *  javax.inject.Provider
 */
package com.google.inject.assistedinject;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.assistedinject.AssistedInjectBinding;
import com.google.inject.assistedinject.AssistedInjectTargetVisitor;
import com.google.inject.assistedinject.AssistedMethod;
import com.google.inject.assistedinject.BindingCollector;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.BytecodeGen;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.internal.util.Classes;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.Message;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderWithExtensionVisitor;
import com.google.inject.spi.Toolable;
import com.google.inject.util.Providers;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class FactoryProvider2<F>
implements InvocationHandler,
ProviderWithExtensionVisitor<F>,
HasDependencies,
AssistedInjectBinding<F> {
    static final Annotation RETURN_ANNOTATION = UniqueAnnotations.create();
    static final Logger logger = Logger.getLogger(AssistedInject.class.getName());
    static final Assisted DEFAULT_ANNOTATION = new Assisted(){

        @Override
        public String value() {
            return "";
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Assisted.class;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Assisted && ((Assisted)o).value().equals("");
        }

        @Override
        public int hashCode() {
            return 127 * "value".hashCode() ^ "".hashCode();
        }

        @Override
        public String toString() {
            return "@" + Assisted.class.getName() + "(value=)";
        }
    };
    private final ImmutableMap<Method, AssistData> assistDataByMethod;
    private Injector injector;
    private final F factory;
    private final Key<F> factoryKey;

    FactoryProvider2(Key<F> factoryKey, BindingCollector collector) {
        this.factoryKey = factoryKey;
        TypeLiteral factoryType = factoryKey.getTypeLiteral();
        Errors errors = new Errors();
        Class factoryRawType = factoryType.getRawType();
        try {
            if (!factoryRawType.isInterface()) {
                throw errors.addMessage("%s must be an interface.", new Object[]{factoryRawType}).toException();
            }
            ImmutableMap.Builder assistDataBuilder = ImmutableMap.builder();
            for (Method method : factoryRawType.getMethods()) {
                InjectionPoint ctorInjectionPoint;
                Class scope;
                Key returnType;
                TypeLiteral returnTypeLiteral = factoryType.getReturnType(method);
                try {
                    returnType = Annotations.getKey((TypeLiteral)returnTypeLiteral, (Member)method, (Annotation[])method.getAnnotations(), (Errors)errors);
                }
                catch (ConfigurationException ce) {
                    if (this.isTypeNotSpecified(returnTypeLiteral, ce)) {
                        throw errors.keyNotFullySpecified(TypeLiteral.get((Class)factoryRawType)).toException();
                    }
                    throw ce;
                }
                this.validateFactoryReturnType(errors, returnType.getTypeLiteral().getRawType(), factoryRawType);
                List params = factoryType.getParameterTypes((Member)method);
                Annotation[][] paramAnnotations = method.getParameterAnnotations();
                int p = 0;
                ArrayList keys = Lists.newArrayList();
                for (TypeLiteral param : params) {
                    Key paramKey = Annotations.getKey((TypeLiteral)param, (Member)method, (Annotation[])paramAnnotations[p++], (Errors)errors);
                    Class underlylingType = paramKey.getTypeLiteral().getRawType();
                    if (underlylingType.equals(Provider.class) || underlylingType.equals(javax.inject.Provider.class)) {
                        errors.addMessage("A Provider may not be a type in a factory method of an AssistedInject.\n  Offending instance is parameter [%s] with key [%s] on method [%s]", new Object[]{p, paramKey, method});
                    }
                    keys.add(this.assistKey(method, paramKey, errors));
                }
                ImmutableList immutableParamList = ImmutableList.copyOf((Collection)keys);
                TypeLiteral implementation = collector.getBindings().get(returnType);
                if (implementation == null) {
                    implementation = returnType.getTypeLiteral();
                }
                if ((scope = Annotations.findScopeAnnotation((Errors)errors, (Class)implementation.getRawType())) != null) {
                    errors.addMessage("Found scope annotation [%s] on implementation class [%s] of AssistedInject factory [%s].\nThis is not allowed, please remove the scope annotation.", new Object[]{scope, implementation.getRawType(), factoryType});
                }
                try {
                    ctorInjectionPoint = this.findMatchingConstructorInjectionPoint(method, (Key<?>)returnType, (TypeLiteral<?>)implementation, (List<Key<?>>)immutableParamList);
                }
                catch (ErrorsException ee) {
                    errors.merge(ee.getErrors());
                    continue;
                }
                Constructor constructor = (Constructor)ctorInjectionPoint.getMember();
                ImmutableList providers = Collections.emptyList();
                Set<Dependency<?>> deps = this.getDependencies(ctorInjectionPoint, implementation);
                boolean optimized = false;
                if (this.isValidForOptimizedAssistedInject(deps, implementation.getRawType(), factoryType)) {
                    ImmutableList.Builder providerListBuilder = ImmutableList.builder();
                    for (int i = 0; i < params.size(); ++i) {
                        providerListBuilder.add((Object)new ThreadLocalProvider());
                    }
                    providers = providerListBuilder.build();
                    optimized = true;
                }
                assistDataBuilder.put((Object)method, (Object)new AssistData(constructor, returnType, immutableParamList, implementation, method, this.removeAssistedDeps(deps), optimized, (List<ThreadLocalProvider>)providers));
            }
            if (errors.hasErrors()) {
                throw errors.toException();
            }
            this.assistDataByMethod = assistDataBuilder.build();
        }
        catch (ErrorsException e) {
            throw new ConfigurationException((Iterable)e.getErrors().getMessages());
        }
        this.factory = factoryRawType.cast(Proxy.newProxyInstance(BytecodeGen.getClassLoader((Class)factoryRawType), new Class[]{factoryRawType}, (InvocationHandler)this));
    }

    public F get() {
        return this.factory;
    }

    public Set<Dependency<?>> getDependencies() {
        HashSet combinedDeps = new HashSet();
        for (AssistData data : this.assistDataByMethod.values()) {
            combinedDeps.addAll(data.dependencies);
        }
        return ImmutableSet.copyOf(combinedDeps);
    }

    @Override
    public Key<F> getKey() {
        return this.factoryKey;
    }

    @Override
    public Collection<AssistedMethod> getAssistedMethods() {
        return this.assistDataByMethod.values();
    }

    public <T, V> V acceptExtensionVisitor(BindingTargetVisitor<T, V> visitor, ProviderInstanceBinding<? extends T> binding) {
        if (visitor instanceof AssistedInjectTargetVisitor) {
            return ((AssistedInjectTargetVisitor)visitor).visit(this);
        }
        return (V)visitor.visit(binding);
    }

    private void validateFactoryReturnType(Errors errors, Class<?> returnType, Class<?> factoryType) {
        if (Modifier.isPublic(factoryType.getModifiers()) && !Modifier.isPublic(returnType.getModifiers())) {
            errors.addMessage("%s is public, but has a method that returns a non-public type: %s. Due to limitations with java.lang.reflect.Proxy, this is not allowed. Please either make the factory non-public or the return type public.", new Object[]{factoryType, returnType});
        }
    }

    private boolean isTypeNotSpecified(TypeLiteral typeLiteral, ConfigurationException ce) {
        Collection messages = ce.getErrorMessages();
        if (messages.size() == 1) {
            Message msg = (Message)Iterables.getOnlyElement((Iterable)new Errors().keyNotFullySpecified(typeLiteral).getMessages());
            return msg.getMessage().equals(((Message)Iterables.getOnlyElement((Iterable)messages)).getMessage());
        }
        return false;
    }

    private InjectionPoint findMatchingConstructorInjectionPoint(Method method, Key<?> returnType, TypeLiteral<?> implementation, List<Key<?>> paramList) throws ErrorsException {
        Errors errors = new Errors((Object)method);
        errors = returnType.getTypeLiteral().equals(implementation) ? errors.withSource(implementation) : errors.withSource(returnType).withSource(implementation);
        Class rawType = implementation.getRawType();
        if (Modifier.isInterface(rawType.getModifiers())) {
            errors.addMessage("%s is an interface, not a concrete class.  Unable to create AssistedInject factory.", new Object[]{implementation});
            throw errors.toException();
        }
        if (Modifier.isAbstract(rawType.getModifiers())) {
            errors.addMessage("%s is abstract, not a concrete class.  Unable to create AssistedInject factory.", new Object[]{implementation});
            throw errors.toException();
        }
        if (Classes.isInnerClass((Class)rawType)) {
            errors.cannotInjectInnerClass(rawType);
            throw errors.toException();
        }
        Constructor<?> matchingConstructor = null;
        boolean anyAssistedInjectConstructors = false;
        for (Constructor<?> constructor : rawType.getDeclaredConstructors()) {
            if (!constructor.isAnnotationPresent(AssistedInject.class)) continue;
            anyAssistedInjectConstructors = true;
            if (!this.constructorHasMatchingParams(implementation, constructor, paramList, errors)) continue;
            if (matchingConstructor != null) {
                errors.addMessage("%s has more than one constructor annotated with @AssistedInject that matches the parameters in method %s.  Unable to create AssistedInject factory.", new Object[]{implementation, method});
                throw errors.toException();
            }
            matchingConstructor = constructor;
        }
        if (!anyAssistedInjectConstructors) {
            try {
                return InjectionPoint.forConstructorOf(implementation);
            }
            catch (ConfigurationException e) {
                errors.merge(e.getErrorMessages());
                throw errors.toException();
            }
        }
        if (matchingConstructor != null) {
            InjectionPoint ip = InjectionPoint.forConstructor(matchingConstructor, implementation);
            return ip;
        }
        errors.addMessage("%s has @AssistedInject constructors, but none of them match the parameters in method %s.  Unable to create AssistedInject factory.", new Object[]{implementation, method});
        throw errors.toException();
    }

    private boolean constructorHasMatchingParams(TypeLiteral<?> type, Constructor<?> constructor, List<Key<?>> paramList, Errors errors) throws ErrorsException {
        List params = type.getParameterTypes(constructor);
        Annotation[][] paramAnnotations = constructor.getParameterAnnotations();
        int p = 0;
        ArrayList constructorKeys = Lists.newArrayList();
        for (TypeLiteral typeLiteral : params) {
            Key paramKey = Annotations.getKey((TypeLiteral)typeLiteral, constructor, (Annotation[])paramAnnotations[p++], (Errors)errors);
            constructorKeys.add(paramKey);
        }
        for (Key key : paramList) {
            if (constructorKeys.remove(key)) continue;
            return false;
        }
        for (Key key : constructorKeys) {
            if (key.getAnnotationType() != Assisted.class) continue;
            return false;
        }
        return true;
    }

    private Set<Dependency<?>> getDependencies(InjectionPoint ctorPoint, TypeLiteral<?> implementation) {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        builder.addAll((Iterable)ctorPoint.getDependencies());
        if (!implementation.getRawType().isInterface()) {
            for (InjectionPoint ip : InjectionPoint.forInstanceMethodsAndFields(implementation)) {
                builder.addAll((Iterable)ip.getDependencies());
            }
        }
        return builder.build();
    }

    private Set<Dependency<?>> removeAssistedDeps(Set<Dependency<?>> deps) {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (Dependency<?> dep : deps) {
            Class annotationType = dep.getKey().getAnnotationType();
            if (annotationType != null && annotationType.equals(Assisted.class)) continue;
            builder.add(dep);
        }
        return builder.build();
    }

    private boolean isValidForOptimizedAssistedInject(Set<Dependency<?>> dependencies, Class<?> implementation, TypeLiteral<?> factoryType) {
        Set badDeps = null;
        for (Dependency<?> dep : dependencies) {
            if (!this.isInjectorOrAssistedProvider(dep)) continue;
            if (badDeps == null) {
                badDeps = Sets.newHashSet();
            }
            badDeps.add(dep);
        }
        if (badDeps != null && !badDeps.isEmpty()) {
            logger.log(Level.WARNING, "AssistedInject factory {0} will be slow because {1} has assisted Provider dependencies or injects the Injector. Stop injecting @Assisted Provider<T> (instead use @Assisted T) or Injector to speed things up. (It will be a ~6500% speed bump!)  The exact offending deps are: {2}", new Object[]{factoryType, implementation, badDeps});
            return false;
        }
        return true;
    }

    private boolean isInjectorOrAssistedProvider(Dependency<?> dependency) {
        Class annotationType = dependency.getKey().getAnnotationType();
        return annotationType != null && annotationType.equals(Assisted.class) ? dependency.getKey().getTypeLiteral().getRawType().equals(Provider.class) : dependency.getKey().getTypeLiteral().getRawType().equals(Injector.class);
    }

    private <T> Key<T> assistKey(Method method, Key<T> key, Errors errors) throws ErrorsException {
        if (key.getAnnotationType() == null) {
            return Key.get((TypeLiteral)key.getTypeLiteral(), (Annotation)DEFAULT_ANNOTATION);
        }
        if (key.getAnnotationType() == Assisted.class) {
            return key;
        }
        errors.withSource((Object)method).addMessage("Only @Assisted is allowed for factory parameters, but found @%s", new Object[]{key.getAnnotationType()});
        throw errors.toException();
    }

    @Inject
    @Toolable
    void initialize(Injector injector) {
        if (this.injector != null) {
            throw new ConfigurationException((Iterable)ImmutableList.of((Object)new Message(FactoryProvider2.class, "Factories.create() factories may only be used in one Injector!")));
        }
        this.injector = injector;
        for (Map.Entry entry : this.assistDataByMethod.entrySet()) {
            Object[] args;
            Method method = (Method)entry.getKey();
            AssistData data = (AssistData)entry.getValue();
            if (!data.optimized) {
                args = new Object[method.getParameterTypes().length];
                Arrays.fill(args, "dummy object for validating Factories");
            } else {
                args = null;
            }
            this.getBindingFromNewInjector(method, args, data);
        }
    }

    public Binding<?> getBindingFromNewInjector(final Method method, final Object[] args, final AssistData data) {
        Preconditions.checkState((this.injector != null ? 1 : 0) != 0, (Object)"Factories.create() factories cannot be used until they're initialized by Guice.");
        Key<?> returnType = data.returnType;
        final Key returnKey = Key.get((TypeLiteral)returnType.getTypeLiteral(), (Annotation)RETURN_ANNOTATION);
        AbstractModule assistedModule = new AbstractModule(){

            protected void configure() {
                Constructor<?> constructor;
                Binder binder = this.binder().withSource((Object)method);
                int p = 0;
                if (!data.optimized) {
                    for (Key paramKey : data.paramTypes) {
                        binder.bind(paramKey).toProvider(Providers.of((Object)args[p++]));
                    }
                } else {
                    for (Key paramKey : data.paramTypes) {
                        binder.bind(paramKey).toProvider((Provider)data.providers.get(p++));
                    }
                }
                if ((constructor = data.constructor) != null) {
                    binder.bind(returnKey).toConstructor(constructor, data.implementationType).in(Scopes.NO_SCOPE);
                }
            }
        };
        Injector forCreate = this.injector.createChildInjector(new Module[]{assistedModule});
        Binding binding = forCreate.getBinding(returnKey);
        if (data.optimized) {
            data.cachedBinding = binding;
        }
        return binding;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke((Object)this, args);
        }
        AssistData data = (AssistData)this.assistDataByMethod.get((Object)method);
        Provider provider = data.cachedBinding != null ? data.cachedBinding.getProvider() : this.getBindingFromNewInjector(method, args, data).getProvider();
        try {
            int p = 0;
            for (ThreadLocalProvider tlp : data.providers) {
                tlp.set(args[p++]);
            }
            Object i$ = provider.get();
            return i$;
        }
        catch (ProvisionException e) {
            Message onlyError;
            Throwable cause;
            if (e.getErrorMessages().size() == 1 && (cause = (onlyError = (Message)Iterables.getOnlyElement((Iterable)e.getErrorMessages())).getCause()) != null && FactoryProvider2.canRethrow(method, cause)) {
                throw cause;
            }
            throw e;
        }
        finally {
            for (ThreadLocalProvider tlp : data.providers) {
                tlp.remove();
            }
        }
    }

    public String toString() {
        return this.factory.getClass().getInterfaces()[0].getName();
    }

    public boolean equals(Object o) {
        return o == this || o == this.factory;
    }

    static boolean canRethrow(Method invoked, Throwable thrown) {
        if (thrown instanceof Error || thrown instanceof RuntimeException) {
            return true;
        }
        for (Class<?> declared : invoked.getExceptionTypes()) {
            if (!declared.isInstance(thrown)) continue;
            return true;
        }
        return false;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ThreadLocalProvider
    extends ThreadLocal<Object>
    implements Provider<Object> {
        private ThreadLocalProvider() {
        }

        @Override
        protected Object initialValue() {
            throw new IllegalStateException("Cannot use optimized @Assisted provider outside the scope of the constructor. (This should never happen.  If it does, please report it.)");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class AssistData
    implements AssistedMethod {
        final Constructor<?> constructor;
        final Key<?> returnType;
        final ImmutableList<Key<?>> paramTypes;
        final TypeLiteral<?> implementationType;
        final Set<Dependency<?>> dependencies;
        final Method factoryMethod;
        final boolean optimized;
        final List<ThreadLocalProvider> providers;
        volatile Binding<?> cachedBinding;

        AssistData(Constructor<?> constructor, Key<?> returnType, ImmutableList<Key<?>> paramTypes, TypeLiteral<?> implementationType, Method factoryMethod, Set<Dependency<?>> dependencies, boolean optimized, List<ThreadLocalProvider> providers) {
            this.constructor = constructor;
            this.returnType = returnType;
            this.paramTypes = paramTypes;
            this.implementationType = implementationType;
            this.factoryMethod = factoryMethod;
            this.dependencies = dependencies;
            this.optimized = optimized;
            this.providers = providers;
        }

        public String toString() {
            return Objects.toStringHelper(this.getClass()).add("ctor", this.constructor).add("return type", this.returnType).add("param type", this.paramTypes).add("implementation type", this.implementationType).add("dependencies", this.dependencies).add("factory method", (Object)this.factoryMethod).add("optimized", this.optimized).add("providers", this.providers).add("cached binding", this.cachedBinding).toString();
        }

        @Override
        public Set<Dependency<?>> getDependencies() {
            return this.dependencies;
        }

        @Override
        public Method getFactoryMethod() {
            return this.factoryMethod;
        }

        @Override
        public Constructor<?> getImplementationConstructor() {
            return this.constructor;
        }

        @Override
        public TypeLiteral<?> getImplementationType() {
            return this.implementationType;
        }
    }
}

