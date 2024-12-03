/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.MapiPlanMapping;
import com.atlassian.migration.agent.store.MapiPlanMappingStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;

public class MapiPlanMappingStoreImpl
implements MapiPlanMappingStore {
    private final EntityManagerTemplate tmpl;

    public MapiPlanMappingStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public void setMapiPlanMapping(MapiPlanMapping mapiPlanMapping) {
        this.tmpl.persist(mapiPlanMapping);
    }

    @Override
    public MapiPlanMapping getMapiPlanMappingByJobId(String jobId) {
        return this.tmpl.query(MapiPlanMapping.class, "select mapiPlanMapping from MapiPlanMapping mapiPlanMapping where mapiPlanMapping.jobId=:jobId").param("jobId", (Object)jobId).single();
    }
}

