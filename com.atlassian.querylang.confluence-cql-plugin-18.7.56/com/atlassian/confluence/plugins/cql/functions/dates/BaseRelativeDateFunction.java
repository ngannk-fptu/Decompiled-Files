/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.functions.EvaluationContext
 *  com.atlassian.querylang.functions.SingleValueQueryFunction
 *  com.atlassian.querylang.literals.DateLiteralHelper
 *  org.joda.time.DateTime
 *  org.joda.time.ReadablePeriod
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.querylang.functions.EvaluationContext;
import com.atlassian.querylang.functions.SingleValueQueryFunction;
import com.atlassian.querylang.literals.DateLiteralHelper;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;

public abstract class BaseRelativeDateFunction
extends SingleValueQueryFunction<EvaluationContext> {
    public BaseRelativeDateFunction(String name) {
        super(name);
    }

    public final int paramCount() {
        return 1;
    }

    public final String invoke(List<String> params, EvaluationContext e) {
        String relativeDate = params.get(0);
        ReadablePeriod period = DateLiteralHelper.toDateTime((String)relativeDate);
        return DateLiteralHelper.toDateTimeString((DateTime)this.getBaseDateTime().plus(period));
    }

    protected abstract DateTime getBaseDateTime();
}

