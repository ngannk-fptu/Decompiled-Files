/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.model.AttachmentViews;
import com.addonengine.addons.analytics.service.model.ContentRef;
import com.addonengine.addons.analytics.service.model.CountType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.PeriodActivity;
import com.addonengine.addons.analytics.service.model.UserViews;
import java.util.List;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&J&\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00032\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH&J\u0016\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u00032\u0006\u0010\u000b\u001a\u00020\fH&\u00a8\u0006\u0011"}, d2={"Lcom/addonengine/addons/analytics/service/ContentAnalyticsService;", "", "getViewsByAttachment", "", "Lcom/addonengine/addons/analytics/service/model/AttachmentViews;", "containerId", "", "getViewsByPeriod", "Lcom/addonengine/addons/analytics/service/model/PeriodActivity;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "contentRef", "Lcom/addonengine/addons/analytics/service/model/ContentRef;", "countType", "Lcom/addonengine/addons/analytics/service/model/CountType;", "getViewsByUser", "Lcom/addonengine/addons/analytics/service/model/UserViews;", "analytics"})
public interface ContentAnalyticsService {
    @NotNull
    public List<UserViews> getViewsByUser(@NotNull ContentRef var1);

    @NotNull
    public List<PeriodActivity> getViewsByPeriod(@NotNull DatePeriodOptions var1, @NotNull ContentRef var2, @NotNull CountType var3);

    @NotNull
    public List<AttachmentViews> getViewsByAttachment(long var1);
}

