/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.Stats;
import com.atlassian.migration.agent.entity.StatsKey;
import com.atlassian.migration.agent.entity.StatsType;
import java.util.List;

public interface StatsStore {
    public List<Stats> getByType(StatsType var1);

    public List<Stats> getByTypeAndPlan(StatsType var1, String var2);

    public void clearByType(StatsType var1);

    public void clearByKey(StatsKey var1);

    public void persist(Stats var1);

    public void persist(List<Stats> var1);
}

