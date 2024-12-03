/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.statistics;

import org.glassfish.external.statistics.Statistic;

public interface BoundaryStatistic
extends Statistic {
    public long getUpperBound();

    public long getLowerBound();
}

