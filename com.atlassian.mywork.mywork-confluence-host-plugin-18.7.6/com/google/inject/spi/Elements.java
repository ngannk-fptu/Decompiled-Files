/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.AnnotatedElementBuilder;
import com.google.inject.internal.AbstractBindingBuilder;
import com.google.inject.internal.BindingBuilder;
import com.google.inject.internal.ConstantBindingBuilderImpl;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ExposureBuilder;
import com.google.inject.internal.PrivateElementsImpl;
import com.google.inject.internal.ProviderMethodsModule;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.util.$Sets;
import com.google.inject.internal.util.$SourceProvider;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.DisableCircularProxiesOption;
import com.google.inject.spi.Element;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.InterceptorBinding;
import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.spi.Message;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.spi.RequireExplicitBindingsOption;
import com.google.inject.spi.ScopeBinding;
import com.google.inject.spi.StaticInjectionRequest;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.TypeListener;
import com.google.inject.spi.TypeListenerBinding;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.aopalliance.intercept.MethodInterceptor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Elements {
    private static final BindingTargetVisitor<Object, Object> GET_INSTANCE_VISITOR = new DefaultBindingTargetVisitor<Object, Object>(){

        @Override
        public Object visit(InstanceBinding<?> binding) {
            return binding.getInstance();
        }

        @Override
        protected Object visitOther(Binding<?> binding) {
            throw new IllegalArgumentException();
        }
    };

    public static List<Element> getElements(Module ... modules) {
        return Elements.getElements(Stage.DEVELOPMENT, Arrays.asList(modules));
    }

    public static List<Element> getElements(Stage stage, Module ... modules) {
        return Elements.getElements(stage, Arrays.asList(modules));
    }

    public static List<Element> getElements(Iterable<? extends Module> modules) {
        return Elements.getElements(Stage.DEVELOPMENT, modules);
    }

    public static List<Element> getElements(Stage stage, Iterable<? extends Module> modules) {
        RecordingBinder binder = new RecordingBinder(stage);
        for (Module module : modules) {
            binder.install(module);
        }
        return Collections.unmodifiableList(binder.elements);
    }

    public static Module getModule(final Iterable<? extends Element> elements) {
        return new Module(){

            public void configure(Binder binder) {
                for (Element element : elements) {
                    element.applyTo(binder);
                }
            }
        };
    }

    static <T> BindingTargetVisitor<T, T> getInstanceVisitor() {
        return GET_INSTANCE_VISITOR;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class RecordingBinder
    implements Binder,
    PrivateBinder {
        private final Stage stage;
        private final Set<Module> modules;
        private final List<Element> elements;
        private final Object source;
        private final $SourceProvider sourceProvider;
        private final RecordingBinder parent;
        private final PrivateElementsImpl privateElements;

        private RecordingBinder(Stage stage) {
            this.stage = stage;
            this.modules = $Sets.newHashSet();
            this.elements = $Lists.newArrayList();
            this.source = null;
            this.sourceProvider = $SourceProvider.DEFAULT_INSTANCE.plusSkippedClasses(Elements.class, RecordingBinder.class, AbstractModule.class, ConstantBindingBuilderImpl.class, AbstractBindingBuilder.class, BindingBuilder.class);
            this.parent = null;
            this.privateElements = null;
        }

        private RecordingBinder(RecordingBinder prototype, Object source, $SourceProvider sourceProvider) {
            $Preconditions.checkArgument(source == null ^ sourceProvider == null);
            this.stage = prototype.stage;
            this.modules = prototype.modules;
            this.elements = prototype.elements;
            this.source = source;
            this.sourceProvider = sourceProvider;
            this.parent = prototype.parent;
            this.privateElements = prototype.privateElements;
        }

        private RecordingBinder(RecordingBinder parent, PrivateElementsImpl privateElements) {
            this.stage = parent.stage;
            this.modules = $Sets.newHashSet();
            this.elements = privateElements.getElementsMutable();
            this.source = parent.source;
            this.sourceProvider = parent.sourceProvider;
            this.parent = parent;
            this.privateElements = privateElements;
        }

        @Override
        public void bindInterceptor(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor ... interceptors) {
            this.elements.add(new InterceptorBinding(this.getSource(), classMatcher, methodMatcher, interceptors));
        }

        @Override
        public void bindScope(Class<? extends Annotation> annotationType, Scope scope) {
            this.elements.add(new ScopeBinding(this.getSource(), annotationType, scope));
        }

        @Override
        public void requestInjection(Object instance) {
            this.requestInjection(TypeLiteral.get(instance.getClass()), instance);
        }

        @Override
        public <T> void requestInjection(TypeLiteral<T> type, T instance) {
            this.elements.add(new InjectionRequest<T>(this.getSource(), type, instance));
        }

        @Override
        public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
            MembersInjectorLookup<T> element = new MembersInjectorLookup<T>(this.getSource(), typeLiteral);
            this.elements.add(element);
            return element.getMembersInjector();
        }

        @Override
        public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
            return this.getMembersInjector(TypeLiteral.get(type));
        }

        @Override
        public void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener) {
            this.elements.add(new TypeListenerBinding(this.getSource(), listener, typeMatcher));
        }

        @Override
        public void requestStaticInjection(Class<?> ... types) {
            for (Class<?> type : types) {
                this.elements.add(new StaticInjectionRequest(this.getSource(), type));
            }
        }

        @Override
        public void install(Module module) {
            if (this.modules.add(module)) {
                PrivateBinder binder = this;
                if (module instanceof PrivateModule) {
                    binder = binder.newPrivateBinder();
                }
                try {
                    module.configure(binder);
                }
                catch (RuntimeException e) {
                    Collection<Message> messages = Errors.getMessagesFromThrowable(e);
                    if (!messages.isEmpty()) {
                        this.elements.addAll(messages);
                    }
                    this.addError(e);
                }
                binder.install(ProviderMethodsModule.forModule(module));
            }
        }

        @Override
        public Stage currentStage() {
            return this.stage;
        }

        @Override
        public void addError(String message, Object ... arguments) {
            this.elements.add(new Message(this.getSource(), Errors.format(message, arguments)));
        }

        @Override
        public void addError(Throwable t) {
            String message = "An exception was caught and reported. Message: " + t.getMessage();
            this.elements.add(new Message($ImmutableList.of(this.getSource()), message, t));
        }

        @Override
        public void addError(Message message) {
            this.elements.add(message);
        }

        public <T> AnnotatedBindingBuilder<T> bind(Key<T> key) {
            return new BindingBuilder<T>(this, this.elements, this.getSource(), key);
        }

        @Override
        public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
            return this.bind((Key)Key.get(typeLiteral));
        }

        @Override
        public <T> AnnotatedBindingBuilder<T> bind(Class<T> type) {
            return this.bind((Key)Key.get(type));
        }

        @Override
        public AnnotatedConstantBindingBuilder bindConstant() {
            return new ConstantBindingBuilderImpl(this, this.elements, this.getSource());
        }

        @Override
        public <T> Provider<T> getProvider(Key<T> key) {
            ProviderLookup<T> element = new ProviderLookup<T>(this.getSource(), key);
            this.elements.add(element);
            return element.getProvider();
        }

        @Override
        public <T> Provider<T> getProvider(Class<T> type) {
            return this.getProvider(Key.get(type));
        }

        @Override
        public void convertToTypes(Matcher<? super TypeLiteral<?>> typeMatcher, TypeConverter converter) {
            this.elements.add(new TypeConverterBinding(this.getSource(), typeMatcher, converter));
        }

        @Override
        public RecordingBinder withSource(Object source) {
            return new RecordingBinder(this, source, null);
        }

        @Override
        public RecordingBinder skipSources(Class ... classesToSkip) {
            if (this.source != null) {
                return this;
            }
            $SourceProvider newSourceProvider = this.sourceProvider.plusSkippedClasses(classesToSkip);
            return new RecordingBinder(this, null, newSourceProvider);
        }

        @Override
        public PrivateBinder newPrivateBinder() {
            PrivateElementsImpl privateElements = new PrivateElementsImpl(this.getSource());
            this.elements.add(privateElements);
            return new RecordingBinder(this, privateElements);
        }

        @Override
        public void disableCircularProxies() {
            this.elements.add(new DisableCircularProxiesOption(this.getSource()));
        }

        @Override
        public void requireExplicitBindings() {
            this.elements.add(new RequireExplicitBindingsOption(this.getSource()));
        }

        @Override
        public void expose(Key<?> key) {
            this.exposeInternal(key);
        }

        @Override
        public AnnotatedElementBuilder expose(Class<?> type) {
            return this.exposeInternal(Key.get(type));
        }

        @Override
        public AnnotatedElementBuilder expose(TypeLiteral<?> type) {
            return this.exposeInternal(Key.get(type));
        }

        private <T> AnnotatedElementBuilder exposeInternal(Key<T> key) {
            if (this.privateElements == null) {
                this.addError("Cannot expose %s on a standard binder. Exposed bindings are only applicable to private binders.", key);
                return new AnnotatedElementBuilder(){

                    @Override
                    public void annotatedWith(Class<? extends Annotation> annotationType) {
                    }

                    @Override
                    public void annotatedWith(Annotation annotation) {
                    }
                };
            }
            ExposureBuilder<T> builder = new ExposureBuilder<T>(this, this.getSource(), key);
            this.privateElements.addExposureBuilder(builder);
            return builder;
        }

        protected Object getSource() {
            return this.sourceProvider != null ? this.sourceProvider.get() : this.source;
        }

        public String toString() {
            return "Binder";
        }
    }
}

