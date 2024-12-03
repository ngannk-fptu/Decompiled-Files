/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.expression.function;

import java.io.Serializable;
import java.sql.Date;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.expression.function.BasicFunctionExpression;

public class CurrentDateFunction
extends BasicFunctionExpression<Date>
implements Serializable {
    public static final String NAME = "current_date";

    public CurrentDateFunction(CriteriaBuilderImpl criteriaBuilder) {
        super(criteriaBuilder, Date.class, NAME);
    }
}

