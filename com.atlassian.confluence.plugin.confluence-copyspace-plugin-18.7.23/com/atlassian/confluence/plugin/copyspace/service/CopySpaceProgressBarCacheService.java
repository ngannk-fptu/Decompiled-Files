/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.plugin.copyspace.entity.CopySpaceProgressBarData;

public interface CopySpaceProgressBarCacheService {
    public CopySpaceProgressBarData getProgressBarData(String var1);

    public void putProgressBarData(CopySpaceProgressBarData var1);

    public void removeProgressBarData(String var1);

    public boolean isCopySpaceInProgress(String var1);
}

