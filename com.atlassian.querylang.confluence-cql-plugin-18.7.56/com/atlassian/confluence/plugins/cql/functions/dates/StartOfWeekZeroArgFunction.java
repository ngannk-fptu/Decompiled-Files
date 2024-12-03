/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.DelegatingZeroArgDateFunction;
import com.atlassian.confluence.plugins.cql.functions.dates.StartOfWeekOneArgFunction;

public class StartOfWeekZeroArgFunction
extends DelegatingZeroArgDateFunction {
    public StartOfWeekZeroArgFunction() {
        super(new StartOfWeekOneArgFunction());
    }
}

