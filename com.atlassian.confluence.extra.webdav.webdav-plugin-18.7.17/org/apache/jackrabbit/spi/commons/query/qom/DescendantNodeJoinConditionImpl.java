/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.DescendantNodeJoinCondition;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.JoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class DescendantNodeJoinConditionImpl
extends JoinConditionImpl
implements DescendantNodeJoinCondition {
    private final Name descendantSelectorName;
    private final Name ancestorSelectorName;

    DescendantNodeJoinConditionImpl(NamePathResolver resolver, Name descendantSelectorName, Name ancestorSelectorName) {
        super(resolver);
        this.descendantSelectorName = descendantSelectorName;
        this.ancestorSelectorName = ancestorSelectorName;
    }

    @Override
    public String getDescendantSelectorName() {
        return this.getJCRName(this.descendantSelectorName);
    }

    @Override
    public String getAncestorSelectorName() {
        return this.getJCRName(this.ancestorSelectorName);
    }

    public Name getDescendantSelectorQName() {
        return this.descendantSelectorName;
    }

    public Name getAncestorSelectorQName() {
        return this.ancestorSelectorName;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        String descendant = this.getDescendantSelectorName();
        String ancestor = this.getAncestorSelectorName();
        return "ISDESCENDANTNODE(" + descendant + ", " + ancestor + ")";
    }
}

