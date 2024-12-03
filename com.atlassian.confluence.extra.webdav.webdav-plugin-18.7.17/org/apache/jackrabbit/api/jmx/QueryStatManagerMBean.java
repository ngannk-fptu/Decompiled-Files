/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.jmx;

import javax.management.openmbean.TabularData;

public interface QueryStatManagerMBean {
    public static final String NAME = "org.apache.jackrabbit:type=QueryStats";

    public TabularData getSlowQueries();

    public TabularData getPopularQueries();

    public int getSlowQueriesQueueSize();

    public void setSlowQueriesQueueSize(int var1);

    public void clearSlowQueriesQueue();

    public int getPopularQueriesQueueSize();

    public void setPopularQueriesQueueSize(int var1);

    public void clearPopularQueriesQueue();
}

