/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.hql.internal.ast.tree.AbstractNullnessCheckNode;

public class IsNotNullLogicOperatorNode
extends AbstractNullnessCheckNode {
    @Override
    protected int getExpansionConnectorType() {
        return 41;
    }

    @Override
    protected String getExpansionConnectorText() {
        return "OR";
    }
}

