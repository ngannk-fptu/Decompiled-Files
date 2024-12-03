/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.DelegatingZeroArgDateFunction;
import com.atlassian.confluence.plugins.cql.functions.dates.StartOfYearOneArgFunction;

public class StartOfYearZeroArgFunction
extends DelegatingZeroArgDateFunction {
    public StartOfYearZeroArgFunction() {
        super(new StartOfYearOneArgFunction());
    }
}

