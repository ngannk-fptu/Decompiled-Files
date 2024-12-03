/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import java.util.HashSet;
import java.util.Set;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.JoinType;

public class OracleJoinFragment
extends JoinFragment {
    private StringBuilder afterFrom = new StringBuilder();
    private StringBuilder afterWhere = new StringBuilder();
    private static final Set OPERATORS = new HashSet();

    @Override
    public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType) {
        this.addCrossJoin(tableName, alias);
        for (int j = 0; j < fkColumns.length; ++j) {
            this.setHasThetaJoins(true);
            this.afterWhere.append(" and ").append(fkColumns[j]);
            if (joinType == JoinType.RIGHT_OUTER_JOIN || joinType == JoinType.FULL_JOIN) {
                this.afterWhere.append("(+)");
            }
            this.afterWhere.append('=').append(alias).append('.').append(pkColumns[j]);
            if (joinType != JoinType.LEFT_OUTER_JOIN && joinType != JoinType.FULL_JOIN) continue;
            this.afterWhere.append("(+)");
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
                this.setHasThetaJoins(true);
                this.afterWhere.append(fkColumns[i][j]);
                if (joinType == JoinType.RIGHT_OUTER_JOIN || joinType == JoinType.FULL_JOIN) {
                    this.afterWhere.append("(+)");
                }
                this.afterWhere.append('=').append(alias).append('.').append(pkColumns[j]);
                if (joinType == JoinType.LEFT_OUTER_JOIN || joinType == JoinType.FULL_JOIN) {
                    this.afterWhere.append("(+)");
                }
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
        OracleJoinFragment copy = new OracleJoinFragment();
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
        if (joinType == JoinType.INNER_JOIN) {
            this.addCondition(on);
        } else if (joinType == JoinType.LEFT_OUTER_JOIN) {
            this.addLeftOuterJoinCondition(on);
        } else {
            throw new UnsupportedOperationException("join type not supported by OracleJoinFragment (use Oracle9iDialect/Oracle10gDialect)");
        }
    }

    @Override
    public void addJoin(String tableName, String alias, String[][] fkColumns, String[] pkColumns, JoinType joinType, String on) {
        this.addJoin(tableName, alias, fkColumns, pkColumns, joinType);
        if (joinType == JoinType.INNER_JOIN) {
            this.addCondition(on);
        } else if (joinType == JoinType.LEFT_OUTER_JOIN) {
            this.addLeftOuterJoinCondition(on);
        } else {
            throw new UnsupportedOperationException("join type not supported by OracleJoinFragment (use Oracle9iDialect/Oracle10gDialect)");
        }
    }

    private void addLeftOuterJoinCondition(String on) {
        StringBuilder buf = new StringBuilder(on);
        for (int i = 0; i < buf.length(); ++i) {
            boolean isInsertPoint;
            char character = buf.charAt(i);
            boolean bl = isInsertPoint = OPERATORS.contains(Character.valueOf(character)) || character == ' ' && buf.length() > i + 3 && "is ".equals(buf.substring(i + 1, i + 4));
            if (!isInsertPoint) continue;
            buf.insert(i, "(+)");
            i += 3;
        }
        this.addCondition(buf.toString());
    }

    static {
        OPERATORS.add(Character.valueOf('='));
        OPERATORS.add(Character.valueOf('<'));
        OPERATORS.add(Character.valueOf('>'));
    }
}

