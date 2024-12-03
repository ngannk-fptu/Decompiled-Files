/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.DynamicOperand;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.AbstractQOMNode;

public abstract class DynamicOperandImpl
extends AbstractQOMNode
implements DynamicOperand {
    private final Name selectorName;

    public DynamicOperandImpl(NamePathResolver resolver, Name selectorName) {
        super(resolver);
        this.selectorName = selectorName;
    }

    public String getSelectorName() {
        return this.getJCRName(this.selectorName);
    }

    public Name getSelectorQName() {
        return this.selectorName;
    }
}

