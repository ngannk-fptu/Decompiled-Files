/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.analytics.MultPartUploadAnalyticEvent
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.analytics.MultPartUploadAnalyticEvent;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0006"}, d2={"Lcom/atlassian/migration/app/AppAnalyticsEventService;", "", "sendEvent", "", "multPartUploadAnalyticEvent", "Lcom/atlassian/migration/app/analytics/MultPartUploadAnalyticEvent;", "app-migration-assistant"})
public interface AppAnalyticsEventService {
    public void sendEvent(@NotNull MultPartUploadAnalyticEvent var1);
}

