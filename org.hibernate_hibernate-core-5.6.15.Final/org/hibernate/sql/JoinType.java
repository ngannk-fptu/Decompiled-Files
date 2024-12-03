/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import org.hibernate.HibernateException;

public enum JoinType {
    NONE(-666, null),
    INNER_JOIN(0, "inner"),
    LEFT_OUTER_JOIN(1, "left"),
    RIGHT_OUTER_JOIN(2, "right"),
    FULL_JOIN(4, "full");

    private final int joinTypeValue;
    private final String sqlText;

    private JoinType(int joinTypeValue, String sqlText) {
        this.joinTypeValue = joinTypeValue;
        this.sqlText = sqlText;
    }

    public int getJoinTypeValue() {
        return this.joinTypeValue;
    }

    public String getSqlText() {
        return this.sqlText;
    }

    public static JoinType parse(int joinType) {
        if (joinType < 0) {
            return NONE;
        }
        switch (joinType) {
            case 0: {
                return INNER_JOIN;
            }
            case 1: {
                return LEFT_OUTER_JOIN;
            }
            case 2: {
                return RIGHT_OUTER_JOIN;
            }
            case 4: {
                return FULL_JOIN;
            }
        }
        throw new HibernateException("unknown join type: " + joinType);
    }
}

