/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import java.util.Map;

public interface StepProgressPropertiesStore {
    public Map<String, Object> getStepProgressProperties(String var1);

    public void storeStepProgressProperties(String var1, Map<String, Object> var2);
}

