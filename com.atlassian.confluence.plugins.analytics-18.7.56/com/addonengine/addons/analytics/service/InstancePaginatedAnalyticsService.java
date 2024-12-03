/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.CountType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.GlobalUserActivity;
import com.addonengine.addons.analytics.service.model.GlobalUserSortField;
import com.addonengine.addons.analytics.service.model.SortOrder;
import com.addonengine.addons.analytics.service.model.SpaceActivity;
import com.addonengine.addons.analytics.service.model.SpaceSortField;
import com.addonengine.addons.analytics.service.model.SpaceType;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001Jl\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b2\b\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\b2\b\b\u0002\u0010\u0016\u001a\u00020\u0017H&JT\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u00032\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b2\b\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u001a2\u0006\u0010\u0012\u001a\u00020\u0013H&\u00a8\u0006\u001b"}, d2={"Lcom/addonengine/addons/analytics/service/InstancePaginatedAnalyticsService;", "", "getActivityBySpace", "", "Lcom/addonengine/addons/analytics/service/model/SpaceActivity;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "spaceTypes", "", "Lcom/addonengine/addons/analytics/service/model/SpaceType;", "contentTypes", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "pageToken", "", "limit", "", "sortField", "Lcom/addonengine/addons/analytics/service/model/SpaceSortField;", "sortOrder", "Lcom/addonengine/addons/analytics/service/model/SortOrder;", "countType", "Lcom/addonengine/addons/analytics/service/model/CountType;", "includeSpaceCategories", "", "getActivityByUser", "Lcom/addonengine/addons/analytics/service/model/GlobalUserActivity;", "Lcom/addonengine/addons/analytics/service/model/GlobalUserSortField;", "analytics"})
public interface InstancePaginatedAnalyticsService {
    @NotNull
    public List<SpaceActivity> getActivityBySpace(@NotNull DatePeriodOptions var1, @NotNull Set<? extends SpaceType> var2, @NotNull Set<? extends ContentType> var3, @Nullable String var4, int var5, @NotNull SpaceSortField var6, @NotNull SortOrder var7, @NotNull Set<? extends CountType> var8, boolean var9);

    @NotNull
    public List<GlobalUserActivity> getActivityByUser(@NotNull DatePeriodOptions var1, @NotNull Set<? extends SpaceType> var2, @NotNull Set<? extends ContentType> var3, @Nullable String var4, int var5, @NotNull GlobalUserSortField var6, @NotNull SortOrder var7);

    @Metadata(mv={1, 9, 0}, k=3, xi=48)
    public static final class DefaultImpls {
        public static /* synthetic */ List getActivityBySpace$default(InstancePaginatedAnalyticsService instancePaginatedAnalyticsService, DatePeriodOptions datePeriodOptions, Set set2, Set set3, String string, int n, SpaceSortField spaceSortField, SortOrder sortOrder, Set set4, boolean bl, int n2, Object object) {
            if (object != null) {
                throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getActivityBySpace");
            }
            if ((n2 & 0x100) != 0) {
                bl = false;
            }
            return instancePaginatedAnalyticsService.getActivityBySpace(datePeriodOptions, set2, set3, string, n, spaceSortField, sortOrder, set4, bl);
        }
    }
}

