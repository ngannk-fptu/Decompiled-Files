/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.stats;

import org.apache.jackrabbit.api.stats.QueryStat;

public interface QueryStatCore
extends QueryStat {
    public void logQuery(String var1, String var2, long var3);
}

