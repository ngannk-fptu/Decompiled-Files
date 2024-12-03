/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.Join;
import javax.jcr.query.qom.JoinCondition;
import javax.jcr.query.qom.Source;
import org.apache.jackrabbit.commons.query.qom.JoinType;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.JoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;
import org.apache.jackrabbit.spi.commons.query.qom.SelectorImpl;
import org.apache.jackrabbit.spi.commons.query.qom.SourceImpl;

public class JoinImpl
extends SourceImpl
implements Join {
    private final SourceImpl left;
    private final SourceImpl right;
    private final JoinType joinType;
    private final JoinConditionImpl joinCondition;

    JoinImpl(NamePathResolver resolver, SourceImpl left, SourceImpl right, JoinType joinType, JoinConditionImpl joinCondition) {
        super(resolver);
        this.left = left;
        this.right = right;
        this.joinType = joinType;
        this.joinCondition = joinCondition;
    }

    public JoinType getJoinTypeInstance() {
        return this.joinType;
    }

    @Override
    public Source getLeft() {
        return this.left;
    }

    @Override
    public Source getRight() {
        return this.right;
    }

    @Override
    public String getJoinType() {
        return this.joinType.toString();
    }

    @Override
    public JoinCondition getJoinCondition() {
        return this.joinCondition;
    }

    @Override
    public SelectorImpl[] getSelectors() {
        SelectorImpl[] leftSelectors = this.left.getSelectors();
        SelectorImpl[] rightSelectors = this.right.getSelectors();
        SelectorImpl[] both = new SelectorImpl[leftSelectors.length + rightSelectors.length];
        System.arraycopy(leftSelectors, 0, both, 0, leftSelectors.length);
        System.arraycopy(rightSelectors, 0, both, leftSelectors.length, rightSelectors.length);
        return both;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        return this.joinType.formatSql(this.left, this.right, this.joinCondition);
    }
}

