/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerImpl;
import com.opensymphony.xwork2.inject.DependencyException;
import com.opensymphony.xwork2.inject.EarlyInitializable;
import com.opensymphony.xwork2.inject.ExternalContext;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.inject.InternalContext;
import com.opensymphony.xwork2.inject.InternalFactory;
import com.opensymphony.xwork2.inject.Key;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.inject.Scoped;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class ContainerBuilder {
    final Map<Key<?>, InternalFactory<?>> factories = new HashMap();
    final List<InternalFactory<?>> singletonFactories = new ArrayList();
    final List<InternalFactory<?>> earlyInitializableFactories = new ArrayList();
    final List<Class<?>> staticInjections = new ArrayList();
    boolean created;
    boolean allowDuplicates = false;
    private static final InternalFactory<Container> CONTAINER_FACTORY = new InternalFactory<Container>(){

        @Override
        public Container create(InternalContext context) {
            return context.getContainer();
        }

        @Override
        public Class<? extends Container> type() {
            return Container.class;
        }
    };
    private static final InternalFactory<Logger> LOGGER_FACTORY = new InternalFactory<Logger>(){

        @Override
        public Logger create(InternalContext context) {
            Member member = context.getExternalContext().getMember();
            return member == null ? Logger.getAnonymousLogger() : Logger.getLogger(member.getDeclaringClass().getName());
        }

        @Override
        public Class<? extends Logger> type() {
            return Logger.class;
        }
    };

    public ContainerBuilder() {
        this.factories.put(Key.newInstance(Container.class, "default"), CONTAINER_FACTORY);
        this.factories.put(Key.newInstance(Logger.class, "default"), LOGGER_FACTORY);
    }

    private <T> ContainerBuilder factory(Key<T> key, InternalFactory<? extends T> factory, Scope scope) {
        this.ensureNotCreated();
        this.checkKey(key);
        InternalFactory<? extends T> scopedFactory = scope.scopeFactory(key.getType(), key.getName(), factory);
        this.factories.put(key, scopedFactory);
        InternalFactory<? extends T> callableFactory = this.createCallableFactory(key, scopedFactory);
        if (EarlyInitializable.class.isAssignableFrom(factory.type())) {
            this.earlyInitializableFactories.add(callableFactory);
        } else if (scope == Scope.SINGLETON) {
            this.singletonFactories.add(callableFactory);
        }
        return this;
    }

    private <T> InternalFactory<T> createCallableFactory(final Key<T> key, final InternalFactory<? extends T> scopedFactory) {
        return new InternalFactory<T>(){

            @Override
            public T create(InternalContext context) {
                try {
                    context.setExternalContext(ExternalContext.newInstance(null, key, context.getContainerImpl()));
                    Object t = scopedFactory.create(context);
                    return t;
                }
                finally {
                    context.setExternalContext(null);
                }
            }

            @Override
            public Class<? extends T> type() {
                return scopedFactory.type();
            }
        };
    }

    private void checkKey(Key<?> key) {
        if (this.factories.containsKey(key) && !this.allowDuplicates) {
            throw new DependencyException("Dependency mapping for " + key + " already exists.");
        }
    }

    public <T> ContainerBuilder factory(final Class<T> type, final String name, final Factory<? extends T> factory, Scope scope) {
        InternalFactory internalFactory = new InternalFactory<T>(){

            @Override
            public T create(InternalContext context) {
                try {
                    ExternalContext externalContext = context.getExternalContext();
                    return factory.create(externalContext);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Class<? extends T> type() {
                return factory.type();
            }

            public String toString() {
                return new LinkedHashMap<String, Object>(){
                    {
                        this.put("type", type);
                        this.put("name", name);
                        this.put("factory", factory);
                    }
                }.toString();
            }
        };
        return this.factory(Key.newInstance(type, name), internalFactory, scope);
    }

    public <T> ContainerBuilder factory(Class<T> type, Factory<? extends T> factory, Scope scope) {
        return this.factory(type, "default", factory, scope);
    }

    public <T> ContainerBuilder factory(Class<T> type, String name, Factory<? extends T> factory) {
        return this.factory(type, name, factory, Scope.PROTOTYPE);
    }

    public <T> ContainerBuilder factory(Class<T> type, Factory<? extends T> factory) {
        return this.factory(type, "default", factory, Scope.PROTOTYPE);
    }

    public <T> ContainerBuilder factory(final Class<T> type, final String name, final Class<? extends T> implementation, final Scope scope) {
        InternalFactory factory = new InternalFactory<T>(){
            volatile ContainerImpl.ConstructorInjector<? extends T> constructor;

            @Override
            public T create(InternalContext context) {
                if (this.constructor == null) {
                    this.constructor = context.getContainerImpl().getConstructor(implementation);
                }
                return this.constructor.construct(context, type);
            }

            @Override
            public Class<? extends T> type() {
                return implementation;
            }

            public String toString() {
                return new LinkedHashMap<String, Object>(){
                    {
                        this.put("type", type);
                        this.put("name", name);
                        this.put("implementation", implementation);
                        this.put("scope", scope);
                    }
                }.toString();
            }
        };
        return this.factory(Key.newInstance(type, name), factory, scope);
    }

    public <T> ContainerBuilder factory(Class<T> type, String name, Class<? extends T> implementation) {
        Scoped scoped = implementation.getAnnotation(Scoped.class);
        Scope scope = scoped == null ? Scope.PROTOTYPE : scoped.value();
        return this.factory(type, name, implementation, scope);
    }

    public <T> ContainerBuilder factory(Class<T> type, Class<? extends T> implementation) {
        return this.factory(type, "default", implementation);
    }

    public <T> ContainerBuilder factory(Class<T> type) {
        return this.factory(type, "default", type);
    }

    public <T> ContainerBuilder factory(Class<T> type, String name) {
        return this.factory(type, name, type);
    }

    public <T> ContainerBuilder factory(Class<T> type, Class<? extends T> implementation, Scope scope) {
        return this.factory(type, "default", implementation, scope);
    }

    public <T> ContainerBuilder factory(Class<T> type, Scope scope) {
        return this.factory(type, "default", type, scope);
    }

    public <T> ContainerBuilder factory(Class<T> type, String name, Scope scope) {
        return this.factory(type, name, type, scope);
    }

    public <T> ContainerBuilder alias(Class<T> type, String alias) {
        return this.alias(type, "default", alias);
    }

    public <T> ContainerBuilder alias(Class<T> type, String name, String alias) {
        return this.alias(Key.newInstance(type, name), Key.newInstance(type, alias));
    }

    private <T> ContainerBuilder alias(Key<T> key, Key<T> aliasKey) {
        this.ensureNotCreated();
        this.checkKey(aliasKey);
        InternalFactory<?> scopedFactory = this.factories.get(key);
        if (scopedFactory == null) {
            throw new DependencyException("Dependency mapping for " + key + " doesn't exists.");
        }
        this.factories.put(aliasKey, scopedFactory);
        return this;
    }

    public ContainerBuilder constant(String name, String value) {
        return this.constant(String.class, name, value);
    }

    public ContainerBuilder constant(String name, int value) {
        return this.constant(Integer.TYPE, name, value);
    }

    public ContainerBuilder constant(String name, long value) {
        return this.constant(Long.TYPE, name, value);
    }

    public ContainerBuilder constant(String name, boolean value) {
        return this.constant(Boolean.TYPE, name, value);
    }

    public ContainerBuilder constant(String name, double value) {
        return this.constant(Double.TYPE, name, value);
    }

    public ContainerBuilder constant(String name, float value) {
        return this.constant(Float.TYPE, name, Float.valueOf(value));
    }

    public ContainerBuilder constant(String name, short value) {
        return this.constant(Short.TYPE, name, value);
    }

    public ContainerBuilder constant(String name, char value) {
        return this.constant(Character.TYPE, name, Character.valueOf(value));
    }

    public ContainerBuilder constant(String name, Class value) {
        return this.constant(Class.class, name, value);
    }

    public <E extends Enum<E>> ContainerBuilder constant(String name, E value) {
        return this.constant(value.getDeclaringClass(), name, value);
    }

    private <T> ContainerBuilder constant(final Class<T> type, final String name, final T value) {
        InternalFactory factory = new InternalFactory<T>(){

            @Override
            public T create(InternalContext ignored) {
                return value;
            }

            @Override
            public Class<? extends T> type() {
                return value.getClass();
            }

            public String toString() {
                return new LinkedHashMap<String, Object>(){
                    {
                        this.put("type", type);
                        this.put("name", name);
                        this.put("value", value);
                    }
                }.toString();
            }
        };
        return this.factory(Key.newInstance(type, name), factory, Scope.PROTOTYPE);
    }

    public ContainerBuilder injectStatics(Class<?> ... types) {
        this.staticInjections.addAll(Arrays.asList(types));
        return this;
    }

    public boolean contains(Class<?> type, String name) {
        return this.factories.containsKey(Key.newInstance(type, name));
    }

    public boolean contains(Class<?> type) {
        return this.contains(type, "default");
    }

    public Container create(boolean loadSingletons) {
        this.ensureNotCreated();
        this.created = true;
        ContainerImpl container = new ContainerImpl(new HashMap(this.factories));
        if (loadSingletons) {
            container.callInContext(new ContainerImpl.ContextualCallable<Void>(){

                @Override
                public Void call(InternalContext context) {
                    for (InternalFactory<?> factory : ContainerBuilder.this.singletonFactories) {
                        factory.create(context);
                    }
                    return null;
                }
            });
        }
        container.callInContext(new ContainerImpl.ContextualCallable<Void>(){

            @Override
            public Void call(InternalContext context) {
                for (InternalFactory<?> factory : ContainerBuilder.this.earlyInitializableFactories) {
                    factory.create(context);
                }
                return null;
            }
        });
        container.injectStatics(this.staticInjections);
        return container;
    }

    private void ensureNotCreated() {
        if (this.created) {
            throw new IllegalStateException("Container already created.");
        }
    }

    public void setAllowDuplicates(boolean val) {
        this.allowDuplicates = val;
    }

    public static interface Command {
        public void build(ContainerBuilder var1);
    }
}

