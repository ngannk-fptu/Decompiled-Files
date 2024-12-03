/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import java.util.ArrayList;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.tree.BinaryLogicOperatorNode;
import org.hibernate.hql.internal.ast.tree.BinaryOperatorNode;
import org.hibernate.hql.internal.ast.tree.CollectionFunction;
import org.hibernate.hql.internal.ast.tree.ExpectedTypeAwareNode;
import org.hibernate.hql.internal.ast.tree.LiteralNode;
import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.ParameterNode;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.type.Type;

public class InLogicOperatorNode
extends BinaryLogicOperatorNode
implements BinaryOperatorNode {
    public Node getInList() {
        return this.getRightHandOperand();
    }

    @Override
    public void initialize() throws SemanticException {
        int rhsColumnSpan;
        SessionFactoryImplementor sessionFactory;
        Node lhs = this.getLeftHandOperand();
        if (lhs == null) {
            throw new SemanticException("left-hand operand of in operator was null");
        }
        Node inList = this.getInList();
        if (inList == null) {
            throw new SemanticException("right-hand operand of in operator was null");
        }
        if (SqlNode.class.isAssignableFrom(((Object)((Object)lhs)).getClass())) {
            Type lhsType = ((SqlNode)lhs).getDataType();
            for (AST inListChild = inList.getFirstChild(); inListChild != null; inListChild = inListChild.getNextSibling()) {
                if (ExpectedTypeAwareNode.class.isAssignableFrom(inListChild.getClass())) {
                    ((ExpectedTypeAwareNode)inListChild).setExpectedType(lhsType);
                }
                if (!CollectionFunction.class.isInstance(inListChild) || !ExpectedTypeAwareNode.class.isInstance((Object)lhs)) continue;
                Type rhsType = ((CollectionFunction)inListChild).getDataType();
                ((ExpectedTypeAwareNode)((Object)lhs)).setExpectedType(rhsType);
            }
        }
        if ((sessionFactory = this.getSessionFactoryHelper().getFactory()).getDialect().supportsRowValueConstructorSyntaxInInList()) {
            return;
        }
        Type lhsType = this.extractDataType(lhs);
        if (lhsType == null) {
            return;
        }
        int lhsColumnSpan = lhsType.getColumnSpan(sessionFactory);
        Node rhsNode = (Node)inList.getFirstChild();
        if (!this.isNodeAcceptable(rhsNode)) {
            return;
        }
        if (rhsNode == null) {
            return;
        }
        if (rhsNode.getType() == 98) {
            rhsColumnSpan = rhsNode.getNumberOfChildren();
        } else {
            Type rhsType = this.extractDataType(rhsNode);
            if (rhsType == null) {
                return;
            }
            rhsColumnSpan = rhsType.getColumnSpan(sessionFactory);
        }
        if (lhsColumnSpan > 1 && rhsColumnSpan > 1) {
            this.mutateRowValueConstructorSyntaxInInListSyntax(lhsColumnSpan, rhsColumnSpan);
        }
    }

    private boolean isNodeAcceptable(Node rhsNode) {
        return rhsNode == null || rhsNode instanceof LiteralNode || rhsNode instanceof ParameterNode || rhsNode.getType() == 98;
    }

    private void mutateRowValueConstructorSyntaxInInListSyntax(int lhsColumnSpan, int rhsColumnSpan) {
        Node rhsNode;
        boolean negated;
        String[] lhsElementTexts = InLogicOperatorNode.extractMutationTexts(this.getLeftHandOperand(), lhsColumnSpan);
        ParameterSpecification lhsEmbeddedCompositeParameterSpecification = this.getLeftHandOperand() == null || !ParameterNode.class.isInstance((Object)this.getLeftHandOperand()) ? null : ((ParameterNode)this.getLeftHandOperand()).getHqlParameterSpecification();
        boolean bl = negated = this.getType() == 88;
        if (rhsNode != null && rhsNode.getNextSibling() == null) {
            String[] rhsElementTexts = InLogicOperatorNode.extractMutationTexts(rhsNode, rhsColumnSpan);
            this.setType(negated ? 41 : 6);
            this.setText(negated ? "or" : "and");
            ParameterSpecification rhsEmbeddedCompositeParameterSpecification = rhsNode == null || !ParameterNode.class.isInstance((Object)rhsNode) ? null : ((ParameterNode)rhsNode).getHqlParameterSpecification();
            this.translate(lhsColumnSpan, negated ? 115 : 108, negated ? "<>" : "=", lhsElementTexts, rhsElementTexts, lhsEmbeddedCompositeParameterSpecification, rhsEmbeddedCompositeParameterSpecification, (AST)this);
        } else {
            ArrayList<AST> andElementsNodeList = new ArrayList<AST>();
            for (rhsNode = (Node)this.getInList().getFirstChild(); rhsNode != null; rhsNode = (Node)rhsNode.getNextSibling()) {
                String[] rhsElementTexts = InLogicOperatorNode.extractMutationTexts(rhsNode, rhsColumnSpan);
                AST group = this.getASTFactory().create(negated ? 41 : 6, negated ? "or" : "and");
                ParameterSpecification rhsEmbeddedCompositeParameterSpecification = rhsNode == null || !ParameterNode.class.isInstance((Object)rhsNode) ? null : ((ParameterNode)rhsNode).getHqlParameterSpecification();
                this.translate(lhsColumnSpan, negated ? 115 : 108, negated ? "<>" : "=", lhsElementTexts, rhsElementTexts, lhsEmbeddedCompositeParameterSpecification, rhsEmbeddedCompositeParameterSpecification, group);
                andElementsNodeList.add(group);
            }
            this.setType(negated ? 6 : 41);
            this.setText(negated ? "and" : "or");
            InLogicOperatorNode curNode = this;
            for (int i = andElementsNodeList.size() - 1; i > 1; --i) {
                AST group = this.getASTFactory().create(negated ? 6 : 41, negated ? "and" : "or");
                curNode.setFirstChild(group);
                curNode = group;
                AST and = (AST)andElementsNodeList.get(i);
                group.setNextSibling(and);
            }
            AST node0 = (AST)andElementsNodeList.get(0);
            AST node1 = (AST)andElementsNodeList.get(1);
            node0.setNextSibling(node1);
            curNode.setFirstChild(node0);
        }
    }
}

