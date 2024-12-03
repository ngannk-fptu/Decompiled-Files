/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql;

import org.hibernate.AssertionFailure;
import org.hibernate.sql.ANSIJoinFragment;
import org.hibernate.sql.JoinType;

public class CacheJoinFragment
extends ANSIJoinFragment {
    @Override
    public void addJoin(String rhsTableName, String rhsAlias, String[] lhsColumns, String[] rhsColumns, JoinType joinType, String on) {
        if (joinType == JoinType.FULL_JOIN) {
            throw new AssertionFailure("Cache does not support full outer joins");
        }
        super.addJoin(rhsTableName, rhsAlias, lhsColumns, rhsColumns, joinType, on);
    }
}

