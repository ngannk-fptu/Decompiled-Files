/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.Progress;
import java.util.List;
import java.util.Optional;

public interface ProgressStore {
    public List<Progress> getAllByLastScheduledPlan(String var1);

    public List<Progress> getAllByLastFailedPlan(String var1);

    public List<Progress> getAllByLastSuccessPlan(String var1);

    public Optional<Progress> getByTypeAndKey(String var1, String var2);
}

