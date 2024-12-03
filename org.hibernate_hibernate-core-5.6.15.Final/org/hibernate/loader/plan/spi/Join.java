/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.plan.spi.QuerySpace;

public interface Join {
    public QuerySpace getLeftHandSide();

    public QuerySpace getRightHandSide();

    public boolean isRightHandSideRequired();

    public String[] resolveAliasedLeftHandSideJoinConditionColumns(String var1);

    public String[] resolveNonAliasedRightHandSideJoinConditionColumns();

    public String getAnyAdditionalJoinConditions(String var1);
}

