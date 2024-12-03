/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest.util;

import com.addonengine.addons.analytics.rest.dto.ErrorDto;
import com.addonengine.addons.analytics.rest.dto.ErrorResponseDto;
import com.addonengine.addons.analytics.rest.util.OffsetDateTimeParam;
import com.addonengine.addons.analytics.rest.util.PeriodOptionParam;
import com.addonengine.addons.analytics.rest.util.ZoneIdParam;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000\u001c\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a&\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b\u00a8\u0006\t"}, d2={"buildDatePeriodOptionsFromParams", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "fromDate", "Lcom/addonengine/addons/analytics/rest/util/OffsetDateTimeParam;", "toDate", "period", "Lcom/addonengine/addons/analytics/rest/util/PeriodOptionParam;", "timezone", "Lcom/addonengine/addons/analytics/rest/util/ZoneIdParam;", "analytics"})
public final class DatePeriodOptionsKt {
    @NotNull
    public static final DatePeriodOptions buildDatePeriodOptionsFromParams(@NotNull OffsetDateTimeParam fromDate, @NotNull OffsetDateTimeParam toDate, @NotNull PeriodOptionParam period, @NotNull ZoneIdParam timezone) {
        DatePeriodOptions datePeriodOptions;
        Intrinsics.checkNotNullParameter((Object)fromDate, (String)"fromDate");
        Intrinsics.checkNotNullParameter((Object)toDate, (String)"toDate");
        Intrinsics.checkNotNullParameter((Object)period, (String)"period");
        Intrinsics.checkNotNullParameter((Object)timezone, (String)"timezone");
        try {
            datePeriodOptions = new DatePeriodOptions(fromDate.getValue(), toDate.getValue(), period.getValue(), timezone.getValue(), null, 16, null);
        }
        catch (IllegalArgumentException e) {
            Response.ResponseBuilder response = Response.status((Response.Status)Response.Status.BAD_REQUEST).type("application/json").entity((Object)new ErrorResponseDto(new ErrorDto("ExceededMaxDataPointCount", "The current date range and period exceed the maximum number of data points the server is allowed to create.")));
            throw new WebApplicationException(response.build());
        }
        return datePeriodOptions;
    }
}

