/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.EnhancedProjection;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.type.Type;

public abstract class SimpleProjection
implements EnhancedProjection {
    private static final int NUM_REUSABLE_ALIASES = 40;
    private static final String[] REUSABLE_ALIASES = SimpleProjection.initializeReusableAliases();

    private static String[] initializeReusableAliases() {
        String[] aliases = new String[40];
        for (int i = 0; i < 40; ++i) {
            aliases[i] = SimpleProjection.aliasForLocation(i);
        }
        return aliases;
    }

    private static String aliasForLocation(int loc) {
        return "y" + loc + "_";
    }

    private static String getAliasForLocation(int loc) {
        if (loc >= 40) {
            return SimpleProjection.aliasForLocation(loc);
        }
        return REUSABLE_ALIASES[loc];
    }

    public Projection as(String alias) {
        return Projections.alias(this, alias);
    }

    @Override
    public String[] getColumnAliases(String alias, int loc) {
        return null;
    }

    @Override
    public String[] getColumnAliases(String alias, int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
        return this.getColumnAliases(alias, loc);
    }

    @Override
    public Type[] getTypes(String alias, Criteria criteria, CriteriaQuery criteriaQuery) {
        return null;
    }

    @Override
    public String[] getColumnAliases(int loc) {
        return new String[]{SimpleProjection.getAliasForLocation(loc)};
    }

    public int getColumnCount(Criteria criteria, CriteriaQuery criteriaQuery) {
        Type[] types = this.getTypes(criteria, criteriaQuery);
        int count = 0;
        for (Type type : types) {
            count += type.getColumnSpan(criteriaQuery.getFactory());
        }
        return count;
    }

    @Override
    public String[] getColumnAliases(int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
        int numColumns = this.getColumnCount(criteria, criteriaQuery);
        String[] aliases = new String[numColumns];
        for (int i = 0; i < numColumns; ++i) {
            aliases[i] = SimpleProjection.getAliasForLocation(loc);
            ++loc;
        }
        return aliases;
    }

    @Override
    public String[] getAliases() {
        return new String[1];
    }

    @Override
    public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        throw new UnsupportedOperationException("not a grouping projection");
    }

    @Override
    public boolean isGrouped() {
        return false;
    }
}

