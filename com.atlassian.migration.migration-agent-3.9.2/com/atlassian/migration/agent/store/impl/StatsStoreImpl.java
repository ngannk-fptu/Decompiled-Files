/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.Stats;
import com.atlassian.migration.agent.entity.StatsKey;
import com.atlassian.migration.agent.entity.StatsType;
import com.atlassian.migration.agent.store.StatsStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;

public class StatsStoreImpl
implements StatsStore {
    private final EntityManagerTemplate tmpl;

    public StatsStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public List<Stats> getByType(StatsType type) {
        return this.tmpl.query(Stats.class, "select s from Stats s where s.type=:type").param("type", (Object)type).list();
    }

    @Override
    public List<Stats> getByTypeAndPlan(StatsType type, String planId) {
        return this.tmpl.query(Stats.class, "select s from Stats s where s.type=:type and s.planId=:planId").param("type", (Object)type).param("planId", (Object)planId).list();
    }

    @Override
    public void clearByType(StatsType type) {
        this.tmpl.query("delete from Stats s where s.type=:type").param("type", (Object)type).flush(true).update();
    }

    @Override
    public void clearByKey(StatsKey key) {
        this.tmpl.query("delete from Stats s where s.type=:type and s.name=:name").param("type", (Object)key.getType()).param("name", (Object)key.getName()).flush(true).update();
    }

    @Override
    public void persist(List<Stats> stats) {
        stats.forEach(this.tmpl::persist);
    }

    @Override
    public void persist(Stats stat) {
        this.tmpl.persist(stat);
    }
}

