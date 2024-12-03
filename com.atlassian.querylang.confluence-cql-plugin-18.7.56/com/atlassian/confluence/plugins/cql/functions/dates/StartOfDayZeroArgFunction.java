/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.DelegatingZeroArgDateFunction;
import com.atlassian.confluence.plugins.cql.functions.dates.StartOfDayOneArgFunction;

public class StartOfDayZeroArgFunction
extends DelegatingZeroArgDateFunction {
    public StartOfDayZeroArgFunction() {
        super(new StartOfDayOneArgFunction());
    }
}

