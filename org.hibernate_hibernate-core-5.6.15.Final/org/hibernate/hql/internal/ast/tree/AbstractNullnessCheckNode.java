/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.collections.AST;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.tree.ExpectedTypeAwareNode;
import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.ParameterNode;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.hql.internal.ast.tree.UnaryLogicOperatorNode;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.type.Type;

public abstract class AbstractNullnessCheckNode
extends UnaryLogicOperatorNode {
    @Override
    public void initialize() {
        Type operandType = AbstractNullnessCheckNode.extractDataType(this.getOperand());
        if (operandType == null) {
            return;
        }
        SessionFactoryImplementor sessionFactory = this.getSessionFactoryHelper().getFactory();
        int operandColumnSpan = operandType.getColumnSpan(sessionFactory);
        if (operandColumnSpan > 1) {
            this.mutateRowValueConstructorSyntax(operandColumnSpan);
        }
    }

    protected abstract int getExpansionConnectorType();

    protected abstract String getExpansionConnectorText();

    private void mutateRowValueConstructorSyntax(int operandColumnSpan) {
        int comparisonType = this.getType();
        String comparisonText = this.getText();
        int expansionConnectorType = this.getExpansionConnectorType();
        String expansionConnectorText = this.getExpansionConnectorText();
        this.setType(expansionConnectorType);
        this.setText(expansionConnectorText);
        String[] mutationTexts = AbstractNullnessCheckNode.extractMutationTexts(this.getOperand(), operandColumnSpan);
        AbstractNullnessCheckNode container = this;
        for (int i = operandColumnSpan - 1; i > 0; --i) {
            if (i == 1) {
                AST op1 = this.getASTFactory().create(comparisonType, comparisonText);
                AST operand1 = this.getASTFactory().create(150, mutationTexts[0]);
                op1.setFirstChild(operand1);
                container.setFirstChild(op1);
                AST op2 = this.getASTFactory().create(comparisonType, comparisonText);
                AST operand2 = this.getASTFactory().create(150, mutationTexts[1]);
                op2.setFirstChild(operand2);
                op1.setNextSibling(op2);
                continue;
            }
            AST op = this.getASTFactory().create(comparisonType, comparisonText);
            AST operand = this.getASTFactory().create(150, mutationTexts[i]);
            op.setFirstChild(operand);
            AST newContainer = this.getASTFactory().create(expansionConnectorType, expansionConnectorText);
            container.setFirstChild(newContainer);
            newContainer.setNextSibling(op);
            container = newContainer;
        }
    }

    private static Type extractDataType(Node operand) {
        if (operand instanceof SqlNode) {
            return ((SqlNode)operand).getDataType();
        }
        if (operand instanceof ExpectedTypeAwareNode) {
            return ((ExpectedTypeAwareNode)((Object)operand)).getExpectedType();
        }
        return null;
    }

    private static String[] extractMutationTexts(Node operand, int count) {
        if (operand instanceof ParameterNode) {
            String[] rtn = new String[count];
            for (int i = 0; i < count; ++i) {
                rtn[i] = "?";
            }
            return rtn;
        }
        if (operand.getType() == 98) {
            String[] rtn = new String[operand.getNumberOfChildren()];
            int x = 0;
            for (AST node = operand.getFirstChild(); node != null; node = node.getNextSibling()) {
                rtn[x++] = node.getText();
            }
            return rtn;
        }
        if (operand instanceof SqlNode) {
            String[] splits;
            String nodeText = operand.getText();
            if (nodeText.startsWith("(")) {
                nodeText = nodeText.substring(1);
            }
            if (nodeText.endsWith(")")) {
                nodeText = nodeText.substring(0, nodeText.length() - 1);
            }
            if (count != (splits = StringHelper.split(", ", nodeText)).length) {
                throw new HibernateException("SqlNode's text did not reference expected number of columns");
            }
            return splits;
        }
        throw new HibernateException("don't know how to extract row value elements from node : " + (Object)((Object)operand));
    }
}

