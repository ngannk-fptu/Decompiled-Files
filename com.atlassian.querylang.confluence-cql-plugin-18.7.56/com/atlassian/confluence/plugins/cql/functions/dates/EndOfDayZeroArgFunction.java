/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.DelegatingZeroArgDateFunction;
import com.atlassian.confluence.plugins.cql.functions.dates.EndOfDayOneArgFunction;

public class EndOfDayZeroArgFunction
extends DelegatingZeroArgDateFunction {
    public EndOfDayZeroArgFunction() {
        super(new EndOfDayOneArgFunction());
    }
}

