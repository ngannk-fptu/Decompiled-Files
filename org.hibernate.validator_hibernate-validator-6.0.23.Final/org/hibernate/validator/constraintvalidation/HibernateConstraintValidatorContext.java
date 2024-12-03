/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidatorContext
 */
package org.hibernate.validator.constraintvalidation;

import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.Incubating;

public interface HibernateConstraintValidatorContext
extends ConstraintValidatorContext {
    public HibernateConstraintValidatorContext addMessageParameter(String var1, Object var2);

    public HibernateConstraintValidatorContext addExpressionVariable(String var1, Object var2);

    public HibernateConstraintValidatorContext withDynamicPayload(Object var1);

    @Incubating
    public <C> C getConstraintValidatorPayload(Class<C> var1);
}

