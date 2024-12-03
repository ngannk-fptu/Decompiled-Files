/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.And;
import javax.jcr.query.qom.Constraint;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.ConstraintImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class AndImpl
extends ConstraintImpl
implements And {
    private final ConstraintImpl constraint1;
    private final ConstraintImpl constraint2;

    AndImpl(NamePathResolver resolver, ConstraintImpl c1, ConstraintImpl c2) {
        super(resolver);
        this.constraint1 = c1;
        this.constraint2 = c2;
    }

    @Override
    public Constraint getConstraint1() {
        return this.constraint1;
    }

    @Override
    public Constraint getConstraint2() {
        return this.constraint2;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        return this.protect(this.constraint1) + " AND " + this.protect(this.constraint2);
    }
}

