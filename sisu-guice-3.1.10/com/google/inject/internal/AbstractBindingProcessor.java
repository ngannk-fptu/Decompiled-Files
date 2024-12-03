/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.google.inject.internal;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.AbstractProcessor;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.ExposedBindingImpl;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.ProcessedBindingData;
import com.google.inject.internal.Scoping;
import com.google.inject.internal.State;
import com.google.inject.internal.UntargettedBindingImpl;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class AbstractBindingProcessor
extends AbstractProcessor {
    private static final boolean DISABLE_MISPLACED_ANNOTATION_CHECK;
    private static final Set<Class<?>> FORBIDDEN_TYPES;
    protected final ProcessedBindingData bindingData;

    AbstractBindingProcessor(Errors errors, ProcessedBindingData bindingData) {
        super(errors);
        this.bindingData = bindingData;
    }

    protected <T> UntargettedBindingImpl<T> invalidBinding(InjectorImpl injector, Key<T> key, Object source) {
        return new UntargettedBindingImpl<T>(injector, key, source);
    }

    protected void putBinding(BindingImpl<?> binding) {
        Key<?> key;
        block6: {
            key = binding.getKey();
            Class<?> rawType = key.getTypeLiteral().getRawType();
            if (FORBIDDEN_TYPES.contains(rawType)) {
                this.errors.cannotBindToGuiceType(rawType.getSimpleName());
                return;
            }
            Binding original = this.injector.getExistingBinding((Key)key);
            if (original != null) {
                if (this.injector.state.getExplicitBinding(key) != null) {
                    try {
                        if (!this.isOkayDuplicate((BindingImpl<?>)original, binding, this.injector.state)) {
                            this.errors.bindingAlreadySet(key, ((BindingImpl)original).getSource());
                            return;
                        }
                        break block6;
                    }
                    catch (Throwable t) {
                        this.errors.errorCheckingDuplicateBinding(key, ((BindingImpl)original).getSource(), t);
                        return;
                    }
                }
                this.errors.jitBindingAlreadySet(key);
                return;
            }
        }
        this.injector.state.parent().blacklist(key, binding.getSource());
        this.injector.state.putBinding(key, binding);
    }

    private boolean isOkayDuplicate(BindingImpl<?> original, BindingImpl<?> binding, State state) {
        if (original instanceof ExposedBindingImpl) {
            ExposedBindingImpl exposed = (ExposedBindingImpl)original;
            InjectorImpl exposedFrom = (InjectorImpl)exposed.getPrivateElements().getInjector();
            return exposedFrom == binding.getInjector();
        }
        original = (BindingImpl)state.getExplicitBindingsThisLevel().get(binding.getKey());
        if (original == null) {
            return false;
        }
        return original.equals(binding);
    }

    private <T> void validateKey(Object source, Key<T> key) {
        if (!DISABLE_MISPLACED_ANNOTATION_CHECK) {
            Annotations.checkForMisplacedScopeAnnotations(key.getTypeLiteral().getRawType(), source, this.errors);
        }
    }

    static {
        boolean disableCheck;
        try {
            disableCheck = Boolean.parseBoolean(System.getProperty("guice.disable.misplaced.annotation.check", "false"));
        }
        catch (Throwable e) {
            disableCheck = false;
        }
        DISABLE_MISPLACED_ANNOTATION_CHECK = disableCheck;
        FORBIDDEN_TYPES = ImmutableSet.of(AbstractModule.class, Binder.class, Binding.class, Injector.class, Key.class, MembersInjector.class, (Object[])new Class[]{Module.class, Provider.class, Scope.class, Stage.class, TypeLiteral.class});
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    abstract class Processor<T, V>
    extends DefaultBindingTargetVisitor<T, V> {
        final Object source;
        final Key<T> key;
        final Class<? super T> rawType;
        Scoping scoping;

        Processor(BindingImpl<T> binding) {
            this.source = binding.getSource();
            this.key = binding.getKey();
            this.rawType = this.key.getTypeLiteral().getRawType();
            this.scoping = binding.getScoping();
        }

        protected void prepareBinding() {
            AbstractBindingProcessor.this.validateKey(this.source, this.key);
            this.scoping = Scoping.makeInjectable(this.scoping, AbstractBindingProcessor.this.injector, AbstractBindingProcessor.this.errors);
        }

        protected void scheduleInitialization(final BindingImpl<?> binding) {
            AbstractBindingProcessor.this.bindingData.addUninitializedBinding(new Runnable(){

                public void run() {
                    try {
                        binding.getInjector().initializeBinding(binding, AbstractBindingProcessor.this.errors.withSource(Processor.this.source));
                    }
                    catch (ErrorsException e) {
                        AbstractBindingProcessor.this.errors.merge(e.getErrors());
                    }
                }
            });
        }
    }
}

