/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.StaticOperand;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.AbstractQOMNode;

public abstract class StaticOperandImpl
extends AbstractQOMNode
implements StaticOperand {
    public StaticOperandImpl(NamePathResolver resolver) {
        super(resolver);
    }
}

