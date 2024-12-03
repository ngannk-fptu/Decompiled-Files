/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.BaseRelativeDateFunction;
import org.joda.time.DateTime;

public class EndOfYearOneArgFunction
extends BaseRelativeDateFunction {
    public EndOfYearOneArgFunction() {
        super("endOfYear");
    }

    @Override
    protected DateTime getBaseDateTime() {
        return new DateTime().withDayOfYear(1).plusYears(1).withTimeAtStartOfDay();
    }
}

