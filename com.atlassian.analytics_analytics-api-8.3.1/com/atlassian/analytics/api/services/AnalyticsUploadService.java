/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.api.services;

import java.util.Date;

public interface AnalyticsUploadService {
    public Date getLastUploadDate();

    public boolean hasUploadedAnalyticsSince(int var1);
}

