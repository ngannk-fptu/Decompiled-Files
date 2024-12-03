/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg;

import java.lang.annotation.Annotation;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.cfg.context.TypeConstraintMappingContext;

public interface ConstraintMapping {
    public <C> TypeConstraintMappingContext<C> type(Class<C> var1);

    public <A extends Annotation> ConstraintDefinitionContext<A> constraintDefinition(Class<A> var1);
}

