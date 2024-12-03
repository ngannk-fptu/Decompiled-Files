/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.context;

import org.hibernate.validator.cfg.context.AnnotationIgnoreOptions;
import org.hibernate.validator.cfg.context.AnnotationProcessingOptions;
import org.hibernate.validator.cfg.context.Cascadable;
import org.hibernate.validator.cfg.context.Constrainable;
import org.hibernate.validator.cfg.context.ConstraintMappingTarget;
import org.hibernate.validator.cfg.context.ConstructorTarget;
import org.hibernate.validator.cfg.context.ContainerElementTarget;
import org.hibernate.validator.cfg.context.MethodTarget;
import org.hibernate.validator.cfg.context.PropertyTarget;

public interface PropertyConstraintMappingContext
extends Constrainable<PropertyConstraintMappingContext>,
ConstraintMappingTarget,
PropertyTarget,
ConstructorTarget,
MethodTarget,
ContainerElementTarget,
Cascadable<PropertyConstraintMappingContext>,
AnnotationProcessingOptions<PropertyConstraintMappingContext>,
AnnotationIgnoreOptions<PropertyConstraintMappingContext> {
}

