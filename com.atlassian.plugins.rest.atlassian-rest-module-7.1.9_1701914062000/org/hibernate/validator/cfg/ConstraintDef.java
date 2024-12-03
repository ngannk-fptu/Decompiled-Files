/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Payload
 */
package org.hibernate.validator.cfg;

import java.lang.annotation.Annotation;
import javax.validation.Payload;
import org.hibernate.validator.cfg.AnnotationDef;

public abstract class ConstraintDef<C extends ConstraintDef<C, A>, A extends Annotation>
extends AnnotationDef<C, A> {
    protected ConstraintDef(Class<A> constraintType) {
        super(constraintType);
    }

    protected ConstraintDef(ConstraintDef<?, A> original) {
        super(original);
    }

    private C getThis() {
        return (C)this;
    }

    public C message(String message) {
        this.addParameter("message", message);
        return this.getThis();
    }

    public C groups(Class<?> ... groups) {
        this.addParameter("groups", groups);
        return this.getThis();
    }

    public C payload(Class<? extends Payload> ... payload) {
        this.addParameter("payload", payload);
        return this.getThis();
    }
}

