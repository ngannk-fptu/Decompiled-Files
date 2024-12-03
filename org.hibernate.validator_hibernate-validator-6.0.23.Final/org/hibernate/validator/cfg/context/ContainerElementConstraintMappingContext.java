/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg.context;

import org.hibernate.validator.cfg.context.Cascadable;
import org.hibernate.validator.cfg.context.Constrainable;
import org.hibernate.validator.cfg.context.ConstraintMappingTarget;
import org.hibernate.validator.cfg.context.ConstructorTarget;
import org.hibernate.validator.cfg.context.ContainerElementTarget;
import org.hibernate.validator.cfg.context.MethodTarget;
import org.hibernate.validator.cfg.context.ParameterTarget;
import org.hibernate.validator.cfg.context.PropertyTarget;
import org.hibernate.validator.cfg.context.ReturnValueTarget;

public interface ContainerElementConstraintMappingContext
extends Constrainable<ContainerElementConstraintMappingContext>,
ConstraintMappingTarget,
PropertyTarget,
ConstructorTarget,
MethodTarget,
ContainerElementTarget,
ParameterTarget,
ReturnValueTarget,
Cascadable<ContainerElementConstraintMappingContext> {
}

