/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.cfg.context;

import java.lang.annotation.Annotation;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.cfg.context.TypeConstraintMappingContext;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;

abstract class ConstraintContextImplBase {
    protected final DefaultConstraintMapping mapping;

    public ConstraintContextImplBase(DefaultConstraintMapping mapping) {
        this.mapping = mapping;
    }

    public <C> TypeConstraintMappingContext<C> type(Class<C> type) {
        return this.mapping.type(type);
    }

    public <A extends Annotation> ConstraintDefinitionContext<A> constraintDefinition(Class<A> annotationClass) {
        return this.mapping.constraintDefinition(annotationClass);
    }
}

