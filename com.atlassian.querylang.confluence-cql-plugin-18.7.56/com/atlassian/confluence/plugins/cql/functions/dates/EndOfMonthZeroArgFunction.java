/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.DelegatingZeroArgDateFunction;
import com.atlassian.confluence.plugins.cql.functions.dates.EndOfMonthOneArgFunction;

public class EndOfMonthZeroArgFunction
extends DelegatingZeroArgDateFunction {
    public EndOfMonthZeroArgFunction() {
        super(new EndOfMonthOneArgFunction());
    }
}

