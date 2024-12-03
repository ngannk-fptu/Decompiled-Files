/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.spi;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.spi.StatisticsImplementor;

public interface StatisticsFactory {
    public StatisticsImplementor buildStatistics(SessionFactoryImplementor var1);
}

