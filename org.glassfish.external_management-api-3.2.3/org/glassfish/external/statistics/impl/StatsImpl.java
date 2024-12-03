/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.statistics.impl;

import java.util.ArrayList;
import org.glassfish.external.statistics.Statistic;
import org.glassfish.external.statistics.Stats;
import org.glassfish.external.statistics.impl.StatisticImpl;

public final class StatsImpl
implements Stats {
    private final StatisticImpl[] statArray;

    protected StatsImpl(StatisticImpl[] statisticArray) {
        this.statArray = statisticArray;
    }

    @Override
    public synchronized Statistic getStatistic(String statisticName) {
        StatisticImpl stat = null;
        for (StatisticImpl s : this.statArray) {
            if (!s.getName().equals(statisticName)) continue;
            stat = s;
            break;
        }
        return stat;
    }

    @Override
    public synchronized String[] getStatisticNames() {
        ArrayList<String> list = new ArrayList<String>();
        for (StatisticImpl s : this.statArray) {
            list.add(s.getName());
        }
        String[] strArray = new String[list.size()];
        return list.toArray(strArray);
    }

    @Override
    public synchronized Statistic[] getStatistics() {
        return this.statArray;
    }

    public synchronized void reset() {
        for (StatisticImpl s : this.statArray) {
            s.reset();
        }
    }
}

