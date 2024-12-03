/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.ChildNodeJoinCondition;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.JoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class ChildNodeJoinConditionImpl
extends JoinConditionImpl
implements ChildNodeJoinCondition {
    private final Name childSelectorName;
    private final Name parentSelectorName;

    ChildNodeJoinConditionImpl(NamePathResolver resolver, Name childSelectorName, Name parentSelectorName) {
        super(resolver);
        this.childSelectorName = childSelectorName;
        this.parentSelectorName = parentSelectorName;
    }

    @Override
    public String getChildSelectorName() {
        return this.getJCRName(this.childSelectorName);
    }

    @Override
    public String getParentSelectorName() {
        return this.getJCRName(this.parentSelectorName);
    }

    public Name getChildSelectorQName() {
        return this.childSelectorName;
    }

    public Name getParentSelectorQName() {
        return this.parentSelectorName;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        String child = this.getChildSelectorName();
        String parent = this.getParentSelectorName();
        return "ISCHILDNODE(" + child + ", " + parent + ")";
    }
}

