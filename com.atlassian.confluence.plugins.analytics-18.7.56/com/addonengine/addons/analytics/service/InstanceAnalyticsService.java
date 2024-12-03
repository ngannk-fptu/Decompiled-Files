/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.model.ActivityByPeriod;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.CountType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.SpaceType;
import java.util.Set;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J4\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00072\u0006\u0010\u000b\u001a\u00020\fH&\u00a8\u0006\r"}, d2={"Lcom/addonengine/addons/analytics/service/InstanceAnalyticsService;", "", "getActivityByPeriod", "Lcom/addonengine/addons/analytics/service/model/ActivityByPeriod;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "spaceTypes", "", "Lcom/addonengine/addons/analytics/service/model/SpaceType;", "contentTypes", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "countType", "Lcom/addonengine/addons/analytics/service/model/CountType;", "analytics"})
public interface InstanceAnalyticsService {
    @NotNull
    public ActivityByPeriod getActivityByPeriod(@NotNull DatePeriodOptions var1, @NotNull Set<? extends SpaceType> var2, @NotNull Set<? extends ContentType> var3, @NotNull CountType var4);
}

