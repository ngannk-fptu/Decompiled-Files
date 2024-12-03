/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.Progress;
import java.util.Collection;
import java.util.Map;

public interface ConfluenceSpaceTaskStore {
    public Map<String, Progress> getLatestSpaceProgress(String var1, Collection<String> var2);
}

