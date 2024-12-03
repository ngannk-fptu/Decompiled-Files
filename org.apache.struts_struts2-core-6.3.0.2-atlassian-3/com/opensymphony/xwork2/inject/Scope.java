/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.InitializableFactory;
import com.opensymphony.xwork2.inject.InternalContext;
import com.opensymphony.xwork2.inject.InternalFactory;
import java.util.concurrent.Callable;

public enum Scope {
    PROTOTYPE{

        @Override
        <T> InternalFactory<? extends T> scopeFactory(Class<T> type, String name, InternalFactory<? extends T> factory) {
            return InitializableFactory.wrapIfNeeded(factory);
        }
    }
    ,
    SINGLETON{

        @Override
        <T> InternalFactory<? extends T> scopeFactory(Class<T> type, String name, final InternalFactory<? extends T> factory) {
            return new InternalFactory<T>(){
                volatile T instance;

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public T create(InternalContext context) {
                    if (this.instance == null) {
                        Container container = context.getContainer();
                        synchronized (container) {
                            if (this.instance == null) {
                                this.instance = InitializableFactory.wrapIfNeeded(factory).create(context);
                            }
                        }
                    }
                    return this.instance;
                }

                @Override
                public Class<? extends T> type() {
                    return factory.type();
                }

                public String toString() {
                    return factory.toString();
                }
            };
        }
    }
    ,
    THREAD{

        @Override
        <T> InternalFactory<? extends T> scopeFactory(Class<T> type, String name, final InternalFactory<? extends T> factory) {
            return new InternalFactory<T>(){
                final ThreadLocal<T> threadLocal = new ThreadLocal();

                @Override
                public T create(InternalContext context) {
                    Object t = this.threadLocal.get();
                    if (t == null) {
                        t = InitializableFactory.wrapIfNeeded(factory).create(context);
                        this.threadLocal.set(t);
                    }
                    return t;
                }

                @Override
                public Class<? extends T> type() {
                    return factory.type();
                }

                public String toString() {
                    return factory.toString();
                }
            };
        }
    }
    ,
    REQUEST{

        @Override
        <T> InternalFactory<? extends T> scopeFactory(final Class<T> type, final String name, final InternalFactory<? extends T> factory) {
            return new InternalFactory<T>(){

                @Override
                public T create(InternalContext context) {
                    Strategy strategy = context.getScopeStrategy();
                    try {
                        return strategy.findInRequest(type, name, this.toCallable(context, factory));
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
                    return factory.toString();
                }
            };
        }
    }
    ,
    SESSION{

        @Override
        <T> InternalFactory<? extends T> scopeFactory(final Class<T> type, final String name, final InternalFactory<? extends T> factory) {
            return new InternalFactory<T>(){

                @Override
                public T create(InternalContext context) {
                    Strategy strategy = context.getScopeStrategy();
                    try {
                        return strategy.findInSession(type, name, this.toCallable(context, factory));
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
                    return factory.toString();
                }
            };
        }
    }
    ,
    WIZARD{

        @Override
        <T> InternalFactory<? extends T> scopeFactory(final Class<T> type, final String name, final InternalFactory<? extends T> factory) {
            return new InternalFactory<T>(){

                @Override
                public T create(InternalContext context) {
                    Strategy strategy = context.getScopeStrategy();
                    try {
                        return strategy.findInWizard(type, name, this.toCallable(context, factory));
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
                    return factory.toString();
                }
            };
        }
    };


    <T> Callable<? extends T> toCallable(InternalContext context, InternalFactory<? extends T> factory) {
        return () -> InitializableFactory.wrapIfNeeded(factory).create(context);
    }

    public static Scope fromString(String scopeStr) {
        switch (scopeStr) {
            case "prototype": {
                return PROTOTYPE;
            }
            case "request": {
                return REQUEST;
            }
            case "session": {
                return SESSION;
            }
            case "thread": {
                return THREAD;
            }
            case "wizard": {
                return WIZARD;
            }
        }
        return SINGLETON;
    }

    abstract <T> InternalFactory<? extends T> scopeFactory(Class<T> var1, String var2, InternalFactory<? extends T> var3);

    public static interface Strategy {
        public <T> T findInRequest(Class<T> var1, String var2, Callable<? extends T> var3) throws Exception;

        public <T> T findInSession(Class<T> var1, String var2, Callable<? extends T> var3) throws Exception;

        public <T> T findInWizard(Class<T> var1, String var2, Callable<? extends T> var3) throws Exception;
    }
}

