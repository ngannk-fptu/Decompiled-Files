/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SimpleProjection;
import org.hibernate.type.Type;

public class ForeingKeyProjection
extends SimpleProjection {
    private String associationPropertyName;

    protected ForeingKeyProjection(String associationPropertyName) {
        this.associationPropertyName = associationPropertyName;
    }

    @Override
    public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) {
        return new Type[]{criteriaQuery.getForeignKeyType(criteria, this.associationPropertyName)};
    }

    @Override
    public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) {
        StringBuilder buf = new StringBuilder();
        String[] cols = criteriaQuery.getForeignKeyColumns(criteria, this.associationPropertyName);
        for (int i = 0; i < cols.length; ++i) {
            buf.append(cols[i]).append(" as y").append(position + i).append('_');
            if (i >= cols.length - 1) continue;
            buf.append(", ");
        }
        return buf.toString();
    }

    @Override
    public boolean isGrouped() {
        return false;
    }

    @Override
    public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        return super.toGroupSqlString(criteria, criteriaQuery);
    }

    public String toString() {
        return "fk";
    }
}

