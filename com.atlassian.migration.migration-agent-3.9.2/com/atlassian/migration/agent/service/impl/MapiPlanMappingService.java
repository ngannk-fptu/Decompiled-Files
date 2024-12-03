/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.NoResultException
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.entity.MapiPlanMapping;
import com.atlassian.migration.agent.store.MapiPlanMappingStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.util.Optional;
import javax.persistence.NoResultException;

public class MapiPlanMappingService {
    private final PluginTransactionTemplate ptx;
    private final MapiPlanMappingStore mapiPlanMappingStore;

    public MapiPlanMappingService(PluginTransactionTemplate ptx, MapiPlanMappingStore mapiPlanMappingStore) {
        this.ptx = ptx;
        this.mapiPlanMappingStore = mapiPlanMappingStore;
    }

    public void saveMapiPlanMapping(MapiPlanMapping mapiPlanMapping) {
        this.ptx.write(() -> {
            this.mapiPlanMappingStore.setMapiPlanMapping(mapiPlanMapping);
            return null;
        });
    }

    public Optional<MapiPlanMapping> getMapiPlanMapping(String jobId) {
        return this.ptx.read(() -> this.getMapiPlanMappingByJobId(jobId));
    }

    private Optional<MapiPlanMapping> getMapiPlanMappingByJobId(String jobId) {
        try {
            return Optional.of(this.mapiPlanMappingStore.getMapiPlanMappingByJobId(jobId));
        }
        catch (NoResultException e) {
            return Optional.empty();
        }
    }
}

