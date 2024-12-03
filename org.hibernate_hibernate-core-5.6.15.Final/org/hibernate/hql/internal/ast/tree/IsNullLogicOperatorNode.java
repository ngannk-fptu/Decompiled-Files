/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.hql.internal.ast.tree.AbstractNullnessCheckNode;

public class IsNullLogicOperatorNode
extends AbstractNullnessCheckNode {
    @Override
    protected int getExpansionConnectorType() {
        return 6;
    }

    @Override
    protected String getExpansionConnectorText() {
        return "AND";
    }
}

