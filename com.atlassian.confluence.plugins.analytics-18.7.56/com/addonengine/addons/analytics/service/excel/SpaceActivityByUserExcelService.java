/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.excel;

import com.addonengine.addons.analytics.service.excel.model.ExcelReport;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import java.util.Set;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J&\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\nH&\u00a8\u0006\u000b"}, d2={"Lcom/addonengine/addons/analytics/service/excel/SpaceActivityByUserExcelService;", "", "buildExcelReport", "Lcom/addonengine/addons/analytics/service/excel/model/ExcelReport;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "contentTypes", "", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "spaceKey", "", "analytics"})
public interface SpaceActivityByUserExcelService {
    @NotNull
    public ExcelReport buildExcelReport(@NotNull DatePeriodOptions var1, @NotNull Set<? extends ContentType> var2, @NotNull String var3);
}

