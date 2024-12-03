/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.DelegatingZeroArgDateFunction;
import com.atlassian.confluence.plugins.cql.functions.dates.EndOfYearOneArgFunction;

public class EndOfYearZeroArgFunction
extends DelegatingZeroArgDateFunction {
    public EndOfYearZeroArgFunction() {
        super(new EndOfYearOneArgFunction());
    }
}

