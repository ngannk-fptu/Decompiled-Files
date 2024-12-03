/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.query.sql2;

import java.util.Arrays;
import java.util.BitSet;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.qom.And;
import javax.jcr.query.qom.BindVariableValue;
import javax.jcr.query.qom.ChildNode;
import javax.jcr.query.qom.ChildNodeJoinCondition;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Comparison;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DescendantNode;
import javax.jcr.query.qom.DescendantNodeJoinCondition;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.EquiJoinCondition;
import javax.jcr.query.qom.FullTextSearch;
import javax.jcr.query.qom.FullTextSearchScore;
import javax.jcr.query.qom.Join;
import javax.jcr.query.qom.JoinCondition;
import javax.jcr.query.qom.Length;
import javax.jcr.query.qom.Literal;
import javax.jcr.query.qom.LowerCase;
import javax.jcr.query.qom.NodeLocalName;
import javax.jcr.query.qom.NodeName;
import javax.jcr.query.qom.Not;
import javax.jcr.query.qom.Or;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.PropertyExistence;
import javax.jcr.query.qom.PropertyValue;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelConstants;
import javax.jcr.query.qom.SameNode;
import javax.jcr.query.qom.SameNodeJoinCondition;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.Source;
import javax.jcr.query.qom.StaticOperand;
import javax.jcr.query.qom.UpperCase;

public class QOMFormatter
implements QueryObjectModelConstants {
    private static final BitSet IDENTIFIER_START;
    private static final BitSet IDENTIFIER_PART_OR_UNDERSCORE;
    private final QueryObjectModel qom;
    private final StringBuilder sb = new StringBuilder();

    private QOMFormatter(QueryObjectModel qom) {
        this.qom = qom;
    }

    public static String format(QueryObjectModel qom) throws RepositoryException {
        return new QOMFormatter(qom).format();
    }

    private String format() throws RepositoryException {
        Ordering[] orderings;
        this.append("SELECT ");
        this.append(this.qom.getColumns());
        this.append(" FROM ");
        this.append(this.qom.getSource());
        Constraint c = this.qom.getConstraint();
        if (c != null) {
            this.append(" WHERE ");
            this.append(c);
        }
        if ((orderings = this.qom.getOrderings()).length > 0) {
            this.append(" ORDER BY ");
            this.append(orderings);
        }
        return this.sb.toString();
    }

    private void append(Ordering[] orderings) {
        String comma = "";
        for (Ordering ordering : orderings) {
            this.append(comma);
            comma = ", ";
            this.append(ordering.getOperand());
            if (!"jcr.order.descending".equals(ordering.getOrder())) continue;
            this.append(" DESC");
        }
    }

    private void append(Constraint c) throws RepositoryException {
        if (c instanceof And) {
            this.append((And)c);
        } else if (c instanceof ChildNode) {
            this.append((ChildNode)c);
        } else if (c instanceof Comparison) {
            this.append((Comparison)c);
        } else if (c instanceof DescendantNode) {
            this.append((DescendantNode)c);
        } else if (c instanceof FullTextSearch) {
            this.append((FullTextSearch)c);
        } else if (c instanceof Not) {
            this.append((Not)c);
        } else if (c instanceof Or) {
            this.append((Or)c);
        } else if (c instanceof PropertyExistence) {
            this.append((PropertyExistence)c);
        } else {
            this.append((SameNode)c);
        }
    }

    private void append(And constraint) throws RepositoryException {
        String and = "";
        for (Constraint c : Arrays.asList(constraint.getConstraint1(), constraint.getConstraint2())) {
            boolean paren;
            this.append(and);
            and = " AND ";
            boolean bl = paren = c instanceof Or || c instanceof Not;
            if (paren) {
                this.append("(");
            }
            this.append(c);
            if (!paren) continue;
            this.append(")");
        }
    }

    private void append(ChildNode constraint) {
        this.append("ISCHILDNODE(");
        this.appendName(constraint.getSelectorName());
        this.append(", ");
        this.appendPath(constraint.getParentPath());
        this.append(")");
    }

    private void append(Comparison constraint) throws RepositoryException {
        this.append(constraint.getOperand1());
        this.append(" ");
        this.appendOperator(constraint.getOperator());
        this.append(" ");
        this.append(constraint.getOperand2());
    }

    private void append(StaticOperand operand) throws RepositoryException {
        if (operand instanceof BindVariableValue) {
            this.append((BindVariableValue)operand);
        } else {
            this.append((Literal)operand);
        }
    }

    private void append(BindVariableValue value) {
        this.append("$");
        this.append(value.getBindVariableName());
    }

    private void append(Literal value) throws RepositoryException {
        Value v = value.getLiteralValue();
        switch (v.getType()) {
            case 2: {
                this.appendCastLiteral(v.getString(), "BINARY");
                break;
            }
            case 6: {
                this.append(v.getString());
                break;
            }
            case 5: {
                this.appendCastLiteral(v.getString(), "DATE");
                break;
            }
            case 12: {
                this.appendCastLiteral(v.getString(), "DECIMAL");
                break;
            }
            case 4: {
                this.appendCastLiteral(v.getString(), "DOUBLE");
                break;
            }
            case 3: {
                this.appendCastLiteral(v.getString(), "LONG");
                break;
            }
            case 7: {
                this.appendCastLiteral(v.getString(), "NAME");
                break;
            }
            case 8: {
                this.appendCastLiteral(v.getString(), "PATH");
                break;
            }
            case 9: {
                this.appendCastLiteral(v.getString(), "REFERENCE");
                break;
            }
            case 1: {
                this.appendStringLiteral(v.getString());
                break;
            }
            case 11: {
                this.appendCastLiteral(v.getString(), "URI");
                break;
            }
            case 10: {
                this.appendCastLiteral(v.getString(), "WEAKREFERENCE");
            }
        }
    }

    private void appendCastLiteral(String value, String propertyType) {
        this.append("CAST(");
        this.appendStringLiteral(value);
        this.append(" AS ");
        this.append(propertyType);
        this.append(")");
    }

    private void appendStringLiteral(String value) {
        this.append("'");
        this.append(value.replaceAll("'", "''"));
        this.append("'");
    }

    private void appendOperator(String operator) {
        if ("jcr.operator.equal.to".equals(operator)) {
            this.append("=");
        } else if ("jcr.operator.greater.than".equals(operator)) {
            this.append(">");
        } else if ("jcr.operator.greater.than.or.equal.to".equals(operator)) {
            this.append(">=");
        } else if ("jcr.operator.less.than".equals(operator)) {
            this.append("<");
        } else if ("jcr.operator.less.than.or.equal.to".equals(operator)) {
            this.append("<=");
        } else if ("jcr.operator.like".equals(operator)) {
            this.append("LIKE");
        } else {
            this.append("<>");
        }
    }

    private void append(DynamicOperand operand) {
        if (operand instanceof FullTextSearchScore) {
            this.append((FullTextSearchScore)operand);
        } else if (operand instanceof Length) {
            this.append((Length)operand);
        } else if (operand instanceof LowerCase) {
            this.append((LowerCase)operand);
        } else if (operand instanceof NodeLocalName) {
            this.append((NodeLocalName)operand);
        } else if (operand instanceof NodeName) {
            this.append((NodeName)operand);
        } else if (operand instanceof PropertyValue) {
            this.append((PropertyValue)operand);
        } else {
            this.append((UpperCase)operand);
        }
    }

    private void append(FullTextSearchScore operand) {
        this.append("SCORE(");
        this.appendName(operand.getSelectorName());
        this.append(")");
    }

    private void append(Length operand) {
        this.append("LENGTH(");
        this.append(operand.getPropertyValue());
        this.append(")");
    }

    private void append(LowerCase operand) {
        this.append("LOWER(");
        this.append(operand.getOperand());
        this.append(")");
    }

    private void append(NodeLocalName operand) {
        this.append("LOCALNAME(");
        this.appendName(operand.getSelectorName());
        this.append(")");
    }

    private void append(NodeName operand) {
        this.append("NAME(");
        this.appendName(operand.getSelectorName());
        this.append(")");
    }

    private void append(PropertyValue operand) {
        this.appendName(operand.getSelectorName());
        this.append(".");
        this.appendName(operand.getPropertyName());
    }

    private void append(UpperCase operand) {
        this.append("UPPER(");
        this.append(operand.getOperand());
        this.append(")");
    }

    private void append(DescendantNode constraint) {
        this.append("ISDESCENDANTNODE(");
        this.appendName(constraint.getSelectorName());
        this.append(", ");
        this.appendPath(constraint.getAncestorPath());
        this.append(")");
    }

    private void append(FullTextSearch constraint) throws RepositoryException {
        this.append("CONTAINS(");
        this.appendName(constraint.getSelectorName());
        this.append(".");
        String propName = constraint.getPropertyName();
        if (propName == null) {
            this.append("*");
        } else {
            this.appendName(propName);
        }
        this.append(", ");
        this.append(constraint.getFullTextSearchExpression());
        this.append(")");
    }

    private void append(Not constraint) throws RepositoryException {
        boolean paren;
        this.append("NOT ");
        Constraint c = constraint.getConstraint();
        boolean bl = paren = c instanceof And || c instanceof Or;
        if (paren) {
            this.append("(");
        }
        this.append(c);
        if (paren) {
            this.append(")");
        }
    }

    private void append(Or constraint) throws RepositoryException {
        this.append(constraint.getConstraint1());
        this.append(" OR ");
        this.append(constraint.getConstraint2());
    }

    private void append(PropertyExistence constraint) {
        this.appendName(constraint.getSelectorName());
        this.append(".");
        this.appendName(constraint.getPropertyName());
        this.append(" IS NOT NULL");
    }

    private void append(SameNode constraint) {
        this.append("ISSAMENODE(");
        this.appendName(constraint.getSelectorName());
        this.append(", ");
        this.appendPath(constraint.getPath());
        this.append(")");
    }

    private void append(Column[] columns) {
        if (columns.length == 0) {
            this.append("*");
        } else {
            String comma = "";
            for (Column c : columns) {
                this.append(comma);
                comma = ", ";
                this.appendName(c.getSelectorName());
                this.append(".");
                String propName = c.getPropertyName();
                if (propName != null) {
                    this.appendName(propName);
                    if (c.getColumnName() == null) continue;
                    this.append(" AS ");
                    this.appendName(c.getColumnName());
                    continue;
                }
                this.append("*");
            }
        }
    }

    private void append(Source source) {
        if (source instanceof Join) {
            this.append((Join)source);
        } else {
            this.append((Selector)source);
        }
    }

    private void append(Join join) {
        this.append(join.getLeft());
        this.append(" ");
        this.appendJoinType(join.getJoinType());
        this.append(" JOIN ");
        this.append(join.getRight());
        this.append(" ON ");
        this.append(join.getJoinCondition());
    }

    private void append(JoinCondition joinCondition) {
        if (joinCondition instanceof EquiJoinCondition) {
            this.append((EquiJoinCondition)joinCondition);
        } else if (joinCondition instanceof ChildNodeJoinCondition) {
            this.append((ChildNodeJoinCondition)joinCondition);
        } else if (joinCondition instanceof DescendantNodeJoinCondition) {
            this.append((DescendantNodeJoinCondition)joinCondition);
        } else {
            this.append((SameNodeJoinCondition)joinCondition);
        }
    }

    private void append(EquiJoinCondition condition) {
        this.appendName(condition.getSelector1Name());
        this.append(".");
        this.appendName(condition.getProperty1Name());
        this.append(" = ");
        this.appendName(condition.getSelector2Name());
        this.append(".");
        this.appendName(condition.getProperty2Name());
    }

    private void append(ChildNodeJoinCondition condition) {
        this.append("ISCHILDNODE(");
        this.appendName(condition.getChildSelectorName());
        this.append(", ");
        this.appendName(condition.getParentSelectorName());
        this.append(")");
    }

    private void append(DescendantNodeJoinCondition condition) {
        this.append("ISDESCENDANTNODE(");
        this.appendName(condition.getDescendantSelectorName());
        this.append(", ");
        this.appendName(condition.getAncestorSelectorName());
        this.append(")");
    }

    private void append(SameNodeJoinCondition condition) {
        this.append("ISSAMENODE(");
        this.appendName(condition.getSelector1Name());
        this.append(", ");
        this.appendName(condition.getSelector2Name());
        if (condition.getSelector2Path() != null) {
            this.append(", ");
            this.appendPath(condition.getSelector2Path());
        }
        this.append(")");
    }

    private void appendPath(String path) {
        if (QOMFormatter.isSimpleName(path)) {
            this.append(path);
        } else {
            boolean needQuotes = path.contains(" ");
            this.append("[");
            if (needQuotes) {
                this.append("'");
            }
            this.append(path);
            if (needQuotes) {
                this.append("'");
            }
            this.append("]");
        }
    }

    private void appendJoinType(String joinType) {
        if (joinType.equals("jcr.join.type.inner")) {
            this.append("INNER");
        } else if (joinType.equals("jcr.join.type.left.outer")) {
            this.append("LEFT OUTER");
        } else {
            this.append("RIGHT OUTER");
        }
    }

    private void append(Selector selector) {
        this.appendName(selector.getNodeTypeName());
        if (!selector.getSelectorName().equals(selector.getNodeTypeName())) {
            this.append(" AS ");
            this.appendName(selector.getSelectorName());
        }
    }

    private void appendName(String name) {
        if (QOMFormatter.isSimpleName(name)) {
            this.append(name);
        } else {
            this.append("[");
            this.append(name);
            this.append("]");
        }
    }

    private static boolean isSimpleName(String name) {
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (!(i == 0 ? !IDENTIFIER_START.get(c) : !IDENTIFIER_PART_OR_UNDERSCORE.get(c))) continue;
            return false;
        }
        return true;
    }

    private void append(String s) {
        this.sb.append(s);
    }

    static {
        int c;
        IDENTIFIER_START = new BitSet();
        IDENTIFIER_PART_OR_UNDERSCORE = new BitSet();
        for (c = 97; c <= 122; c = (int)((char)(c + 1))) {
            IDENTIFIER_START.set(c);
        }
        for (c = 65; c <= 90; c = (int)((char)(c + 1))) {
            IDENTIFIER_START.set(c);
        }
        IDENTIFIER_PART_OR_UNDERSCORE.or(IDENTIFIER_START);
        for (c = 48; c <= 57; c = (int)((char)(c + 1))) {
            IDENTIFIER_PART_OR_UNDERSCORE.set(c);
        }
        IDENTIFIER_PART_OR_UNDERSCORE.set(95);
    }
}

