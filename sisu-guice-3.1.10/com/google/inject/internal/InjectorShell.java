/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  javax.inject.Singleton
 *  org.slf4j.ILoggerFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.internal.BindingProcessor;
import com.google.inject.internal.ConstantFactory;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InheritingState;
import com.google.inject.internal.Initializables;
import com.google.inject.internal.Initializer;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InjectorOptionsProcessor;
import com.google.inject.internal.InstanceBindingImpl;
import com.google.inject.internal.InterceptorBindingProcessor;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.ListenerBindingProcessor;
import com.google.inject.internal.MembersInjectorStore;
import com.google.inject.internal.MessageProcessor;
import com.google.inject.internal.PrivateElementProcessor;
import com.google.inject.internal.PrivateElementsImpl;
import com.google.inject.internal.ProcessedBindingData;
import com.google.inject.internal.ProviderInstanceBindingImpl;
import com.google.inject.internal.ProvisionListenerCallbackStore;
import com.google.inject.internal.ScopeBindingProcessor;
import com.google.inject.internal.Scoping;
import com.google.inject.internal.State;
import com.google.inject.internal.TypeConverterBindingProcessor;
import com.google.inject.internal.UntargettedBindingProcessor;
import com.google.inject.internal.util.SourceProvider;
import com.google.inject.internal.util.Stopwatch;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ProvisionListenerBinding;
import com.google.inject.spi.TypeListenerBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class InjectorShell {
    private final List<Element> elements;
    private final InjectorImpl injector;

    private InjectorShell(Builder builder, List<Element> elements, InjectorImpl injector) {
        this.elements = elements;
        this.injector = injector;
    }

    InjectorImpl getInjector() {
        return this.injector;
    }

    List<Element> getElements() {
        return this.elements;
    }

    private static void bindInjector(InjectorImpl injector) {
        Key<Injector> key = Key.get(Injector.class);
        InjectorFactory injectorFactory = new InjectorFactory(injector);
        injector.state.putBinding(key, new ProviderInstanceBindingImpl<Injector>(injector, key, SourceProvider.UNKNOWN_SOURCE, injectorFactory, Scoping.UNSCOPED, injectorFactory, (Set<InjectionPoint>)ImmutableSet.of()));
    }

    private static void bindLogger(InjectorImpl injector) {
        Key<java.util.logging.Logger> key = Key.get(java.util.logging.Logger.class);
        LoggerFactory loggerFactory = new LoggerFactory();
        injector.state.putBinding(key, new ProviderInstanceBindingImpl<java.util.logging.Logger>(injector, key, SourceProvider.UNKNOWN_SOURCE, loggerFactory, Scoping.UNSCOPED, loggerFactory, (Set<InjectionPoint>)ImmutableSet.of()));
        try {
            Key<Logger> slf4jKey = Key.get(Logger.class);
            SLF4JLoggerFactory slf4jLoggerFactory = new SLF4JLoggerFactory(injector);
            injector.state.putBinding(slf4jKey, new ProviderInstanceBindingImpl<Logger>(injector, slf4jKey, SourceProvider.UNKNOWN_SOURCE, slf4jLoggerFactory, Scoping.UNSCOPED, slf4jLoggerFactory, (Set<InjectionPoint>)ImmutableSet.of()));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void bindStage(InjectorImpl injector, Stage stage) {
        Key<Stage> key = Key.get(Stage.class);
        InstanceBindingImpl<Stage> stageBinding = new InstanceBindingImpl<Stage>(injector, key, SourceProvider.UNKNOWN_SOURCE, new ConstantFactory<Stage>(Initializables.of(stage)), (Set<InjectionPoint>)ImmutableSet.of(), stage);
        injector.state.putBinding(key, stageBinding);
    }

    private static class RootModule
    implements Module {
        private RootModule() {
        }

        public void configure(Binder binder) {
            binder = binder.withSource(SourceProvider.UNKNOWN_SOURCE);
            binder.bindScope(Singleton.class, Scopes.SINGLETON);
            binder.bindScope(javax.inject.Singleton.class, Scopes.SINGLETON);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SLF4JLoggerFactory
    implements InternalFactory<Logger>,
    Provider<Logger> {
        private final Injector injector;
        private ILoggerFactory loggerFactory;

        SLF4JLoggerFactory(Injector injector) {
            this.injector = injector;
        }

        ILoggerFactory loggerFactory() {
            if (this.loggerFactory == null) {
                try {
                    this.loggerFactory = this.injector.getInstance(ILoggerFactory.class);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (this.loggerFactory == null) {
                    this.loggerFactory = org.slf4j.LoggerFactory.getILoggerFactory();
                }
            }
            return this.loggerFactory;
        }

        @Override
        public Logger get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) {
            InjectionPoint injectionPoint = dependency.getInjectionPoint();
            if (injectionPoint != null) {
                return this.loggerFactory().getLogger(injectionPoint.getMember().getDeclaringClass().getName());
            }
            return this.loggerFactory().getLogger("ROOT");
        }

        @Override
        public Logger get() {
            return this.loggerFactory().getLogger("ROOT");
        }

        public String toString() {
            return "Provider<org.slf4j.Logger>";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class LoggerFactory
    implements InternalFactory<java.util.logging.Logger>,
    Provider<java.util.logging.Logger> {
        private LoggerFactory() {
        }

        @Override
        public java.util.logging.Logger get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) {
            InjectionPoint injectionPoint = dependency.getInjectionPoint();
            return injectionPoint == null ? java.util.logging.Logger.getAnonymousLogger() : java.util.logging.Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
        }

        @Override
        public java.util.logging.Logger get() {
            return java.util.logging.Logger.getAnonymousLogger();
        }

        public String toString() {
            return "Provider<Logger>";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class InjectorFactory
    implements InternalFactory<Injector>,
    Provider<Injector> {
        private final Injector injector;

        private InjectorFactory(Injector injector) {
            this.injector = injector;
        }

        @Override
        public Injector get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) throws ErrorsException {
            return this.injector;
        }

        @Override
        public Injector get() {
            return this.injector;
        }

        public String toString() {
            return "Provider<Injector>";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class Builder {
        private final List<Element> elements = Lists.newArrayList();
        private final List<Module> modules = Lists.newArrayList();
        private State state;
        private InjectorImpl parent;
        private InjectorImpl.InjectorOptions options;
        private Stage stage;
        private PrivateElementsImpl privateElements;

        Builder() {
        }

        Builder stage(Stage stage) {
            this.stage = stage;
            return this;
        }

        Builder parent(InjectorImpl parent) {
            this.parent = parent;
            this.state = new InheritingState(parent.state);
            this.options = parent.options;
            this.stage = this.options.stage;
            return this;
        }

        Builder privateElements(PrivateElements privateElements) {
            this.privateElements = (PrivateElementsImpl)privateElements;
            this.elements.addAll(privateElements.getElements());
            return this;
        }

        void addModules(Iterable<? extends Module> modules) {
            for (Module module : modules) {
                this.modules.add(module);
            }
        }

        Stage getStage() {
            return this.options.stage;
        }

        Object lock() {
            return this.getState().lock();
        }

        List<InjectorShell> build(Initializer initializer, ProcessedBindingData bindingData, Stopwatch stopwatch, Errors errors) {
            Preconditions.checkState((this.stage != null ? 1 : 0) != 0, (Object)"Stage not initialized");
            Preconditions.checkState((this.privateElements == null || this.parent != null ? 1 : 0) != 0, (Object)"PrivateElements with no parent");
            Preconditions.checkState((this.state != null ? 1 : 0) != 0, (Object)"no state. Did you remember to lock() ?");
            if (this.parent == null) {
                this.modules.add(0, new RootModule());
            }
            this.elements.addAll(Elements.getElements(this.stage, this.modules));
            InjectorOptionsProcessor optionsProcessor = new InjectorOptionsProcessor(errors);
            optionsProcessor.process(null, this.elements);
            this.options = optionsProcessor.getOptions(this.stage, this.options);
            InjectorImpl injector = new InjectorImpl(this.parent, this.state, this.options);
            if (this.privateElements != null) {
                this.privateElements.initInjector(injector);
            }
            if (this.parent == null) {
                TypeConverterBindingProcessor.prepareBuiltInConverters(injector);
            }
            stopwatch.resetAndLog("Module execution");
            new MessageProcessor(errors).process(injector, this.elements);
            new InterceptorBindingProcessor(errors).process(injector, this.elements);
            stopwatch.resetAndLog("Interceptors creation");
            new ListenerBindingProcessor(errors).process(injector, this.elements);
            List<TypeListenerBinding> typeListenerBindings = injector.state.getTypeListenerBindings();
            injector.membersInjectorStore = new MembersInjectorStore(injector, typeListenerBindings);
            List<ProvisionListenerBinding> provisionListenerBindings = injector.state.getProvisionListenerBindings();
            injector.provisionListenerStore = new ProvisionListenerCallbackStore(provisionListenerBindings);
            stopwatch.resetAndLog("TypeListeners & ProvisionListener creation");
            new ScopeBindingProcessor(errors).process(injector, this.elements);
            stopwatch.resetAndLog("Scopes creation");
            new TypeConverterBindingProcessor(errors).process(injector, this.elements);
            stopwatch.resetAndLog("Converters creation");
            InjectorShell.bindStage(injector, this.stage);
            InjectorShell.bindInjector(injector);
            InjectorShell.bindLogger(injector);
            new BindingProcessor(errors, initializer, bindingData).process(injector, this.elements);
            new UntargettedBindingProcessor(errors, bindingData).process(injector, this.elements);
            stopwatch.resetAndLog("Binding creation");
            ArrayList injectorShells = Lists.newArrayList();
            injectorShells.add(new InjectorShell(this, this.elements, injector));
            PrivateElementProcessor processor = new PrivateElementProcessor(errors);
            processor.process(injector, this.elements);
            for (Builder builder : processor.getInjectorShellBuilders()) {
                injectorShells.addAll(builder.build(initializer, bindingData, stopwatch, errors));
            }
            stopwatch.resetAndLog("Private environment creation");
            return injectorShells;
        }

        private State getState() {
            if (this.state == null) {
                this.state = new InheritingState(State.NONE);
            }
            return this.state;
        }
    }
}

