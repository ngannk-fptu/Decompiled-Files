/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.io.Serializable;
import java.util.Locale;
import org.hibernate.Criteria;
import org.hibernate.NullPrecedence;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

public class Order
implements Serializable {
    private boolean ascending;
    private boolean ignoreCase;
    private String propertyName;
    private NullPrecedence nullPrecedence;

    public static Order asc(String propertyName) {
        return new Order(propertyName, true);
    }

    public static Order desc(String propertyName) {
        return new Order(propertyName, false);
    }

    protected Order(String propertyName, boolean ascending) {
        this.propertyName = propertyName;
        this.ascending = ascending;
    }

    public Order ignoreCase() {
        this.ignoreCase = true;
        return this;
    }

    public Order nulls(NullPrecedence nullPrecedence) {
        this.nullPrecedence = nullPrecedence;
        return this;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public boolean isAscending() {
        return this.ascending;
    }

    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }

    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        String[] columns = criteriaQuery.getColumnsUsingProjection(criteria, this.propertyName);
        Type type = criteriaQuery.getTypeUsingProjection(criteria, this.propertyName);
        SessionFactoryImplementor factory = criteriaQuery.getFactory();
        int[] sqlTypes = type.sqlTypes(factory);
        StringBuilder fragment = new StringBuilder();
        for (int i = 0; i < columns.length; ++i) {
            StringBuilder expression = new StringBuilder();
            boolean lower = false;
            if (this.ignoreCase) {
                int sqlType = sqlTypes[i];
                boolean bl = lower = sqlType == 12 || sqlType == 1 || sqlType == -1;
            }
            if (lower) {
                expression.append(factory.getDialect().getLowercaseFunction()).append('(');
            }
            expression.append(columns[i]);
            if (lower) {
                expression.append(')');
            }
            fragment.append(factory.getDialect().renderOrderByElement(expression.toString(), null, this.ascending ? "asc" : "desc", this.nullPrecedence != null ? this.nullPrecedence : factory.getSettings().getDefaultNullPrecedence()));
            if (i >= columns.length - 1) continue;
            fragment.append(", ");
        }
        return fragment.toString();
    }

    public String toString() {
        return this.propertyName + ' ' + (this.ascending ? "asc" : "desc") + (this.nullPrecedence != null ? ' ' + this.nullPrecedence.name().toLowerCase(Locale.ROOT) : "");
    }
}

