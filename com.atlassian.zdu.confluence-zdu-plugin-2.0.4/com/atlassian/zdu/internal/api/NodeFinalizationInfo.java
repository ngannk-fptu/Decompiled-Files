/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.internal.api;

import com.atlassian.zdu.internal.api.UpgradeTaskError;
import java.util.Date;
import java.util.List;

public interface NodeFinalizationInfo {
    public Date getLastRequested();

    public boolean runsClusterWideTasks();

    public List<UpgradeTaskError> getErrors();
}

