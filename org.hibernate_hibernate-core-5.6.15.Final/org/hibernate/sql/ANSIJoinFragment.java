/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import org.hibernate.AssertionFailure;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.JoinType;

public class ANSIJoinFragment
extends JoinFragment {
    private StringBuilder buffer = new StringBuilder();
    private StringBuilder conditions = new StringBuilder();

    @Override
    public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType) {
        this.addJoin(tableName, alias, fkColumns, pkColumns, joinType, null);
    }

    @Override
    public void addJoin(String rhsTableName, String rhsAlias, String[] lhsColumns, String[] rhsColumns, JoinType joinType, String on) {
        String joinString;
        switch (joinType) {
            case INNER_JOIN: {
                joinString = " inner join ";
                break;
            }
            case LEFT_OUTER_JOIN: {
                joinString = " left outer join ";
                break;
            }
            case RIGHT_OUTER_JOIN: {
                joinString = " right outer join ";
                break;
            }
            case FULL_JOIN: {
                joinString = " full outer join ";
                break;
            }
            default: {
                throw new AssertionFailure("undefined join type");
            }
        }
        this.buffer.append(joinString).append(rhsTableName).append(' ').append(rhsAlias).append(" on ");
        for (int j = 0; j < lhsColumns.length; ++j) {
            this.buffer.append(lhsColumns[j]).append('=').append(rhsAlias).append('.').append(rhsColumns[j]);
            if (j >= lhsColumns.length - 1) continue;
            this.buffer.append(" and ");
        }
        this.addCondition(this.buffer, on);
    }

    @Override
    public void addJoin(String rhsTableName, String rhsAlias, String[][] lhsColumns, String[] rhsColumns, JoinType joinType, String on) {
        String joinString;
        switch (joinType) {
            case INNER_JOIN: {
                joinString = " inner join ";
                break;
            }
            case LEFT_OUTER_JOIN: {
                joinString = " left outer join ";
                break;
            }
            case RIGHT_OUTER_JOIN: {
                joinString = " right outer join ";
                break;
            }
            case FULL_JOIN: {
                joinString = " full outer join ";
                break;
            }
            default: {
                throw new AssertionFailure("undefined join type");
            }
        }
        this.buffer.append(joinString).append(rhsTableName).append(' ').append(rhsAlias).append(" on ");
        if (lhsColumns.length > 1) {
            this.buffer.append("(");
        }
        for (int i = 0; i < lhsColumns.length; ++i) {
            for (int j = 0; j < lhsColumns[i].length; ++j) {
                this.buffer.append(lhsColumns[i][j]).append('=').append(rhsAlias).append('.').append(rhsColumns[j]);
                if (j >= lhsColumns[i].length - 1) continue;
                this.buffer.append(" and ");
            }
            if (i >= lhsColumns.length - 1) continue;
            this.buffer.append(" or ");
        }
        if (lhsColumns.length > 1) {
            this.buffer.append(")");
        }
        this.addCondition(this.buffer, on);
    }

    @Override
    public String toFromFragmentString() {
        return this.buffer.toString();
    }

    @Override
    public String toWhereFragmentString() {
        return this.conditions.toString();
    }

    @Override
    public void addJoins(String fromFragment, String whereFragment) {
        this.buffer.append(fromFragment);
    }

    @Override
    public JoinFragment copy() {
        ANSIJoinFragment copy = new ANSIJoinFragment();
        copy.buffer = new StringBuilder(this.buffer.toString());
        return copy;
    }

    public void addCondition(String alias, String[] columns, String condition) {
        for (String column : columns) {
            this.conditions.append(" and ").append(alias).append('.').append(column).append(condition);
        }
    }

    @Override
    public void addCrossJoin(String tableName, String alias) {
        this.buffer.append(", ").append(tableName).append(' ').append(alias);
    }

    @Override
    public void addCondition(String alias, String[] fkColumns, String[] pkColumns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addCondition(String condition) {
        return this.addCondition(this.conditions, condition);
    }

    public void addFromFragmentString(String fromFragmentString) {
        this.buffer.append(fromFragmentString);
    }
}

