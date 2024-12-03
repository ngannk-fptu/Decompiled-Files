/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.expression.function;

import java.io.Serializable;
import java.sql.Time;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.expression.function.BasicFunctionExpression;

public class CurrentTimeFunction
extends BasicFunctionExpression<Time>
implements Serializable {
    public static final String NAME = "current_time";

    public CurrentTimeFunction(CriteriaBuilderImpl criteriaBuilder) {
        super(criteriaBuilder, Time.class, NAME);
    }
}

