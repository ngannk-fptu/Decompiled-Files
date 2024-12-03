/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.expression.function;

import java.io.Serializable;
import java.sql.Timestamp;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.expression.function.BasicFunctionExpression;

public class CurrentTimestampFunction
extends BasicFunctionExpression<Timestamp>
implements Serializable {
    public static final String NAME = "current_timestamp";

    public CurrentTimestampFunction(CriteriaBuilderImpl criteriaBuilder) {
        super(criteriaBuilder, Timestamp.class, NAME);
    }
}

