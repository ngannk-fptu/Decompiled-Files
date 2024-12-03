/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SimpleProjection;
import org.hibernate.type.Type;

public class IdentifierProjection
extends SimpleProjection {
    private boolean grouped;

    protected IdentifierProjection() {
        this(false);
    }

    private IdentifierProjection(boolean grouped) {
        this.grouped = grouped;
    }

    @Override
    public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) {
        return new Type[]{criteriaQuery.getIdentifierType(criteria)};
    }

    @Override
    public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) {
        StringBuilder buf = new StringBuilder();
        String[] cols = criteriaQuery.getIdentifierColumns(criteria);
        for (int i = 0; i < cols.length; ++i) {
            buf.append(cols[i]).append(" as y").append(position + i).append('_');
            if (i >= cols.length - 1) continue;
            buf.append(", ");
        }
        return buf.toString();
    }

    @Override
    public boolean isGrouped() {
        return this.grouped;
    }

    @Override
    public String toGroupSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        if (!this.grouped) {
            return super.toGroupSqlString(criteria, criteriaQuery);
        }
        return String.join((CharSequence)", ", criteriaQuery.getIdentifierColumns(criteria));
    }

    public String toString() {
        return "id";
    }
}

