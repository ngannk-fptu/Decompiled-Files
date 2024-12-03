/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.OperatorNode;

public interface BinaryOperatorNode
extends OperatorNode {
    public Node getLeftHandOperand();

    public Node getRightHandOperand();
}

