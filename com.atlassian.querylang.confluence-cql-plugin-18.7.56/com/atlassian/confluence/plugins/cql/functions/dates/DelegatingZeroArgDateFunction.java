/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.functions.EvaluationContext
 *  com.atlassian.querylang.functions.SingleValueQueryFunction
 *  com.atlassian.querylang.literals.DateLiteralHelper
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.BaseRelativeDateFunction;
import com.atlassian.querylang.functions.EvaluationContext;
import com.atlassian.querylang.functions.SingleValueQueryFunction;
import com.atlassian.querylang.literals.DateLiteralHelper;
import java.util.List;
import org.joda.time.DateTime;

public abstract class DelegatingZeroArgDateFunction
extends SingleValueQueryFunction<EvaluationContext> {
    private final BaseRelativeDateFunction delegate;

    public DelegatingZeroArgDateFunction(BaseRelativeDateFunction delegate) {
        super(delegate.name());
        this.delegate = delegate;
    }

    public final int paramCount() {
        return 0;
    }

    public final String invoke(List<String> params, EvaluationContext cxt) {
        return DateLiteralHelper.toDateTimeString((DateTime)this.delegate.getBaseDateTime());
    }
}

