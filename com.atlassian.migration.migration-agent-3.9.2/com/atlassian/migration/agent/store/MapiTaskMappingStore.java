/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.MapiTaskMapping;
import java.util.List;
import java.util.Optional;

public interface MapiTaskMappingStore {
    public void createTaskMapping(MapiTaskMapping var1);

    public void update(MapiTaskMapping var1);

    public List<MapiTaskMapping> getPendingTasks();

    public MapiTaskMapping getTaskMapping(String var1, Optional<List<String>> var2, Optional<List<String>> var3);
}

