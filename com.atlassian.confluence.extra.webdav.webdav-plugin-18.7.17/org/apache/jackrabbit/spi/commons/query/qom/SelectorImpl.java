/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.Selector;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;
import org.apache.jackrabbit.spi.commons.query.qom.SourceImpl;

public class SelectorImpl
extends SourceImpl
implements Selector {
    private final Name nodeTypeName;
    private final Name selectorName;

    SelectorImpl(NamePathResolver resolver, Name nodeTypeName, Name selectorName) {
        super(resolver);
        this.nodeTypeName = nodeTypeName;
        this.selectorName = selectorName;
    }

    public Name getNodeTypeQName() {
        return this.nodeTypeName;
    }

    public Name getSelectorQName() {
        return this.selectorName;
    }

    @Override
    public SelectorImpl[] getSelectors() {
        return new SelectorImpl[]{this};
    }

    @Override
    public String getNodeTypeName() {
        return this.getJCRName(this.nodeTypeName);
    }

    @Override
    public String getSelectorName() {
        return this.getJCRName(this.selectorName);
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        return this.quote(this.nodeTypeName) + " AS " + this.getSelectorName();
    }
}

