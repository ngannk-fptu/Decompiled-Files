/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.Progress;
import com.atlassian.migration.agent.store.ProgressStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import java.util.Optional;

public class ProgressStoreImpl
implements ProgressStore {
    private static final String PLANID_QUERY_PARAM = "planId";
    private static final String TYPE_QUERY_PARAM = "type";
    private static final String KEY_QUERY_PARAM = "key";
    private final EntityManagerTemplate tmpl;

    public ProgressStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public List<Progress> getAllByLastScheduledPlan(String planId) {
        return this.tmpl.query(Progress.class, "select p from Progress where p.lastScheduled=:planId").param(PLANID_QUERY_PARAM, (Object)planId).list();
    }

    @Override
    public List<Progress> getAllByLastFailedPlan(String planId) {
        return this.tmpl.query(Progress.class, "select p from Progress where p.lastFailed=:planId").param(PLANID_QUERY_PARAM, (Object)planId).list();
    }

    @Override
    public List<Progress> getAllByLastSuccessPlan(String planId) {
        return this.tmpl.query(Progress.class, "select p from Progress where p.lastSuccess=:planId").param(PLANID_QUERY_PARAM, (Object)planId).list();
    }

    @Override
    public Optional<Progress> getByTypeAndKey(String type, String key) {
        return this.tmpl.query(Progress.class, "select p from Progress where p.type=:type and p.key=:key").param(TYPE_QUERY_PARAM, (Object)type).param(KEY_QUERY_PARAM, (Object)key).first();
    }
}

