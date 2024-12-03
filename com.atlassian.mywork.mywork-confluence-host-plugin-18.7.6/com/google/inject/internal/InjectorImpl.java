/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.ImplementedBy;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.ConstantFactory;
import com.google.inject.internal.ConstructorBindingImpl;
import com.google.inject.internal.ConstructorInjectorStore;
import com.google.inject.internal.ContextualCallable;
import com.google.inject.internal.DeferredLookups;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.Initializables;
import com.google.inject.internal.InstanceBindingImpl;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.InternalInjectorCreator;
import com.google.inject.internal.LinkedBindingImpl;
import com.google.inject.internal.LinkedProviderBindingImpl;
import com.google.inject.internal.Lookups;
import com.google.inject.internal.MembersInjectorImpl;
import com.google.inject.internal.MembersInjectorStore;
import com.google.inject.internal.Scoping;
import com.google.inject.internal.SingleParameterInjector;
import com.google.inject.internal.State;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$ImmutableMap;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Maps;
import com.google.inject.internal.util.$Nullable;
import com.google.inject.internal.util.$Objects;
import com.google.inject.internal.util.$SourceProvider;
import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.util.Providers;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class InjectorImpl
implements Injector,
Lookups {
    public static final TypeLiteral<String> STRING_TYPE = TypeLiteral.get(String.class);
    final State state;
    final InjectorImpl parent;
    final BindingsMultimap bindingsMultimap = new BindingsMultimap();
    final InjectorOptions options;
    final Map<Key<?>, BindingImpl<?>> jitBindings = $Maps.newHashMap();
    Lookups lookups = new DeferredLookups(this);
    final ConstructorInjectorStore constructors = new ConstructorInjectorStore(this);
    MembersInjectorStore membersInjectorStore;
    final ThreadLocal<Object[]> localContext;

    InjectorImpl(@.Nullable InjectorImpl parent, State state, InjectorOptions injectorOptions) {
        this.parent = parent;
        this.state = state;
        this.options = injectorOptions;
        this.localContext = parent != null ? parent.localContext : new ThreadLocal<Object[]>(){

            @Override
            protected Object[] initialValue() {
                return new Object[1];
            }
        };
    }

    void index() {
        for (Binding<?> binding : this.state.getExplicitBindingsThisLevel().values()) {
            this.index(binding);
        }
    }

    <T> void index(Binding<T> binding) {
        this.bindingsMultimap.put(binding.getKey().getTypeLiteral(), binding);
    }

    @Override
    public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type) {
        return this.bindingsMultimap.getAll(type);
    }

    public <T> BindingImpl<T> getBinding(Key<T> key) {
        Errors errors = new Errors(key);
        try {
            BindingImpl<T> result = this.getBindingOrThrow(key, errors, JitLimitation.EXISTING_JIT);
            errors.throwConfigurationExceptionIfErrorsExist();
            return result;
        }
        catch (ErrorsException e) {
            throw new ConfigurationException(errors.merge(e.getErrors()).getMessages());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> BindingImpl<T> getExistingBinding(Key<T> key) {
        BindingImpl<T> explicitBinding = this.state.getExplicitBinding(key);
        if (explicitBinding != null) {
            return explicitBinding;
        }
        Object object = this.state.lock();
        synchronized (object) {
            InjectorImpl injector = this;
            while (injector != null) {
                BindingImpl<?> jitBinding = injector.jitBindings.get(key);
                if (jitBinding != null) {
                    return jitBinding;
                }
                injector = injector.parent;
            }
        }
        if (InjectorImpl.isProvider(key)) {
            try {
                Key<T> providedKey = InjectorImpl.getProvidedKey(key, new Errors());
                if (this.getExistingBinding((Key)providedKey) != null) {
                    return this.getBinding((Key)key);
                }
            }
            catch (ErrorsException e) {
                throw new ConfigurationException(e.getErrors().getMessages());
            }
        }
        return null;
    }

    <T> BindingImpl<T> getBindingOrThrow(Key<T> key, Errors errors, JitLimitation jitType) throws ErrorsException {
        BindingImpl<T> binding = this.state.getExplicitBinding(key);
        if (binding != null) {
            return binding;
        }
        return this.getJustInTimeBinding(key, errors, jitType);
    }

    @Override
    public <T> Binding<T> getBinding(Class<T> type) {
        return this.getBinding((Key)Key.get(type));
    }

    @Override
    public Injector getParent() {
        return this.parent;
    }

    @Override
    public Injector createChildInjector(Iterable<? extends Module> modules) {
        return new InternalInjectorCreator().parentInjector(this).addModules(modules).build();
    }

    @Override
    public Injector createChildInjector(Module ... modules) {
        return this.createChildInjector($ImmutableList.of(modules));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T> BindingImpl<T> getJustInTimeBinding(Key<T> key, Errors errors, JitLimitation jitType) throws ErrorsException {
        boolean jitOverride = InjectorImpl.isProvider(key) || InjectorImpl.isTypeLiteral(key) || InjectorImpl.isMembersInjector(key);
        Object object = this.state.lock();
        synchronized (object) {
            InjectorImpl injector = this;
            while (injector != null) {
                BindingImpl<?> binding = injector.jitBindings.get(key);
                if (binding != null) {
                    if (this.options.jitDisabled && jitType == JitLimitation.NO_JIT && !jitOverride && !(binding instanceof ConvertedConstantBindingImpl)) {
                        throw errors.jitDisabled(key).toException();
                    }
                    return binding;
                }
                injector = injector.parent;
            }
            return this.createJustInTimeBindingRecursive(key, errors, this.options.jitDisabled, jitType);
        }
    }

    private static boolean isProvider(Key<?> key) {
        return key.getTypeLiteral().getRawType().equals(Provider.class);
    }

    private static boolean isTypeLiteral(Key<?> key) {
        return key.getTypeLiteral().getRawType().equals(TypeLiteral.class);
    }

    private static <T> Key<T> getProvidedKey(Key<Provider<T>> key, Errors errors) throws ErrorsException {
        Type providerType = key.getTypeLiteral().getType();
        if (!(providerType instanceof ParameterizedType)) {
            throw errors.cannotInjectRawProvider().toException();
        }
        Type entryType = ((ParameterizedType)providerType).getActualTypeArguments()[0];
        Key<?> providedKey = key.ofType(entryType);
        return providedKey;
    }

    private static boolean isMembersInjector(Key<?> key) {
        return key.getTypeLiteral().getRawType().equals(MembersInjector.class) && key.getAnnotationType() == null;
    }

    private <T> BindingImpl<MembersInjector<T>> createMembersInjectorBinding(Key<MembersInjector<T>> key, Errors errors) throws ErrorsException {
        Type membersInjectorType = key.getTypeLiteral().getType();
        if (!(membersInjectorType instanceof ParameterizedType)) {
            throw errors.cannotInjectRawMembersInjector().toException();
        }
        TypeLiteral<?> instanceType = TypeLiteral.get(((ParameterizedType)membersInjectorType).getActualTypeArguments()[0]);
        MembersInjectorImpl<?> membersInjector = this.membersInjectorStore.get(instanceType, errors);
        ConstantFactory factory = new ConstantFactory(Initializables.of(membersInjector));
        return new InstanceBindingImpl<MembersInjector<T>>(this, key, $SourceProvider.UNKNOWN_SOURCE, factory, $ImmutableSet.<InjectionPoint>of(), membersInjector);
    }

    private <T> BindingImpl<Provider<T>> createProviderBinding(Key<Provider<T>> key, Errors errors) throws ErrorsException {
        Key<T> providedKey = InjectorImpl.getProvidedKey(key, errors);
        BindingImpl<T> delegate = this.getBindingOrThrow(providedKey, errors, JitLimitation.NO_JIT);
        return new ProviderBindingImpl<T>(this, key, delegate);
    }

    private <T> BindingImpl<T> convertConstantStringBinding(Key<T> key, Errors errors) throws ErrorsException {
        Key<String> stringKey = key.ofType(STRING_TYPE);
        BindingImpl<String> stringBinding = this.state.getExplicitBinding(stringKey);
        if (stringBinding == null || !stringBinding.isConstant()) {
            return null;
        }
        String stringValue = stringBinding.getProvider().get();
        Object source = stringBinding.getSource();
        TypeLiteral<T> type = key.getTypeLiteral();
        TypeConverterBinding typeConverterBinding = this.state.getConverter(stringValue, type, errors, source);
        if (typeConverterBinding == null) {
            return null;
        }
        try {
            Object converted = typeConverterBinding.getTypeConverter().convert(stringValue, type);
            if (converted == null) {
                throw errors.converterReturnedNull(stringValue, source, type, typeConverterBinding).toException();
            }
            if (!type.getRawType().isInstance(converted)) {
                throw errors.conversionTypeError(stringValue, source, type, typeConverterBinding, converted).toException();
            }
            return new ConvertedConstantBindingImpl<Object>(this, key, converted, stringBinding, typeConverterBinding);
        }
        catch (ErrorsException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw errors.conversionError(stringValue, source, type, typeConverterBinding, e).toException();
        }
    }

    <T> void initializeBinding(BindingImpl<T> binding, Errors errors) throws ErrorsException {
        if (binding instanceof ConstructorBindingImpl) {
            ((ConstructorBindingImpl)binding).initialize(this, errors);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    <T> void initializeJitBinding(BindingImpl<T> binding, Errors errors) throws ErrorsException {
        if (binding instanceof ConstructorBindingImpl) {
            Key<T> key = binding.getKey();
            this.jitBindings.put(key, binding);
            boolean successful = false;
            ConstructorBindingImpl cb = (ConstructorBindingImpl)binding;
            try {
                cb.initialize(this, errors);
                successful = true;
            }
            finally {
                if (!successful) {
                    this.removeFailedJitBinding(key, null);
                    this.cleanup(binding, new HashSet<Key>());
                }
            }
        }
    }

    private boolean cleanup(BindingImpl<?> binding, Set<Key> encountered) {
        boolean bindingFailed = false;
        Set<Dependency<?>> deps = this.getInternalDependencies(binding);
        for (Dependency<?> dep : deps) {
            Key<?> depKey = dep.getKey();
            InjectionPoint ip = dep.getInjectionPoint();
            if (!encountered.add(depKey)) continue;
            BindingImpl<?> depBinding = this.jitBindings.get(depKey);
            if (depBinding != null) {
                boolean failed = this.cleanup(depBinding, encountered);
                if (depBinding instanceof ConstructorBindingImpl) {
                    ConstructorBindingImpl ctorBinding = (ConstructorBindingImpl)depBinding;
                    ip = ctorBinding.getInternalConstructor();
                    if (!ctorBinding.isInitialized()) {
                        failed = true;
                    }
                }
                if (!failed) continue;
                this.removeFailedJitBinding(depKey, ip);
                bindingFailed = true;
                continue;
            }
            if (this.state.getExplicitBinding(depKey) != null) continue;
            bindingFailed = true;
        }
        return bindingFailed;
    }

    private void removeFailedJitBinding(Key<?> key, InjectionPoint ip) {
        this.jitBindings.remove(key);
        this.membersInjectorStore.remove(key.getTypeLiteral());
        if (ip != null) {
            this.constructors.remove(ip);
        }
    }

    private Set<Dependency<?>> getInternalDependencies(BindingImpl<?> binding) {
        if (binding instanceof ConstructorBindingImpl) {
            return ((ConstructorBindingImpl)binding).getInternalDependencies();
        }
        if (binding instanceof HasDependencies) {
            return ((HasDependencies)((Object)binding)).getDependencies();
        }
        return $ImmutableSet.of();
    }

    <T> BindingImpl<T> createUninitializedBinding(Key<T> key, Scoping scoping, Object source, Errors errors, boolean jitBinding) throws ErrorsException {
        Class<T> rawType = key.getTypeLiteral().getRawType();
        if (rawType.isArray() || rawType.isEnum()) {
            throw errors.missingImplementation(key).toException();
        }
        if (rawType == TypeLiteral.class) {
            BindingImpl<TypeLiteral<T>> binding = this.createTypeLiteralBinding(key, errors);
            return binding;
        }
        ImplementedBy implementedBy = rawType.getAnnotation(ImplementedBy.class);
        if (implementedBy != null) {
            Annotations.checkForMisplacedScopeAnnotations(rawType, source, errors);
            return this.createImplementedByBinding(key, scoping, implementedBy, errors);
        }
        ProvidedBy providedBy = rawType.getAnnotation(ProvidedBy.class);
        if (providedBy != null) {
            Annotations.checkForMisplacedScopeAnnotations(rawType, source, errors);
            return this.createProvidedByBinding(key, scoping, providedBy, errors);
        }
        return ConstructorBindingImpl.create(this, key, null, source, scoping, errors, jitBinding && this.options.jitDisabled);
    }

    private <T> BindingImpl<TypeLiteral<T>> createTypeLiteralBinding(Key<TypeLiteral<T>> key, Errors errors) throws ErrorsException {
        Type typeLiteralType = key.getTypeLiteral().getType();
        if (!(typeLiteralType instanceof ParameterizedType)) {
            throw errors.cannotInjectRawTypeLiteral().toException();
        }
        ParameterizedType parameterizedType = (ParameterizedType)typeLiteralType;
        Type innerType = parameterizedType.getActualTypeArguments()[0];
        if (!(innerType instanceof Class || innerType instanceof GenericArrayType || innerType instanceof ParameterizedType)) {
            throw errors.cannotInjectTypeLiteralOf(innerType).toException();
        }
        TypeLiteral<?> value = TypeLiteral.get(innerType);
        ConstantFactory factory = new ConstantFactory(Initializables.of(value));
        return new InstanceBindingImpl<TypeLiteral<T>>(this, key, $SourceProvider.UNKNOWN_SOURCE, factory, $ImmutableSet.<InjectionPoint>of(), value);
    }

    <T> BindingImpl<T> createProvidedByBinding(Key<T> key, Scoping scoping, ProvidedBy providedBy, Errors errors) throws ErrorsException {
        final Class<T> rawType = key.getTypeLiteral().getRawType();
        final Class<? extends Provider<?>> providerType = providedBy.value();
        if (providerType == rawType) {
            throw errors.recursiveProviderType().toException();
        }
        final Key<? extends Provider<?>> providerKey = Key.get(providerType);
        final BindingImpl<? extends Provider<?>> providerBinding = this.getBindingOrThrow(providerKey, errors, JitLimitation.NEW_OR_EXISTING_JIT);
        InternalFactory internalFactory = new InternalFactory<T>(){

            @Override
            public T get(Errors errors, InternalContext context, Dependency dependency, boolean linked) throws ErrorsException {
                errors = errors.withSource(providerKey);
                Provider provider = (Provider)providerBinding.getInternalFactory().get(errors, context, dependency, true);
                try {
                    Object o = provider.get();
                    if (o != null && !rawType.isInstance(o)) {
                        throw errors.subtypeNotProvided(providerType, rawType).toException();
                    }
                    Object t = o;
                    return t;
                }
                catch (RuntimeException e) {
                    throw errors.errorInProvider(e).toException();
                }
            }
        };
        Class<T> source = rawType;
        return new LinkedProviderBindingImpl<T>(this, key, source, Scoping.scope(key, this, internalFactory, source, scoping), scoping, providerKey);
    }

    private <T> BindingImpl<T> createImplementedByBinding(Key<T> key, Scoping scoping, ImplementedBy implementedBy, Errors errors) throws ErrorsException {
        Class<T> rawType = key.getTypeLiteral().getRawType();
        Class<?> implementationType = implementedBy.value();
        if (implementationType == rawType) {
            throw errors.recursiveImplementationType().toException();
        }
        if (!rawType.isAssignableFrom(implementationType)) {
            throw errors.notASubtype(implementationType, rawType).toException();
        }
        Class<?> subclass = implementationType;
        final Key<?> targetKey = Key.get(subclass);
        final BindingImpl<?> targetBinding = this.getBindingOrThrow(targetKey, errors, JitLimitation.NEW_OR_EXISTING_JIT);
        InternalFactory internalFactory = new InternalFactory<T>(){

            @Override
            public T get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) throws ErrorsException {
                return targetBinding.getInternalFactory().get(errors.withSource(targetKey), context, dependency, true);
            }
        };
        Class<T> source = rawType;
        return new LinkedBindingImpl<T>(this, key, source, Scoping.scope(key, this, internalFactory, source, scoping), scoping, targetKey);
    }

    private <T> BindingImpl<T> createJustInTimeBindingRecursive(Key<T> key, Errors errors, boolean jitDisabled, JitLimitation jitType) throws ErrorsException {
        if (this.parent != null) {
            try {
                return this.parent.createJustInTimeBindingRecursive(key, new Errors(), jitDisabled, this.parent.options.jitDisabled ? JitLimitation.NO_JIT : jitType);
            }
            catch (ErrorsException ignored) {
                // empty catch block
            }
        }
        if (this.state.isBlacklisted(key)) {
            Set<Object> sources = this.state.getSourcesForBlacklistedKey(key);
            throw errors.childBindingAlreadySet(key, sources).toException();
        }
        BindingImpl<T> binding = this.createJustInTimeBinding(key, errors, jitDisabled, jitType);
        this.state.parent().blacklist(key, binding.getSource());
        this.jitBindings.put(key, binding);
        return binding;
    }

    private <T> BindingImpl<T> createJustInTimeBinding(Key<T> key, Errors errors, boolean jitDisabled, JitLimitation jitType) throws ErrorsException {
        int numErrorsBefore = errors.size();
        if (this.state.isBlacklisted(key)) {
            Set<Object> sources = this.state.getSourcesForBlacklistedKey(key);
            throw errors.childBindingAlreadySet(key, sources).toException();
        }
        if (InjectorImpl.isProvider(key)) {
            BindingImpl<Provider<T>> binding = this.createProviderBinding(key, errors);
            return binding;
        }
        if (InjectorImpl.isMembersInjector(key)) {
            BindingImpl<MembersInjector<T>> binding = this.createMembersInjectorBinding(key, errors);
            return binding;
        }
        BindingImpl<Provider<T>> convertedBinding = this.convertConstantStringBinding(key, errors);
        if (convertedBinding != null) {
            return convertedBinding;
        }
        if (!InjectorImpl.isTypeLiteral(key) && jitDisabled && jitType != JitLimitation.NEW_OR_EXISTING_JIT) {
            throw errors.jitDisabled(key).toException();
        }
        if (key.getAnnotationType() != null) {
            if (key.hasAttributes()) {
                try {
                    Errors ignored = new Errors();
                    return this.getBindingOrThrow(key.withoutAttributes(), ignored, JitLimitation.NO_JIT);
                }
                catch (ErrorsException ignored) {
                    // empty catch block
                }
            }
            throw errors.missingImplementation(key).toException();
        }
        Class<Provider<T>> source = key.getTypeLiteral().getRawType();
        BindingImpl<Provider<T>> binding = this.createUninitializedBinding(key, Scoping.UNSCOPED, source, errors, true);
        errors.throwIfNewErrors(numErrorsBefore);
        this.initializeJitBinding(binding, errors);
        return binding;
    }

    <T> InternalFactory<? extends T> getInternalFactory(Key<T> key, Errors errors, JitLimitation jitType) throws ErrorsException {
        return this.getBindingOrThrow(key, errors, jitType).getInternalFactory();
    }

    @Override
    public Map<Key<?>, Binding<?>> getBindings() {
        return this.state.getExplicitBindingsThisLevel();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Map<Key<?>, Binding<?>> getAllBindings() {
        Object object = this.state.lock();
        synchronized (object) {
            return new $ImmutableMap.Builder().putAll(this.state.getExplicitBindingsThisLevel()).putAll(this.jitBindings).build();
        }
    }

    @Override
    public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
        return $ImmutableMap.copyOf(this.state.getScopes());
    }

    @Override
    public Set<TypeConverterBinding> getTypeConverterBindings() {
        return $ImmutableSet.copyOf(this.state.getConvertersThisLevel());
    }

    SingleParameterInjector<?>[] getParametersInjectors(List<Dependency<?>> parameters, Errors errors) throws ErrorsException {
        if (parameters.isEmpty()) {
            return null;
        }
        int numErrorsBefore = errors.size();
        SingleParameterInjector[] result = new SingleParameterInjector[parameters.size()];
        int i = 0;
        for (Dependency<?> parameter : parameters) {
            try {
                result[i++] = this.createParameterInjector(parameter, errors.withSource(parameter));
            }
            catch (ErrorsException rethrownBelow) {}
        }
        errors.throwIfNewErrors(numErrorsBefore);
        return result;
    }

    <T> SingleParameterInjector<T> createParameterInjector(Dependency<T> dependency, Errors errors) throws ErrorsException {
        InternalFactory<T> factory = this.getInternalFactory(dependency.getKey(), errors, JitLimitation.NO_JIT);
        return new SingleParameterInjector<T>(dependency, factory);
    }

    @Override
    public void injectMembers(Object instance) {
        MembersInjector<?> membersInjector = this.getMembersInjector(instance.getClass());
        membersInjector.injectMembers(instance);
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
        Errors errors = new Errors(typeLiteral);
        try {
            return this.membersInjectorStore.get(typeLiteral, errors);
        }
        catch (ErrorsException e) {
            throw new ConfigurationException(errors.merge(e.getErrors()).getMessages());
        }
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
        return this.getMembersInjector(TypeLiteral.get(type));
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        return this.getProvider(Key.get(type));
    }

    <T> Provider<T> getProviderOrThrow(Key<T> key, Errors errors) throws ErrorsException {
        final InternalFactory<T> factory = this.getInternalFactory(key, errors, JitLimitation.NO_JIT);
        final Dependency<T> dependency = Dependency.get(key);
        return new Provider<T>(){

            @Override
            public T get() {
                final Errors errors = new Errors(dependency);
                try {
                    Object t = InjectorImpl.this.callInContext(new ContextualCallable<T>(){

                        /*
                         * WARNING - Removed try catching itself - possible behaviour change.
                         */
                        @Override
                        public T call(InternalContext context) throws ErrorsException {
                            Dependency previous = context.setDependency(dependency);
                            try {
                                Object t = factory.get(errors, context, dependency, false);
                                return t;
                            }
                            finally {
                                context.setDependency(previous);
                            }
                        }
                    });
                    errors.throwIfNewErrors(0);
                    return t;
                }
                catch (ErrorsException e) {
                    throw new ProvisionException(errors.merge(e.getErrors()).getMessages());
                }
            }

            public String toString() {
                return factory.toString();
            }
        };
    }

    @Override
    public <T> Provider<T> getProvider(Key<T> key) {
        Errors errors = new Errors(key);
        try {
            Provider<T> result = this.getProviderOrThrow(key, errors);
            errors.throwIfNewErrors(0);
            return result;
        }
        catch (ErrorsException e) {
            throw new ConfigurationException(errors.merge(e.getErrors()).getMessages());
        }
    }

    @Override
    public <T> T getInstance(Key<T> key) {
        return this.getProvider(key).get();
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return this.getProvider(type).get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    <T> T callInContext(ContextualCallable<T> callable) throws ErrorsException {
        Object[] reference = this.localContext.get();
        if (reference[0] == null) {
            reference[0] = new InternalContext();
            try {
                T t = callable.call((InternalContext)reference[0]);
                return t;
            }
            finally {
                reference[0] = null;
            }
        }
        return callable.call((InternalContext)reference[0]);
    }

    public String toString() {
        return new $ToStringBuilder(Injector.class).add("bindings", this.state.getExplicitBindingsThisLevel().values()).toString();
    }

    static interface MethodInvoker {
        public Object invoke(Object var1, Object ... var2) throws IllegalAccessException, InvocationTargetException;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class BindingsMultimap {
        final Map<TypeLiteral<?>, List<Binding<?>>> multimap = $Maps.newHashMap();

        private BindingsMultimap() {
        }

        <T> void put(TypeLiteral<T> type, Binding<T> binding) {
            List<Binding<?>> bindingsForType = this.multimap.get(type);
            if (bindingsForType == null) {
                bindingsForType = $Lists.newArrayList();
                this.multimap.put(type, bindingsForType);
            }
            bindingsForType.add(binding);
        }

        <T> List<Binding<T>> getAll(TypeLiteral<T> type) {
            List<Binding<?>> bindings = this.multimap.get(type);
            return bindings != null ? Collections.unmodifiableList(this.multimap.get(type)) : $ImmutableList.of();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ConvertedConstantBindingImpl<T>
    extends BindingImpl<T>
    implements ConvertedConstantBinding<T> {
        final T value;
        final Provider<T> provider;
        final Binding<String> originalBinding;
        final TypeConverterBinding typeConverterBinding;

        ConvertedConstantBindingImpl(InjectorImpl injector, Key<T> key, T value, Binding<String> originalBinding, TypeConverterBinding typeConverterBinding) {
            super(injector, key, originalBinding.getSource(), new ConstantFactory<T>(Initializables.of(value)), Scoping.UNSCOPED);
            this.value = value;
            this.provider = Providers.of(value);
            this.originalBinding = originalBinding;
            this.typeConverterBinding = typeConverterBinding;
        }

        @Override
        public Provider<T> getProvider() {
            return this.provider;
        }

        @Override
        public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
            return visitor.visit(this);
        }

        @Override
        public T getValue() {
            return this.value;
        }

        @Override
        public TypeConverterBinding getTypeConverterBinding() {
            return this.typeConverterBinding;
        }

        @Override
        public Key<String> getSourceKey() {
            return this.originalBinding.getKey();
        }

        @Override
        public Set<Dependency<?>> getDependencies() {
            return $ImmutableSet.of(Dependency.get(this.getSourceKey()));
        }

        @Override
        public void applyTo(Binder binder) {
            throw new UnsupportedOperationException("This element represents a synthetic binding.");
        }

        @Override
        public String toString() {
            return new $ToStringBuilder(ConvertedConstantBinding.class).add("key", this.getKey()).add("sourceKey", this.getSourceKey()).add("value", this.value).toString();
        }

        public boolean equals(Object obj) {
            if (obj instanceof ConvertedConstantBindingImpl) {
                ConvertedConstantBindingImpl o = (ConvertedConstantBindingImpl)obj;
                return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && $Objects.equal(this.value, o.value);
            }
            return false;
        }

        public int hashCode() {
            return $Objects.hashCode(this.getKey(), this.getScoping(), this.value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ProviderBindingImpl<T>
    extends BindingImpl<Provider<T>>
    implements ProviderBinding<Provider<T>>,
    HasDependencies {
        final BindingImpl<T> providedBinding;

        ProviderBindingImpl(InjectorImpl injector, Key<Provider<T>> key, Binding<T> providedBinding) {
            super(injector, key, providedBinding.getSource(), ProviderBindingImpl.createInternalFactory(providedBinding), Scoping.UNSCOPED);
            this.providedBinding = (BindingImpl)providedBinding;
        }

        static <T> InternalFactory<Provider<T>> createInternalFactory(Binding<T> providedBinding) {
            final Provider<T> provider = providedBinding.getProvider();
            return new InternalFactory<Provider<T>>(){

                @Override
                public Provider<T> get(Errors errors, InternalContext context, Dependency dependency, boolean linked) {
                    return provider;
                }
            };
        }

        @Override
        public Key<? extends T> getProvidedKey() {
            return this.providedBinding.getKey();
        }

        @Override
        public <V> V acceptTargetVisitor(BindingTargetVisitor<? super Provider<T>, V> visitor) {
            return visitor.visit(this);
        }

        @Override
        public void applyTo(Binder binder) {
            throw new UnsupportedOperationException("This element represents a synthetic binding.");
        }

        @Override
        public String toString() {
            return new $ToStringBuilder(ProviderBinding.class).add("key", this.getKey()).add("providedKey", this.getProvidedKey()).toString();
        }

        @Override
        public Set<Dependency<?>> getDependencies() {
            return $ImmutableSet.of(Dependency.get(this.getProvidedKey()));
        }

        public boolean equals(Object obj) {
            if (obj instanceof ProviderBindingImpl) {
                ProviderBindingImpl o = (ProviderBindingImpl)obj;
                return this.getKey().equals(o.getKey()) && this.getScoping().equals(o.getScoping()) && $Objects.equal(this.providedBinding, o.providedBinding);
            }
            return false;
        }

        public int hashCode() {
            return $Objects.hashCode(this.getKey(), this.getScoping(), this.providedBinding);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static enum JitLimitation {
        NO_JIT,
        EXISTING_JIT,
        NEW_OR_EXISTING_JIT;

    }

    static class InjectorOptions {
        final Stage stage;
        final boolean jitDisabled;
        final boolean disableCircularProxies;

        InjectorOptions(Stage stage, boolean jitDisabled, boolean disableCircularProxies) {
            this.stage = stage;
            this.jitDisabled = jitDisabled;
            this.disableCircularProxies = disableCircularProxies;
        }

        public String toString() {
            return new $ToStringBuilder(this.getClass()).add("stage", (Object)this.stage).add("jitDisabled", this.jitDisabled).add("disableCircularProxies", this.disableCircularProxies).toString();
        }
    }
}

