/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintViolation
 */
package org.hibernate.validator.engine;

import javax.validation.ConstraintViolation;

public interface HibernateConstraintViolation<T>
extends ConstraintViolation<T> {
    public <C> C getDynamicPayload(Class<C> var1);
}

