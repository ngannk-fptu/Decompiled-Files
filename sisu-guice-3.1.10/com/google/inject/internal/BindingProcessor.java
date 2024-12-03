/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.internal.AbstractBindingProcessor;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.BoundProviderFactory;
import com.google.inject.internal.ConstantFactory;
import com.google.inject.internal.ConstructorBindingImpl;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.ExposedBindingImpl;
import com.google.inject.internal.ExposedKeyFactory;
import com.google.inject.internal.FactoryProxy;
import com.google.inject.internal.Initializable;
import com.google.inject.internal.Initializer;
import com.google.inject.internal.InstanceBindingImpl;
import com.google.inject.internal.InternalFactory;
import com.google.inject.internal.InternalFactoryToInitializableAdapter;
import com.google.inject.internal.LinkedBindingImpl;
import com.google.inject.internal.LinkedProviderBindingImpl;
import com.google.inject.internal.ProcessedBindingData;
import com.google.inject.internal.ProviderInstanceBindingImpl;
import com.google.inject.internal.ProviderMethod;
import com.google.inject.internal.Scoping;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderKeyBinding;
import com.google.inject.spi.UntargettedBinding;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class BindingProcessor
extends AbstractBindingProcessor {
    private final Initializer initializer;

    BindingProcessor(Errors errors, Initializer initializer, ProcessedBindingData bindingData) {
        super(errors, bindingData);
        this.initializer = initializer;
    }

    @Override
    public <T> Boolean visit(Binding<T> command) {
        Class<T> rawType = command.getKey().getTypeLiteral().getRawType();
        if (Void.class.equals(rawType)) {
            if (command instanceof ProviderInstanceBinding && ((ProviderInstanceBinding)command).getProviderInstance() instanceof ProviderMethod) {
                this.errors.voidProviderMethod();
            } else {
                this.errors.missingConstantValues();
            }
            return true;
        }
        if (rawType == Provider.class) {
            this.errors.bindingToProvider();
            return true;
        }
        return (Boolean)command.acceptTargetVisitor(new AbstractBindingProcessor.Processor<T, Boolean>((BindingImpl)command){

            @Override
            public Boolean visit(ConstructorBinding<? extends T> binding) {
                this.prepareBinding();
                try {
                    ConstructorBindingImpl onInjector = ConstructorBindingImpl.create(BindingProcessor.this.injector, this.key, binding.getConstructor(), this.source, this.scoping, BindingProcessor.this.errors, false, false);
                    this.scheduleInitialization(onInjector);
                    BindingProcessor.this.putBinding(onInjector);
                }
                catch (ErrorsException e) {
                    BindingProcessor.this.errors.merge(e.getErrors());
                    BindingProcessor.this.putBinding(BindingProcessor.this.invalidBinding(BindingProcessor.this.injector, this.key, this.source));
                }
                return true;
            }

            @Override
            public Boolean visit(InstanceBinding<? extends T> binding) {
                this.prepareBinding();
                Set<InjectionPoint> injectionPoints = binding.getInjectionPoints();
                Object instance = binding.getInstance();
                Initializable ref = BindingProcessor.this.initializer.requestInjection(BindingProcessor.this.injector, instance, binding, this.source, injectionPoints);
                ConstantFactory factory = new ConstantFactory(ref);
                InternalFactory scopedFactory = Scoping.scope(this.key, BindingProcessor.this.injector, factory, this.source, this.scoping);
                BindingProcessor.this.putBinding(new InstanceBindingImpl(BindingProcessor.this.injector, this.key, this.source, scopedFactory, injectionPoints, instance));
                return true;
            }

            @Override
            public Boolean visit(ProviderInstanceBinding<? extends T> binding) {
                this.prepareBinding();
                Provider provider = binding.getProviderInstance();
                Set<InjectionPoint> injectionPoints = binding.getInjectionPoints();
                Initializable initializable = BindingProcessor.this.initializer.requestInjection(BindingProcessor.this.injector, provider, null, this.source, injectionPoints);
                InternalFactoryToInitializableAdapter factory = new InternalFactoryToInitializableAdapter(initializable, this.source, !BindingProcessor.this.injector.options.disableCircularProxies, BindingProcessor.this.injector.provisionListenerStore.get(binding));
                InternalFactory scopedFactory = Scoping.scope(this.key, BindingProcessor.this.injector, factory, this.source, this.scoping);
                BindingProcessor.this.putBinding(new ProviderInstanceBindingImpl(BindingProcessor.this.injector, this.key, this.source, scopedFactory, this.scoping, provider, injectionPoints));
                return true;
            }

            @Override
            public Boolean visit(ProviderKeyBinding<? extends T> binding) {
                this.prepareBinding();
                Key providerKey = binding.getProviderKey();
                BoundProviderFactory boundProviderFactory = new BoundProviderFactory(BindingProcessor.this.injector, providerKey, this.source, !BindingProcessor.this.injector.options.disableCircularProxies, BindingProcessor.this.injector.provisionListenerStore.get(binding));
                BindingProcessor.this.bindingData.addCreationListener(boundProviderFactory);
                InternalFactory scopedFactory = Scoping.scope(this.key, BindingProcessor.this.injector, boundProviderFactory, this.source, this.scoping);
                BindingProcessor.this.putBinding(new LinkedProviderBindingImpl(BindingProcessor.this.injector, this.key, this.source, scopedFactory, this.scoping, providerKey));
                return true;
            }

            @Override
            public Boolean visit(LinkedKeyBinding<? extends T> binding) {
                this.prepareBinding();
                Key linkedKey = binding.getLinkedKey();
                if (this.key.equals(linkedKey)) {
                    BindingProcessor.this.errors.recursiveBinding();
                }
                FactoryProxy factory = new FactoryProxy(BindingProcessor.this.injector, this.key, linkedKey, this.source);
                BindingProcessor.this.bindingData.addCreationListener(factory);
                InternalFactory scopedFactory = Scoping.scope(this.key, BindingProcessor.this.injector, factory, this.source, this.scoping);
                BindingProcessor.this.putBinding(new LinkedBindingImpl(BindingProcessor.this.injector, this.key, this.source, scopedFactory, this.scoping, linkedKey));
                return true;
            }

            @Override
            public Boolean visit(UntargettedBinding<? extends T> untargetted) {
                return false;
            }

            @Override
            public Boolean visit(ExposedBinding<? extends T> binding) {
                throw new IllegalArgumentException("Cannot apply a non-module element");
            }

            @Override
            public Boolean visit(ConvertedConstantBinding<? extends T> binding) {
                throw new IllegalArgumentException("Cannot apply a non-module element");
            }

            @Override
            public Boolean visit(ProviderBinding<? extends T> binding) {
                throw new IllegalArgumentException("Cannot apply a non-module element");
            }

            @Override
            protected Boolean visitOther(Binding<? extends T> binding) {
                throw new IllegalStateException("BindingProcessor should override all visitations");
            }
        });
    }

    @Override
    public Boolean visit(PrivateElements privateElements) {
        for (Key<?> key : privateElements.getExposedKeys()) {
            this.bindExposed(privateElements, key);
        }
        return false;
    }

    private <T> void bindExposed(PrivateElements privateElements, Key<T> key) {
        ExposedKeyFactory<T> exposedKeyFactory = new ExposedKeyFactory<T>(key, privateElements);
        this.bindingData.addCreationListener(exposedKeyFactory);
        this.putBinding(new ExposedBindingImpl<T>(this.injector, privateElements.getExposedSource(key), key, exposedKeyFactory, privateElements));
    }
}

