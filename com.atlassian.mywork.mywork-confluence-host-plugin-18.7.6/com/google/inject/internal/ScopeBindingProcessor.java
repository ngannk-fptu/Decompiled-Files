/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Scope;
import com.google.inject.internal.AbstractProcessor;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.spi.ScopeBinding;
import java.lang.annotation.Annotation;

final class ScopeBindingProcessor
extends AbstractProcessor {
    ScopeBindingProcessor(Errors errors) {
        super(errors);
    }

    public Boolean visit(ScopeBinding command) {
        Scope existing;
        Scope scope = command.getScope();
        Class<? extends Annotation> annotationType = command.getAnnotationType();
        if (!Annotations.isScopeAnnotation(annotationType)) {
            this.errors.withSource(annotationType).missingScopeAnnotation();
        }
        if (!Annotations.isRetainedAtRuntime(annotationType)) {
            this.errors.withSource(annotationType).missingRuntimeRetention(command.getSource());
        }
        if ((existing = this.injector.state.getScope($Preconditions.checkNotNull(annotationType, "annotation type"))) != null) {
            this.errors.duplicateScopes(existing, annotationType, scope);
        } else {
            this.injector.state.putAnnotation(annotationType, $Preconditions.checkNotNull(scope, "scope"));
        }
        return true;
    }
}

