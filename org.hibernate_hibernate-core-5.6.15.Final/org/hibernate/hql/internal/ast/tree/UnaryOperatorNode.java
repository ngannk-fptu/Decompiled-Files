/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.OperatorNode;

public interface UnaryOperatorNode
extends OperatorNode {
    public Node getOperand();
}

