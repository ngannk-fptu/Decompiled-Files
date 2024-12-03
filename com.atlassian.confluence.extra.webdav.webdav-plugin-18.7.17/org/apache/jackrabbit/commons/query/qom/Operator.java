/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.query.qom;

import javax.jcr.RepositoryException;
import javax.jcr.query.qom.Comparison;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.StaticOperand;

public enum Operator {
    EQ("jcr.operator.equal.to", "="),
    NE("jcr.operator.not.equal.to", "!=", "<>"),
    GT("jcr.operator.greater.than", ">"),
    GE("jcr.operator.greater.than.or.equal.to", ">="),
    LT("jcr.operator.less.than", "<"),
    LE("jcr.operator.less.than.or.equal.to", "<="),
    LIKE("jcr.operator.like", null, "like");

    private final String name;
    private final String xpath;
    private final String sql;

    private Operator(String name, String op) {
        this(name, op, op);
    }

    private Operator(String name, String xpath, String sql) {
        this.name = name;
        this.xpath = xpath;
        this.sql = sql;
    }

    public Comparison comparison(QueryObjectModelFactory factory, DynamicOperand left, StaticOperand right) throws RepositoryException {
        return factory.comparison(left, this.name, right);
    }

    public String formatXpath(String a, String b) {
        if (this == LIKE) {
            return "jcr:like(" + a + ", " + b + ")";
        }
        return a + " " + this.xpath + " " + b;
    }

    public String formatSql(String a, String b) {
        return a + " " + this.sql + " " + b;
    }

    public String toString() {
        return this.name;
    }

    public static String[] getAllQueryOperators() {
        return new String[]{EQ.toString(), NE.toString(), GT.toString(), GE.toString(), LT.toString(), LE.toString(), LIKE.toString()};
    }

    public static Operator getOperatorByName(String name) throws RepositoryException {
        for (Operator operator : Operator.values()) {
            if (!operator.name.equals(name)) continue;
            return operator;
        }
        throw new RepositoryException("Unknown operator name: " + name);
    }
}

