/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.model.ContentActivity;
import com.addonengine.addons.analytics.service.model.ContentSortField;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.SortOrder;
import com.addonengine.addons.analytics.service.model.SpaceLevelUserActivity;
import com.addonengine.addons.analytics.service.model.SpaceLevelUserSortField;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001JN\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\b\u0010\f\u001a\u0004\u0018\u00010\b2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H&JN\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\b\u0010\f\u001a\u0004\u0018\u00010\b2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00152\u0006\u0010\u0011\u001a\u00020\u0012H&\u00a8\u0006\u0016"}, d2={"Lcom/addonengine/addons/analytics/service/SpacePaginatedAnalyticsService;", "", "getActivityByContent", "", "Lcom/addonengine/addons/analytics/service/model/ContentActivity;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "spaceKey", "", "contentTypes", "", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "pageToken", "limit", "", "sortField", "Lcom/addonengine/addons/analytics/service/model/ContentSortField;", "sortOrder", "Lcom/addonengine/addons/analytics/service/model/SortOrder;", "getActivityByUser", "Lcom/addonengine/addons/analytics/service/model/SpaceLevelUserActivity;", "Lcom/addonengine/addons/analytics/service/model/SpaceLevelUserSortField;", "analytics"})
public interface SpacePaginatedAnalyticsService {
    @NotNull
    public List<ContentActivity> getActivityByContent(@NotNull DatePeriodOptions var1, @NotNull String var2, @NotNull Set<? extends ContentType> var3, @Nullable String var4, int var5, @NotNull ContentSortField var6, @NotNull SortOrder var7);

    @NotNull
    public List<SpaceLevelUserActivity> getActivityByUser(@NotNull DatePeriodOptions var1, @NotNull String var2, @NotNull Set<? extends ContentType> var3, @Nullable String var4, int var5, @NotNull SpaceLevelUserSortField var6, @NotNull SortOrder var7);
}

