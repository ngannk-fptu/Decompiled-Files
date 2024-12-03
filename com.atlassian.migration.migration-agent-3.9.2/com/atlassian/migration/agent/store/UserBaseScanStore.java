/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.ScanStatus;
import com.atlassian.migration.agent.entity.UserBaseScan;
import java.util.Optional;

public interface UserBaseScanStore {
    public void save(UserBaseScan var1);

    public void updateStatus(String var1, ScanStatus var2);

    public void updateStatusAndCounts(String var1, ScanStatus var2, long var3, long var5);

    public Optional<UserBaseScan> get(String var1);

    public Optional<UserBaseScan> getLatestStarted();
}

