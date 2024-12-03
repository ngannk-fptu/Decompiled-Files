/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.JoinCondition;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.AbstractQOMNode;

public abstract class JoinConditionImpl
extends AbstractQOMNode
implements JoinCondition {
    public JoinConditionImpl(NamePathResolver resolver) {
        super(resolver);
    }
}

