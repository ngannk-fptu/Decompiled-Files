/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.AnnotatedElementBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Message;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class PrivateModule
implements Module {
    private PrivateBinder binder;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final synchronized void configure(Binder binder) {
        $Preconditions.checkState(this.binder == null, "Re-entry is not allowed.");
        this.binder = (PrivateBinder)binder.skipSources(PrivateModule.class);
        try {
            this.configure();
        }
        finally {
            this.binder = null;
        }
    }

    protected abstract void configure();

    protected final <T> void expose(Key<T> key) {
        this.binder.expose(key);
    }

    protected final AnnotatedElementBuilder expose(Class<?> type) {
        return this.binder.expose(type);
    }

    protected final AnnotatedElementBuilder expose(TypeLiteral<?> type) {
        return this.binder.expose(type);
    }

    protected final PrivateBinder binder() {
        return this.binder;
    }

    protected final void bindScope(Class<? extends Annotation> scopeAnnotation, Scope scope) {
        this.binder.bindScope(scopeAnnotation, scope);
    }

    protected final <T> LinkedBindingBuilder<T> bind(Key<T> key) {
        return this.binder.bind(key);
    }

    protected final <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
        return this.binder.bind(typeLiteral);
    }

    protected final <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz) {
        return this.binder.bind(clazz);
    }

    protected final AnnotatedConstantBindingBuilder bindConstant() {
        return this.binder.bindConstant();
    }

    protected final void install(Module module) {
        this.binder.install(module);
    }

    protected final void addError(String message, Object ... arguments) {
        this.binder.addError(message, arguments);
    }

    protected final void addError(Throwable t) {
        this.binder.addError(t);
    }

    protected final void addError(Message message) {
        this.binder.addError(message);
    }

    protected final void requestInjection(Object instance) {
        this.binder.requestInjection(instance);
    }

    protected final void requestStaticInjection(Class<?> ... types) {
        this.binder.requestStaticInjection(types);
    }

    protected final void bindInterceptor(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor ... interceptors) {
        this.binder.bindInterceptor(classMatcher, methodMatcher, interceptors);
    }

    protected final void requireBinding(Key<?> key) {
        this.binder.getProvider(key);
    }

    protected final void requireBinding(Class<?> type) {
        this.binder.getProvider(type);
    }

    protected final <T> Provider<T> getProvider(Key<T> key) {
        return this.binder.getProvider(key);
    }

    protected final <T> Provider<T> getProvider(Class<T> type) {
        return this.binder.getProvider(type);
    }

    protected final void convertToTypes(Matcher<? super TypeLiteral<?>> typeMatcher, TypeConverter converter) {
        this.binder.convertToTypes(typeMatcher, converter);
    }

    protected final Stage currentStage() {
        return this.binder.currentStage();
    }

    protected <T> MembersInjector<T> getMembersInjector(Class<T> type) {
        return this.binder.getMembersInjector(type);
    }

    protected <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> type) {
        return this.binder.getMembersInjector(type);
    }

    protected void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener) {
        this.binder.bindListener(typeMatcher, listener);
    }
}

