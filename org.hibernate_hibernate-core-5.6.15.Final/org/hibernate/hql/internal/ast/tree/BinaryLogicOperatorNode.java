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
import java.util.Arrays;
import org.hibernate.HibernateException;
import org.hibernate.TypeMismatchException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.hql.internal.ast.tree.AbstractSelectExpression;
import org.hibernate.hql.internal.ast.tree.BinaryOperatorNode;
import org.hibernate.hql.internal.ast.tree.ExpectedTypeAwareNode;
import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.ParameterNode;
import org.hibernate.hql.internal.ast.tree.SqlFragment;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.hql.internal.ast.tree.TableReferenceNode;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class BinaryLogicOperatorNode
extends AbstractSelectExpression
implements BinaryOperatorNode {
    @Override
    public void initialize() throws SemanticException {
        Node lhs = this.getLeftHandOperand();
        if (lhs == null) {
            throw new SemanticException("left-hand operand of a binary operator was null");
        }
        Node rhs = this.getRightHandOperand();
        if (rhs == null) {
            throw new SemanticException("right-hand operand of a binary operator was null");
        }
        Type lhsType = this.extractDataType(lhs);
        Type rhsType = this.extractDataType(rhs);
        if (lhsType == null) {
            lhsType = rhsType;
        }
        if (rhsType == null) {
            rhsType = lhsType;
        }
        if (ExpectedTypeAwareNode.class.isAssignableFrom(((Object)((Object)lhs)).getClass())) {
            ((ExpectedTypeAwareNode)((Object)lhs)).setExpectedType(rhsType);
        }
        if (ExpectedTypeAwareNode.class.isAssignableFrom(((Object)((Object)rhs)).getClass())) {
            ((ExpectedTypeAwareNode)((Object)rhs)).setExpectedType(lhsType);
        }
        this.mutateRowValueConstructorSyntaxesIfNecessary(lhsType, rhsType);
    }

    protected final void mutateRowValueConstructorSyntaxesIfNecessary(Type lhsType, Type rhsType) {
        SessionFactoryImplementor sessionFactory = this.getSessionFactoryHelper().getFactory();
        if (lhsType != null && rhsType != null) {
            int lhsColumnSpan = this.getColumnSpan(lhsType, sessionFactory);
            if (lhsColumnSpan != this.getColumnSpan(rhsType, sessionFactory)) {
                throw new TypeMismatchException("left and right hand sides of a binary logic operator were incompatible [" + lhsType.getName() + " : " + rhsType.getName() + "]");
            }
            if (lhsColumnSpan > 1 && !this.useRowValueConstructorSyntax(sessionFactory.getDialect())) {
                this.mutateRowValueConstructorSyntax(lhsColumnSpan);
            }
        }
    }

    private boolean useRowValueConstructorSyntax(Dialect dialect) {
        if (this.isInsideSetClause()) {
            return dialect.supportsRowValueConstructorSyntaxInSet();
        }
        return dialect.supportsRowValueConstructorSyntax();
    }

    private int getColumnSpan(Type type, SessionFactoryImplementor sfi) {
        int columnSpan = type.getColumnSpan(sfi);
        if (columnSpan == 0 && type instanceof OneToOneType) {
            columnSpan = ((OneToOneType)type).getIdentifierOrUniqueKeyType(sfi).getColumnSpan(sfi);
        }
        return columnSpan;
    }

    private void mutateRowValueConstructorSyntax(int valueElements) {
        int comparisonType = this.getType();
        String comparisonText = this.getText();
        if (!this.isInsideSetClause()) {
            switch (comparisonType) {
                case 108: {
                    this.setType(6);
                    this.setText("AND");
                    break;
                }
                case 115: {
                    this.setType(41);
                    this.setText("OR");
                    break;
                }
                default: {
                    throw new QuerySyntaxException(comparisonText + " operator not supported on composite types.");
                }
            }
        }
        String[] lhsElementTexts = BinaryLogicOperatorNode.extractMutationTexts(this.getLeftHandOperand(), valueElements);
        String[] rhsElementTexts = BinaryLogicOperatorNode.extractMutationTexts(this.getRightHandOperand(), valueElements);
        ParameterSpecification lhsEmbeddedCompositeParameterSpecification = this.getLeftHandOperand() == null || !ParameterNode.class.isInstance((Object)this.getLeftHandOperand()) ? null : ((ParameterNode)this.getLeftHandOperand()).getHqlParameterSpecification();
        ParameterSpecification rhsEmbeddedCompositeParameterSpecification = this.getRightHandOperand() == null || !ParameterNode.class.isInstance((Object)this.getRightHandOperand()) ? null : ((ParameterNode)this.getRightHandOperand()).getHqlParameterSpecification();
        this.translate(valueElements, comparisonType, comparisonText, lhsElementTexts, rhsElementTexts, lhsEmbeddedCompositeParameterSpecification, rhsEmbeddedCompositeParameterSpecification, (AST)this);
    }

    protected void translate(int valueElements, int comparisonType, String comparisonText, String[] lhsElementTexts, String[] rhsElementTexts, ParameterSpecification lhsEmbeddedCompositeParameterSpecification, ParameterSpecification rhsEmbeddedCompositeParameterSpecification, AST container) {
        Node leftHandOperand = this.getLeftHandOperand();
        Node rightHandOperand = this.getRightHandOperand();
        for (int i = valueElements - 1; i > 0; --i) {
            if (i == 1) {
                AST op1 = this.isInsideSetClause() ? container : this.getASTFactory().create(comparisonType, comparisonText);
                SqlFragment lhs1 = (SqlFragment)this.getASTFactory().create(150, lhsElementTexts[0]);
                SqlFragment rhs1 = (SqlFragment)this.getASTFactory().create(150, rhsElementTexts[0]);
                BinaryLogicOperatorNode.copyReferencedTables(leftHandOperand, lhs1);
                BinaryLogicOperatorNode.copyReferencedTables(rightHandOperand, rhs1);
                op1.setFirstChild((AST)lhs1);
                lhs1.setNextSibling((AST)rhs1);
                AST op2 = this.getASTFactory().create(comparisonType, comparisonText);
                SqlFragment lhs2 = (SqlFragment)this.getASTFactory().create(150, lhsElementTexts[1]);
                SqlFragment rhs2 = (SqlFragment)this.getASTFactory().create(150, rhsElementTexts[1]);
                BinaryLogicOperatorNode.copyReferencedTables(leftHandOperand, lhs2);
                BinaryLogicOperatorNode.copyReferencedTables(rightHandOperand, rhs2);
                op2.setFirstChild((AST)lhs2);
                lhs2.setNextSibling((AST)rhs2);
                op1.setNextSibling(op2);
                if (!this.isInsideSetClause()) {
                    container.setFirstChild(op1);
                }
                if (lhsEmbeddedCompositeParameterSpecification != null) {
                    lhs1.addEmbeddedParameter(lhsEmbeddedCompositeParameterSpecification);
                }
                if (rhsEmbeddedCompositeParameterSpecification == null) continue;
                lhs1.addEmbeddedParameter(rhsEmbeddedCompositeParameterSpecification);
                continue;
            }
            AST op = this.getASTFactory().create(comparisonType, comparisonText);
            SqlFragment lhs = (SqlFragment)this.getASTFactory().create(150, lhsElementTexts[i]);
            SqlFragment rhs = (SqlFragment)this.getASTFactory().create(150, rhsElementTexts[i]);
            BinaryLogicOperatorNode.copyReferencedTables(leftHandOperand, lhs);
            BinaryLogicOperatorNode.copyReferencedTables(rightHandOperand, rhs);
            op.setFirstChild((AST)lhs);
            lhs.setNextSibling((AST)rhs);
            AST newContainer = this.getASTFactory().create(container.getType(), container.getText());
            container.setFirstChild(newContainer);
            newContainer.setNextSibling(op);
            container = newContainer;
        }
    }

    private boolean isInsideSetClause() {
        return this.getWalker().getCurrentClauseType() == 47;
    }

    private static void copyReferencedTables(Node from, SqlFragment to) {
        if (from instanceof TableReferenceNode) {
            TableReferenceNode tableReferenceNode = (TableReferenceNode)((Object)from);
            to.setReferencedTables(tableReferenceNode.getReferencedTables());
        }
    }

    protected static String[] extractMutationTexts(Node operand, int count) {
        if (operand instanceof ParameterNode) {
            Object[] rtn = new String[count];
            Arrays.fill(rtn, "?");
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
        throw new HibernateException("dont know how to extract row value elements from node : " + (Object)((Object)operand));
    }

    protected Type extractDataType(Node operand) {
        Type type = null;
        if (operand instanceof SqlNode) {
            type = ((SqlNode)operand).getDataType();
        }
        if (type == null && operand instanceof ExpectedTypeAwareNode) {
            type = ((ExpectedTypeAwareNode)((Object)operand)).getExpectedType();
        }
        return type;
    }

    @Override
    public Type getDataType() {
        return StandardBasicTypes.BOOLEAN;
    }

    @Override
    public Node getLeftHandOperand() {
        return (Node)this.getFirstChild();
    }

    @Override
    public Node getRightHandOperand() {
        return (Node)this.getFirstChild().getNextSibling();
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        ColumnHelper.generateSingleScalarColumn(this, i);
    }
}

