/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.context;

import org.hibernate.validator.cfg.context.AnnotationIgnoreOptions;
import org.hibernate.validator.cfg.context.CrossParameterTarget;
import org.hibernate.validator.cfg.context.ParameterTarget;
import org.hibernate.validator.cfg.context.ReturnValueTarget;

public interface ConstructorConstraintMappingContext
extends ParameterTarget,
CrossParameterTarget,
ReturnValueTarget,
AnnotationIgnoreOptions<ConstructorConstraintMappingContext> {
}

