/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.JoinType;

public class Sybase11JoinFragment
extends JoinFragment {
    private StringBuilder afterFrom = new StringBuilder();
    private StringBuilder afterWhere = new StringBuilder();

    @Override
    public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType) {
        this.addCrossJoin(tableName, alias);
        for (int j = 0; j < fkColumns.length; ++j) {
            if (joinType == JoinType.FULL_JOIN) {
                throw new UnsupportedOperationException();
            }
            this.afterWhere.append(" and ").append(fkColumns[j]).append(" ");
            if (joinType == JoinType.LEFT_OUTER_JOIN) {
                this.afterWhere.append('*');
            }
            this.afterWhere.append('=');
            if (joinType == JoinType.RIGHT_OUTER_JOIN) {
                this.afterWhere.append("*");
            }
            this.afterWhere.append(" ").append(alias).append('.').append(pkColumns[j]);
        }
    }

    public void addJoin(String tableName, String alias, String[][] fkColumns, String[] pkColumns, JoinType joinType) {
        this.addCrossJoin(tableName, alias);
        if (fkColumns.length > 1) {
            this.afterWhere.append("(");
        }
        for (int i = 0; i < fkColumns.length; ++i) {
            this.afterWhere.append(" and ");
            for (int j = 0; j < fkColumns[i].length; ++j) {
                if (joinType == JoinType.FULL_JOIN) {
                    throw new UnsupportedOperationException();
                }
                this.afterWhere.append(fkColumns[i][j]).append(" ");
                if (joinType == JoinType.LEFT_OUTER_JOIN) {
                    this.afterWhere.append('*');
                }
                this.afterWhere.append('=');
                if (joinType == JoinType.RIGHT_OUTER_JOIN) {
                    this.afterWhere.append("*");
                }
                this.afterWhere.append(" ").append(alias).append('.').append(pkColumns[j]);
                if (j >= fkColumns[i].length - 1) continue;
                this.afterWhere.append(" and ");
            }
            if (i >= fkColumns.length - 1) continue;
            this.afterWhere.append(" or ");
        }
        if (fkColumns.length > 1) {
            this.afterWhere.append(")");
        }
    }

    @Override
    public String toFromFragmentString() {
        return this.afterFrom.toString();
    }

    @Override
    public String toWhereFragmentString() {
        return this.afterWhere.toString();
    }

    @Override
    public void addJoins(String fromFragment, String whereFragment) {
        this.afterFrom.append(fromFragment);
        this.afterWhere.append(whereFragment);
    }

    @Override
    public JoinFragment copy() {
        Sybase11JoinFragment copy = new Sybase11JoinFragment();
        copy.afterFrom = new StringBuilder(this.afterFrom.toString());
        copy.afterWhere = new StringBuilder(this.afterWhere.toString());
        return copy;
    }

    public void addCondition(String alias, String[] columns, String condition) {
        for (String column : columns) {
            this.afterWhere.append(" and ").append(alias).append('.').append(column).append(condition);
        }
    }

    @Override
    public void addCrossJoin(String tableName, String alias) {
        this.afterFrom.append(", ").append(tableName).append(' ').append(alias);
    }

    @Override
    public void addCondition(String alias, String[] fkColumns, String[] pkColumns) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addCondition(String condition) {
        return this.addCondition(this.afterWhere, condition);
    }

    public void addFromFragmentString(String fromFragmentString) {
        this.afterFrom.append(fromFragmentString);
    }

    @Override
    public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType, String on) {
        this.addJoin(tableName, alias, fkColumns, pkColumns, joinType);
        this.addCondition(on);
    }

    @Override
    public void addJoin(String tableName, String alias, String[][] fkColumns, String[] pkColumns, JoinType joinType, String on) {
        this.addJoin(tableName, alias, fkColumns, pkColumns, joinType);
        this.addCondition(on);
    }
}

