/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import org.hibernate.internal.util.StringHelper;
import org.hibernate.sql.JoinType;

public abstract class JoinFragment {
    @Deprecated
    public static final int INNER_JOIN = JoinType.INNER_JOIN.getJoinTypeValue();
    @Deprecated
    public static final int FULL_JOIN = JoinType.FULL_JOIN.getJoinTypeValue();
    @Deprecated
    public static final int LEFT_OUTER_JOIN = JoinType.LEFT_OUTER_JOIN.getJoinTypeValue();
    @Deprecated
    public static final int RIGHT_OUTER_JOIN = JoinType.RIGHT_OUTER_JOIN.getJoinTypeValue();
    private boolean hasFilterCondition;
    private boolean hasThetaJoins;

    public abstract void addJoin(String var1, String var2, String[] var3, String[] var4, JoinType var5);

    public abstract void addJoin(String var1, String var2, String[] var3, String[] var4, JoinType var5, String var6);

    public void addJoin(String tableName, String alias, String[][] fkColumns, String[] pkColumns, JoinType joinType, String on) {
        if (fkColumns.length > 1) {
            throw new UnsupportedOperationException("The join fragment does not support multiple foreign key columns: " + this.getClass());
        }
        this.addJoin(tableName, alias, fkColumns[0], pkColumns, joinType, on);
    }

    public abstract void addCrossJoin(String var1, String var2);

    public abstract void addJoins(String var1, String var2);

    public abstract String toFromFragmentString();

    public abstract String toWhereFragmentString();

    public abstract void addCondition(String var1, String[] var2, String[] var3);

    public abstract boolean addCondition(String var1);

    public abstract JoinFragment copy();

    public void addFragment(JoinFragment ojf) {
        if (ojf.hasThetaJoins()) {
            this.hasThetaJoins = true;
        }
        this.addJoins(ojf.toFromFragmentString(), ojf.toWhereFragmentString());
    }

    protected boolean addCondition(StringBuilder buffer, String on) {
        if (StringHelper.isNotEmpty(on)) {
            if (!on.startsWith(" and")) {
                buffer.append(" and ");
            }
            buffer.append(on);
            return true;
        }
        return false;
    }

    public boolean hasFilterCondition() {
        return this.hasFilterCondition;
    }

    public void setHasFilterCondition(boolean b) {
        this.hasFilterCondition = b;
    }

    public boolean hasThetaJoins() {
        return this.hasThetaJoins;
    }

    public void setHasThetaJoins(boolean hasThetaJoins) {
        this.hasThetaJoins = hasThetaJoins;
    }
}

