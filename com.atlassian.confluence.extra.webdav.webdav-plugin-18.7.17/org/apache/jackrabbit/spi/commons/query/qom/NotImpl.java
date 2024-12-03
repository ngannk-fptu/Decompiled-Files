/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.Not;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.ConstraintImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class NotImpl
extends ConstraintImpl
implements Not {
    private final ConstraintImpl constraint;

    NotImpl(NamePathResolver resolver, ConstraintImpl constraint) {
        super(resolver);
        this.constraint = constraint;
    }

    @Override
    public Constraint getConstraint() {
        return this.constraint;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        return "NOT " + this.protect(this.constraint);
    }
}

