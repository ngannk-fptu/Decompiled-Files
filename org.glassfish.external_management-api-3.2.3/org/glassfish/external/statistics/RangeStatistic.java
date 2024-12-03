/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.statistics;

import org.glassfish.external.statistics.Statistic;

public interface RangeStatistic
extends Statistic {
    public long getHighWaterMark();

    public long getLowWaterMark();

    public long getCurrent();
}

