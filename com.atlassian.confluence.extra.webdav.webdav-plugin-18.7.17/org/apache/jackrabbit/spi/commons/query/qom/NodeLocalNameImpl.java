/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.NodeLocalName;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.DynamicOperandImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class NodeLocalNameImpl
extends DynamicOperandImpl
implements NodeLocalName {
    NodeLocalNameImpl(NamePathResolver resolver, Name selectorName) {
        super(resolver, selectorName);
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        return "LOCALNAME(" + this.getSelectorName() + ")";
    }
}

