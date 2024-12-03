/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Scope;
import com.google.inject.internal.AbstractProcessor;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;
import com.google.inject.spi.ScopeBinding;
import java.lang.annotation.Annotation;

final class ScopeBindingProcessor
extends AbstractProcessor {
    ScopeBindingProcessor(Errors errors) {
        super(errors);
    }

    public Boolean visit(ScopeBinding command) {
        ScopeBinding existing;
        Scope scope = command.getScope();
        Class<? extends Annotation> annotationType = command.getAnnotationType();
        if (!Annotations.isScopeAnnotation(annotationType)) {
            this.errors.missingScopeAnnotation(annotationType);
        }
        if (!Annotations.isRetainedAtRuntime(annotationType)) {
            this.errors.missingRuntimeRetention(annotationType);
        }
        if ((existing = this.injector.state.getScopeBinding((Class)Preconditions.checkNotNull(annotationType, (Object)"annotation type"))) != null) {
            this.errors.duplicateScopes(existing, annotationType, scope);
        } else {
            Preconditions.checkNotNull((Object)scope, (Object)"scope");
            this.injector.state.putScopeBinding(annotationType, command);
        }
        return true;
    }
}

