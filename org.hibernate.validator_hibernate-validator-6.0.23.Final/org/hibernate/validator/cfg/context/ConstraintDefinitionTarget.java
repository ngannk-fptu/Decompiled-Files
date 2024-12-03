/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.context;

import java.lang.annotation.Annotation;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;

public interface ConstraintDefinitionTarget {
    public <A extends Annotation> ConstraintDefinitionContext<A> constraintDefinition(Class<A> var1);
}

