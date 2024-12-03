/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.context;

import org.hibernate.validator.cfg.context.AnnotationIgnoreOptions;
import org.hibernate.validator.cfg.context.Cascadable;
import org.hibernate.validator.cfg.context.Constrainable;
import org.hibernate.validator.cfg.context.ConstraintMappingTarget;
import org.hibernate.validator.cfg.context.ConstructorTarget;
import org.hibernate.validator.cfg.context.ContainerElementTarget;
import org.hibernate.validator.cfg.context.CrossParameterTarget;
import org.hibernate.validator.cfg.context.MethodTarget;
import org.hibernate.validator.cfg.context.ParameterTarget;
import org.hibernate.validator.cfg.context.ReturnValueTarget;

public interface ParameterConstraintMappingContext
extends ConstraintMappingTarget,
CrossParameterTarget,
ParameterTarget,
ReturnValueTarget,
ConstructorTarget,
MethodTarget,
ContainerElementTarget,
Constrainable<ParameterConstraintMappingContext>,
Cascadable<ParameterConstraintMappingContext>,
AnnotationIgnoreOptions<ParameterConstraintMappingContext> {
}

