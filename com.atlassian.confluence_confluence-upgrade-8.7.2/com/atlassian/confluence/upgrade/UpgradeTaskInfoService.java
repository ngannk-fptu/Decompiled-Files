/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.confluence.upgrade.UpgradeTaskInfo;
import java.util.Collection;

public interface UpgradeTaskInfoService {
    public Collection<UpgradeTaskInfo> getAllUpgradeTasksInfo();
}

