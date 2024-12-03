/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.cql.functions.dates;

import com.atlassian.confluence.plugins.cql.functions.dates.DelegatingZeroArgDateFunction;
import com.atlassian.confluence.plugins.cql.functions.dates.NowOneArgFunction;

public class NowZeroArgFunction
extends DelegatingZeroArgDateFunction {
    public NowZeroArgFunction() {
        super(new NowOneArgFunction());
    }
}

