/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.AbstractProcessor;
import com.google.inject.internal.Errors;
import com.google.inject.internal.MethodAspect;
import com.google.inject.spi.InterceptorBinding;

final class InterceptorBindingProcessor
extends AbstractProcessor {
    InterceptorBindingProcessor(Errors errors) {
        super(errors);
    }

    public Boolean visit(InterceptorBinding command) {
        this.injector.state.addMethodAspect(new MethodAspect(command.getClassMatcher(), command.getMethodMatcher(), command.getInterceptors()));
        return true;
    }
}

