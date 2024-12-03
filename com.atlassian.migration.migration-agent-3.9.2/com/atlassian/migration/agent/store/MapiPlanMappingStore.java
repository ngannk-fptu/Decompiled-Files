/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.MapiPlanMapping;

public interface MapiPlanMappingStore {
    public void setMapiPlanMapping(MapiPlanMapping var1);

    public MapiPlanMapping getMapiPlanMappingByJobId(String var1);
}

