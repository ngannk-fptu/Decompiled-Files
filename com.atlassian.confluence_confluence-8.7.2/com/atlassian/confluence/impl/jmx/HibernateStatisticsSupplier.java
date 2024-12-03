/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.SessionFactory
 *  org.hibernate.stat.Statistics
 */
package com.atlassian.confluence.impl.jmx;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import java.util.function.Supplier;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

public class HibernateStatisticsSupplier
implements Supplier<Statistics> {
    private final SessionFactory sessionFactory;

    public HibernateStatisticsSupplier(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Statistics get() {
        if (ConfluenceSystemProperties.isEnableHibernateJMX()) {
            return this.sessionFactory.getStatistics();
        }
        return null;
    }
}

