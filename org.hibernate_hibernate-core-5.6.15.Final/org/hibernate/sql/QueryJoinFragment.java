/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.JoinType;

public class QueryJoinFragment
extends JoinFragment {
    private StringBuilder afterFrom = new StringBuilder();
    private StringBuilder afterWhere = new StringBuilder();
    private Dialect dialect;
    private boolean useThetaStyleInnerJoins;

    public QueryJoinFragment(Dialect dialect, boolean useThetaStyleInnerJoins) {
        this.dialect = dialect;
        this.useThetaStyleInnerJoins = useThetaStyleInnerJoins;
    }

    @Override
    public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType) {
        this.addJoin(tableName, alias, alias, fkColumns, pkColumns, joinType, null);
    }

    @Override
    public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType, String on) {
        this.addJoin(tableName, alias, alias, fkColumns, pkColumns, joinType, on);
    }

    public void addJoin(String tableName, String alias, String[][] fkColumns, String[] pkColumns, JoinType joinType) {
        this.addJoin(tableName, alias, alias, fkColumns, pkColumns, joinType, null);
    }

    @Override
    public void addJoin(String tableName, String alias, String[][] fkColumns, String[] pkColumns, JoinType joinType, String on) {
        this.addJoin(tableName, alias, alias, fkColumns, pkColumns, joinType, on);
    }

    private void addJoin(String tableName, String alias, String concreteAlias, String[] fkColumns, String[] pkColumns, JoinType joinType, String on) {
        if (!this.useThetaStyleInnerJoins || joinType != JoinType.INNER_JOIN) {
            JoinFragment jf = this.dialect.createOuterJoinFragment();
            jf.addJoin(tableName, alias, fkColumns, pkColumns, joinType, on);
            this.addFragment(jf);
        } else {
            this.addCrossJoin(tableName, alias);
            this.addCondition(concreteAlias, fkColumns, pkColumns);
            this.addCondition(on);
        }
    }

    private void addJoin(String tableName, String alias, String concreteAlias, String[][] fkColumns, String[] pkColumns, JoinType joinType, String on) {
        if (!this.useThetaStyleInnerJoins || joinType != JoinType.INNER_JOIN) {
            JoinFragment jf = this.dialect.createOuterJoinFragment();
            jf.addJoin(tableName, alias, fkColumns, pkColumns, joinType, on);
            this.addFragment(jf);
        } else {
            this.addCrossJoin(tableName, alias);
            this.addCondition(concreteAlias, fkColumns, pkColumns);
            this.addCondition(on);
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
        QueryJoinFragment copy = new QueryJoinFragment(this.dialect, this.useThetaStyleInnerJoins);
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
        for (int j = 0; j < fkColumns.length; ++j) {
            this.afterWhere.append(" and ").append(fkColumns[j]).append('=').append(alias).append('.').append(pkColumns[j]);
        }
    }

    public void addCondition(String alias, String[][] fkColumns, String[] pkColumns) {
        this.afterWhere.append(" and ");
        if (fkColumns.length > 1) {
            this.afterWhere.append("(");
        }
        for (int i = 0; i < fkColumns.length; ++i) {
            for (int j = 0; j < fkColumns[i].length; ++j) {
                this.afterWhere.append(fkColumns[i][j]).append('=').append(alias).append('.').append(pkColumns[j]);
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
    public boolean addCondition(String condition) {
        if (!StringHelper.isEmpty(condition) && this.afterFrom.toString().indexOf(condition.trim()) < 0 && this.afterWhere.toString().indexOf(condition.trim()) < 0) {
            if (!condition.startsWith(" and ")) {
                this.afterWhere.append(" and ");
            }
            this.afterWhere.append(condition);
            return true;
        }
        return false;
    }

    public void addFromFragmentString(String fromFragmentString) {
        this.afterFrom.append(fromFragmentString);
    }

    public void clearWherePart() {
        this.afterWhere.setLength(0);
    }
}

