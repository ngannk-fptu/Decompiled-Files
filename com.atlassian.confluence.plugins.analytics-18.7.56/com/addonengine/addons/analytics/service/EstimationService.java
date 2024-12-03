/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.ReportTimingEstimation;
import java.time.OffsetDateTime;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H&\u00a8\u0006\u0007"}, d2={"Lcom/addonengine/addons/analytics/service/EstimationService;", "", "estimateReportTiming", "Lcom/addonengine/addons/analytics/service/ReportTimingEstimation;", "from", "Ljava/time/OffsetDateTime;", "to", "analytics"})
public interface EstimationService {
    @NotNull
    public ReportTimingEstimation estimateReportTiming(@NotNull OffsetDateTime var1, @NotNull OffsetDateTime var2);
}

