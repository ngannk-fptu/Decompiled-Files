/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.statistics;

import org.glassfish.external.statistics.Statistic;

public interface TimeStatistic
extends Statistic {
    public long getCount();

    public long getMaxTime();

    public long getMinTime();

    public long getTotalTime();
}

