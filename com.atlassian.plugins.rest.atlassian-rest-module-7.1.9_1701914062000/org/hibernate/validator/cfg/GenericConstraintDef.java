/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.cfg;

import java.lang.annotation.Annotation;
import org.hibernate.validator.cfg.ConstraintDef;

public class GenericConstraintDef<A extends Annotation>
extends ConstraintDef<GenericConstraintDef<A>, A> {
    public GenericConstraintDef(Class<A> constraintType) {
        super(constraintType);
    }

    public GenericConstraintDef<A> param(String key, Object value) {
        this.addParameter(key, value);
        return this;
    }
}

