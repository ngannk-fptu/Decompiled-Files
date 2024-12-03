/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.query.qom;

import javax.jcr.RepositoryException;
import javax.jcr.query.qom.Join;
import javax.jcr.query.qom.JoinCondition;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Source;

public enum JoinType {
    INNER("jcr.join.type.inner", "INNER JOIN"),
    LEFT("jcr.join.type.left.outer", "LEFT OUTER JOIN"),
    RIGHT("jcr.join.type.right.outer", "RIGHT OUTER JOIN");

    private final String name;
    private final String sql;

    private JoinType(String name, String sql) {
        this.name = name;
        this.sql = sql;
    }

    public Join join(QueryObjectModelFactory factory, Source left, Source right, JoinCondition condition) throws RepositoryException {
        return factory.join(left, right, this.name, condition);
    }

    public String formatSql(Object left, Object right, Object condition) {
        return left + " " + this.sql + " " + right + " ON " + condition;
    }

    public String toString() {
        return this.name;
    }

    public static JoinType getJoinTypeByName(String name) throws RepositoryException {
        for (JoinType type : JoinType.values()) {
            if (!type.name.equals(name)) continue;
            return type;
        }
        throw new RepositoryException("Unknown join type name: " + name);
    }
}

