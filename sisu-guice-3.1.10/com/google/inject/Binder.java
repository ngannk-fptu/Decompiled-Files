/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 */
package com.google.inject;

import com.google.inject.Binding;
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
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Message;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Binder {
    public void bindInterceptor(Matcher<? super Class<?>> var1, Matcher<? super Method> var2, MethodInterceptor ... var3);

    public void bindScope(Class<? extends Annotation> var1, Scope var2);

    public <T> LinkedBindingBuilder<T> bind(Key<T> var1);

    public <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> var1);

    public <T> AnnotatedBindingBuilder<T> bind(Class<T> var1);

    public AnnotatedConstantBindingBuilder bindConstant();

    public <T> void requestInjection(TypeLiteral<T> var1, T var2);

    public void requestInjection(Object var1);

    public void requestStaticInjection(Class<?> ... var1);

    public void install(Module var1);

    public Stage currentStage();

    public void addError(String var1, Object ... var2);

    public void addError(Throwable var1);

    public void addError(Message var1);

    public <T> Provider<T> getProvider(Key<T> var1);

    public <T> Provider<T> getProvider(Class<T> var1);

    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> var1);

    public <T> MembersInjector<T> getMembersInjector(Class<T> var1);

    public void convertToTypes(Matcher<? super TypeLiteral<?>> var1, TypeConverter var2);

    public void bindListener(Matcher<? super TypeLiteral<?>> var1, TypeListener var2);

    public void bindListener(Matcher<? super Binding<?>> var1, ProvisionListener ... var2);

    public Binder withSource(Object var1);

    public Binder skipSources(Class ... var1);

    public PrivateBinder newPrivateBinder();

    public void requireExplicitBindings();

    public void disableCircularProxies();

    public void requireAtInjectOnConstructors();

    public void requireExactBindingAnnotations();
}

