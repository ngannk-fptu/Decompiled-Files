/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.statistics;

import org.glassfish.external.statistics.Statistic;

public interface Stats {
    public Statistic getStatistic(String var1);

    public String[] getStatisticNames();

    public Statistic[] getStatistics();
}

