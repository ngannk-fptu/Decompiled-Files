/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import org.hibernate.hql.internal.ast.tree.ExpectedTypeAwareNode;
import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.OperatorNode;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class BetweenOperatorNode
extends SqlNode
implements OperatorNode {
    @Override
    public void initialize() throws SemanticException {
        Node fixture = this.getFixtureOperand();
        if (fixture == null) {
            throw new SemanticException("fixture operand of a between operator was null");
        }
        Node low = this.getLowOperand();
        if (low == null) {
            throw new SemanticException("low operand of a between operator was null");
        }
        Node high = this.getHighOperand();
        if (high == null) {
            throw new SemanticException("high operand of a between operator was null");
        }
        Type expectedType = null;
        if (fixture instanceof SqlNode) {
            expectedType = ((SqlNode)fixture).getDataType();
        }
        if (expectedType == null && low instanceof SqlNode) {
            expectedType = ((SqlNode)low).getDataType();
        }
        if (expectedType == null && high instanceof SqlNode) {
            expectedType = ((SqlNode)high).getDataType();
        }
        if (fixture instanceof ExpectedTypeAwareNode) {
            ((ExpectedTypeAwareNode)((Object)fixture)).setExpectedType(expectedType);
        }
        if (low instanceof ExpectedTypeAwareNode) {
            ((ExpectedTypeAwareNode)((Object)low)).setExpectedType(expectedType);
        }
        if (high instanceof ExpectedTypeAwareNode) {
            ((ExpectedTypeAwareNode)((Object)high)).setExpectedType(expectedType);
        }
    }

    @Override
    public Type getDataType() {
        return StandardBasicTypes.BOOLEAN;
    }

    public Node getFixtureOperand() {
        return (Node)this.getFirstChild();
    }

    public Node getLowOperand() {
        return (Node)this.getFirstChild().getNextSibling();
    }

    public Node getHighOperand() {
        return (Node)this.getFirstChild().getNextSibling().getNextSibling();
    }
}

