/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SQLCriterion;
import org.hibernate.type.Type;

@Deprecated
public final class Expression
extends Restrictions {
    @Deprecated
    public static Criterion sql(String sql, Object[] values, Type[] types) {
        return new SQLCriterion(sql, values, types);
    }

    @Deprecated
    public static Criterion sql(String sql, Object value, Type type) {
        return new SQLCriterion(sql, value, type);
    }

    @Deprecated
    public static Criterion sql(String sql) {
        return new SQLCriterion(sql);
    }

    private Expression() {
    }
}

