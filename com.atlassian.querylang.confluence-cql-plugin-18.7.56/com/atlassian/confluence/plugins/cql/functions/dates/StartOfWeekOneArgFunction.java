/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.BaseRelativeDateFunction;
import org.joda.time.DateTime;

public class StartOfWeekOneArgFunction
extends BaseRelativeDateFunction {
    public StartOfWeekOneArgFunction() {
        super("startOfWeek");
    }

    @Override
    protected DateTime getBaseDateTime() {
        return new DateTime().withDayOfWeek(1).withTimeAtStartOfDay();
    }
}

