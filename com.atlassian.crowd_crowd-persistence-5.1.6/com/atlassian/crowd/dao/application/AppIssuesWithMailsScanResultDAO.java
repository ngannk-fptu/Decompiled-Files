/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.dao.application;

import com.atlassian.crowd.model.application.AppIssuesWithMailScanResultEntity;
import java.util.Optional;

public interface AppIssuesWithMailsScanResultDAO {
    public void persistLatestResult(AppIssuesWithMailScanResultEntity var1);

    public Optional<AppIssuesWithMailScanResultEntity> getLatestResult(long var1);

    public void removeLatestResultIfPresent(long var1);
}

