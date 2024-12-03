/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.Constraint;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.AbstractQOMNode;

public abstract class ConstraintImpl
extends AbstractQOMNode
implements Constraint {
    public ConstraintImpl(NamePathResolver resolver) {
        super(resolver);
    }
}

