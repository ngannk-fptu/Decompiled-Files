/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.context;

import org.hibernate.validator.cfg.context.AnnotationIgnoreOptions;
import org.hibernate.validator.cfg.context.AnnotationProcessingOptions;
import org.hibernate.validator.cfg.context.Constrainable;
import org.hibernate.validator.cfg.context.ConstraintMappingTarget;
import org.hibernate.validator.cfg.context.ConstructorTarget;
import org.hibernate.validator.cfg.context.MethodTarget;
import org.hibernate.validator.cfg.context.PropertyTarget;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

public interface TypeConstraintMappingContext<C>
extends Constrainable<TypeConstraintMappingContext<C>>,
ConstraintMappingTarget,
PropertyTarget,
MethodTarget,
ConstructorTarget,
AnnotationProcessingOptions<TypeConstraintMappingContext<C>>,
AnnotationIgnoreOptions<TypeConstraintMappingContext<C>> {
    public TypeConstraintMappingContext<C> ignoreAllAnnotations();

    public TypeConstraintMappingContext<C> defaultGroupSequence(Class<?> ... var1);

    public TypeConstraintMappingContext<C> defaultGroupSequenceProviderClass(Class<? extends DefaultGroupSequenceProvider<? super C>> var1);
}

