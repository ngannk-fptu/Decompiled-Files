/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.DelegatingZeroArgDateFunction;
import com.atlassian.confluence.plugins.cql.functions.dates.StartOfMonthOneArgFunction;

public class StartOfMonthZeroArgFunction
extends DelegatingZeroArgDateFunction {
    public StartOfMonthZeroArgFunction() {
        super(new StartOfMonthOneArgFunction());
    }
}

