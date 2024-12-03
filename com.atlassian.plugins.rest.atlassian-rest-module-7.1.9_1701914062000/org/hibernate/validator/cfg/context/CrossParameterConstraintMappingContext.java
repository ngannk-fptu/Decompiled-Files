/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.context;

import org.hibernate.validator.cfg.context.AnnotationIgnoreOptions;
import org.hibernate.validator.cfg.context.Constrainable;
import org.hibernate.validator.cfg.context.ConstraintMappingTarget;
import org.hibernate.validator.cfg.context.ConstructorTarget;
import org.hibernate.validator.cfg.context.MethodTarget;
import org.hibernate.validator.cfg.context.ParameterTarget;
import org.hibernate.validator.cfg.context.ReturnValueTarget;

public interface CrossParameterConstraintMappingContext
extends ConstraintMappingTarget,
ConstructorTarget,
MethodTarget,
ParameterTarget,
ReturnValueTarget,
Constrainable<CrossParameterConstraintMappingContext>,
AnnotationIgnoreOptions<CrossParameterConstraintMappingContext> {
}

