/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.BaseRelativeDateFunction;
import org.joda.time.DateTime;

public class EndOfWeekOneArgFunction
extends BaseRelativeDateFunction {
    public EndOfWeekOneArgFunction() {
        super("endOfWeek");
    }

    @Override
    protected DateTime getBaseDateTime() {
        return new DateTime().plusWeeks(1).withDayOfWeek(1).withTimeAtStartOfDay();
    }
}

