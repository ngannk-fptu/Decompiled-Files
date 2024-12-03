/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.util.$Preconditions;
import com.google.inject.matcher.Matcher;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class MethodAspect {
    private final Matcher<? super Class<?>> classMatcher;
    private final Matcher<? super Method> methodMatcher;
    private final List<MethodInterceptor> interceptors;

    MethodAspect(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, List<MethodInterceptor> interceptors) {
        this.classMatcher = $Preconditions.checkNotNull(classMatcher, "class matcher");
        this.methodMatcher = $Preconditions.checkNotNull(methodMatcher, "method matcher");
        this.interceptors = $Preconditions.checkNotNull(interceptors, "interceptors");
    }

    MethodAspect(Matcher<? super Class<?>> classMatcher, Matcher<? super Method> methodMatcher, MethodInterceptor ... interceptors) {
        this(classMatcher, methodMatcher, Arrays.asList(interceptors));
    }

    boolean matches(Class<?> clazz) {
        return this.classMatcher.matches(clazz);
    }

    boolean matches(Method method) {
        return this.methodMatcher.matches(method);
    }

    List<MethodInterceptor> interceptors() {
        return this.interceptors;
    }
}

