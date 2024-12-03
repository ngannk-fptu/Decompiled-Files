/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.stats;

import org.apache.jackrabbit.api.stats.QueryStatDto;

public interface QueryStat {
    public QueryStatDto[] getSlowQueries();

    public int getSlowQueriesQueueSize();

    public void setSlowQueriesQueueSize(int var1);

    public void clearSlowQueriesQueue();

    public QueryStatDto[] getPopularQueries();

    public int getPopularQueriesQueueSize();

    public void setPopularQueriesQueueSize(int var1);

    public void clearPopularQueriesQueue();

    public boolean isEnabled();

    public void setEnabled(boolean var1);

    public void reset();
}

