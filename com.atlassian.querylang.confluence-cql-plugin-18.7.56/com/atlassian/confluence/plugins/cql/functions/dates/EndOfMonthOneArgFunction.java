/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.BaseRelativeDateFunction;
import org.joda.time.DateTime;

public class EndOfMonthOneArgFunction
extends BaseRelativeDateFunction {
    public EndOfMonthOneArgFunction() {
        super("endOfMonth");
    }

    @Override
    protected DateTime getBaseDateTime() {
        return new DateTime().plusMonths(1).withDayOfMonth(1).withTimeAtStartOfDay();
    }
}

