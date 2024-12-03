/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.EnhancedProjection;
import org.hibernate.criterion.Projection;
import org.hibernate.type.Type;

public class Distinct
implements EnhancedProjection {
    private final Projection wrappedProjection;

    public Distinct(Projection wrappedProjection) {
        this.wrappedProjection = wrappedProjection;
    }

    @Override
    public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) {
        return "distinct " + this.wrappedProjection.toSqlString(criteria, position, criteriaQuery);
    }

    @Override
    public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        return this.wrappedProjection.toGroupSqlString(criteria, criteriaQuery);
    }

    @Override
    public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) {
        return this.wrappedProjection.getTypes(criteria, criteriaQuery);
    }

    @Override
    public Type[] getTypes(String alias, Criteria criteria, CriteriaQuery criteriaQuery) {
        return this.wrappedProjection.getTypes(alias, criteria, criteriaQuery);
    }

    @Override
    public String[] getColumnAliases(int loc) {
        return this.wrappedProjection.getColumnAliases(loc);
    }

    @Override
    public String[] getColumnAliases(int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
        return this.wrappedProjection instanceof EnhancedProjection ? ((EnhancedProjection)this.wrappedProjection).getColumnAliases(loc, criteria, criteriaQuery) : this.getColumnAliases(loc);
    }

    @Override
    public String[] getColumnAliases(String alias, int loc) {
        return this.wrappedProjection.getColumnAliases(alias, loc);
    }

    @Override
    public String[] getColumnAliases(String alias, int loc, Criteria criteria, CriteriaQuery criteriaQuery) {
        return this.wrappedProjection instanceof EnhancedProjection ? ((EnhancedProjection)this.wrappedProjection).getColumnAliases(alias, loc, criteria, criteriaQuery) : this.getColumnAliases(alias, loc);
    }

    @Override
    public String[] getAliases() {
        return this.wrappedProjection.getAliases();
    }

    @Override
    public boolean isGrouped() {
        return this.wrappedProjection.isGrouped();
    }

    public String toString() {
        return "distinct " + this.wrappedProjection.toString();
    }
}

