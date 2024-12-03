/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.BaseRelativeDateFunction;
import org.joda.time.DateTime;

public class StartOfDayOneArgFunction
extends BaseRelativeDateFunction {
    public StartOfDayOneArgFunction() {
        super("startOfDay");
    }

    @Override
    protected DateTime getBaseDateTime() {
        return new DateTime().withTimeAtStartOfDay();
    }
}

