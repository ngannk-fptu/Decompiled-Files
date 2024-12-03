/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import java.lang.reflect.Method;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class InterceptorBinding
implements Element {
    private final Object source;
    private final Matcher<? super Class<?>> classMatcher;
    private final Matcher<? super Method> methodMatcher;
    private final $ImmutableList<MethodInterceptor> interceptors;

    InterceptorBinding(Object source, Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor[] interceptors) {
        this.source = $Preconditions.checkNotNull(source, "source");
        this.classMatcher = $Preconditions.checkNotNull(classMatcher, "classMatcher");
        this.methodMatcher = $Preconditions.checkNotNull(methodMatcher, "methodMatcher");
        this.interceptors = $ImmutableList.of(interceptors);
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    public Matcher<? super Class<?>> getClassMatcher() {
        return this.classMatcher;
    }

    public Matcher<? super Method> getMethodMatcher() {
        return this.methodMatcher;
    }

    public List<MethodInterceptor> getInterceptors() {
        return this.interceptors;
    }

    @Override
    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void applyTo(Binder binder) {
        binder.withSource(this.getSource()).bindInterceptor(this.classMatcher, this.methodMatcher, this.interceptors.toArray(new MethodInterceptor[this.interceptors.size()]));
    }
}

