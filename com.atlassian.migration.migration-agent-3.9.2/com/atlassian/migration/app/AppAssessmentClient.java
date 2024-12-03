/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.AppsCloudSiteResponse
 *  com.atlassian.migration.app.dto.AppsLicenseResponseDto
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.dto.AppsCloudSiteResponse;
import com.atlassian.migration.app.dto.AppsLicenseResponseDto;
import java.util.List;

public interface AppAssessmentClient {
    public AppsCloudSiteResponse getAppInfoForSite(String var1, List<String> var2);

    public AppsLicenseResponseDto getAppsLicense(String var1, List<String> var2);
}

