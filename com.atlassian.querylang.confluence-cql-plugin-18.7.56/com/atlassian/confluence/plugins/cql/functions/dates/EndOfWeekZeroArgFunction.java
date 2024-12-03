/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.DelegatingZeroArgDateFunction;
import com.atlassian.confluence.plugins.cql.functions.dates.EndOfWeekOneArgFunction;

public class EndOfWeekZeroArgFunction
extends DelegatingZeroArgDateFunction {
    public EndOfWeekZeroArgFunction() {
        super(new EndOfWeekOneArgFunction());
    }
}

