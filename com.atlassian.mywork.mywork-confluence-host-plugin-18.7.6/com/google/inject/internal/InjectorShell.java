/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.internal.BindingProcessor;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InheritingState;
import com.google.inject.internal.Initializer;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InjectorOptionsProcessor;
import com.google.inject.internal.InterceptorBindingProcessor;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.MembersInjectorStore;
import com.google.inject.internal.MessageProcessor;
import com.google.inject.internal.PrivateElementProcessor;
import com.google.inject.internal.PrivateElementsImpl;
import com.google.inject.internal.ProcessedBindingData;
import com.google.inject.internal.ProviderInstanceBindingImpl;
import com.google.inject.internal.ScopeBindingProcessor;
import com.google.inject.internal.Scoping;
import com.google.inject.internal.State;
import com.google.inject.internal.TypeConverterBindingProcessor;
import com.google.inject.internal.TypeListenerBindingProcessor;
import com.google.inject.internal.UntargettedBindingProcessor;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.util.$SourceProvider;
import com.google.inject.internal.util.$Stopwatch;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.TypeListenerBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
        injector.state.putBinding(key, new ProviderInstanceBindingImpl<Injector>(injector, key, $SourceProvider.UNKNOWN_SOURCE, injectorFactory, Scoping.UNSCOPED, injectorFactory, $ImmutableSet.<InjectionPoint>of()));
    }

    private static void bindLogger(InjectorImpl injector) {
        Key<Logger> key = Key.get(Logger.class);
        LoggerFactory loggerFactory = new LoggerFactory();
        injector.state.putBinding(key, new ProviderInstanceBindingImpl<Logger>(injector, key, $SourceProvider.UNKNOWN_SOURCE, loggerFactory, Scoping.UNSCOPED, loggerFactory, $ImmutableSet.<InjectionPoint>of()));
    }

    private static class RootModule
    implements Module {
        final Stage stage;

        private RootModule(Stage stage) {
            this.stage = $Preconditions.checkNotNull(stage, "stage");
        }

        public void configure(Binder binder) {
            binder = binder.withSource($SourceProvider.UNKNOWN_SOURCE);
            binder.bind(Stage.class).toInstance(this.stage);
            binder.bindScope(Singleton.class, Scopes.SINGLETON);
            binder.bindScope(javax.inject.Singleton.class, Scopes.SINGLETON);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class LoggerFactory
    implements InternalFactory<Logger>,
    Provider<Logger> {
        private LoggerFactory() {
        }

        @Override
        public Logger get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked) {
            InjectionPoint injectionPoint = dependency.getInjectionPoint();
            return injectionPoint == null ? Logger.getAnonymousLogger() : Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
        }

        @Override
        public Logger get() {
            return Logger.getAnonymousLogger();
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
        private final List<Element> elements = $Lists.newArrayList();
        private final List<Module> modules = $Lists.newArrayList();
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

        List<InjectorShell> build(Initializer initializer, ProcessedBindingData bindingData, $Stopwatch stopwatch, Errors errors) {
            $Preconditions.checkState(this.stage != null, "Stage not initialized");
            $Preconditions.checkState(this.privateElements == null || this.parent != null, "PrivateElements with no parent");
            $Preconditions.checkState(this.state != null, "no state. Did you remember to lock() ?");
            if (this.parent == null) {
                this.modules.add(0, new RootModule(this.stage));
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
                new TypeConverterBindingProcessor(errors).prepareBuiltInConverters(injector);
            }
            stopwatch.resetAndLog("Module execution");
            new MessageProcessor(errors).process(injector, this.elements);
            InterceptorBindingProcessor interceptors = new InterceptorBindingProcessor(errors);
            interceptors.process(injector, this.elements);
            stopwatch.resetAndLog("Interceptors creation");
            new TypeListenerBindingProcessor(errors).process(injector, this.elements);
            List<TypeListenerBinding> listenerBindings = injector.state.getTypeListenerBindings();
            injector.membersInjectorStore = new MembersInjectorStore(injector, listenerBindings);
            stopwatch.resetAndLog("TypeListeners creation");
            new ScopeBindingProcessor(errors).process(injector, this.elements);
            stopwatch.resetAndLog("Scopes creation");
            new TypeConverterBindingProcessor(errors).process(injector, this.elements);
            stopwatch.resetAndLog("Converters creation");
            InjectorShell.bindInjector(injector);
            InjectorShell.bindLogger(injector);
            new BindingProcessor(errors, initializer, bindingData).process(injector, this.elements);
            new UntargettedBindingProcessor(errors, bindingData).process(injector, this.elements);
            stopwatch.resetAndLog("Binding creation");
            ArrayList<InjectorShell> injectorShells = $Lists.newArrayList();
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

