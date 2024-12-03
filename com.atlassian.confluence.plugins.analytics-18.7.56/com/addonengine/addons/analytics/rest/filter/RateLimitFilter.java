/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.container.ContainerRequest
 *  com.sun.jersey.spi.container.ContainerResponse
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Provider
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.addonengine.addons.analytics.rest.filter;

import com.addonengine.addons.analytics.rest.dto.ErrorDto;
import com.addonengine.addons.analytics.rest.dto.ErrorResponseDto;
import com.addonengine.addons.analytics.rest.filter.PrerequestFilter;
import com.addonengine.addons.analytics.service.SettingsService;
import com.addonengine.addons.analytics.service.confluence.RateLimitService;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0016J\u0018\u0010\n\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\rH\u0016R\u0016\u0010\u0007\u001a\n \t*\u0004\u0018\u00010\b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2={"Lcom/addonengine/addons/analytics/rest/filter/RateLimitFilter;", "Lcom/addonengine/addons/analytics/rest/filter/PrerequestFilter;", "rateLimitService", "Lcom/addonengine/addons/analytics/service/confluence/RateLimitService;", "settingsService", "Lcom/addonengine/addons/analytics/service/SettingsService;", "(Lcom/addonengine/addons/analytics/service/confluence/RateLimitService;Lcom/addonengine/addons/analytics/service/SettingsService;)V", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "filter", "Lcom/sun/jersey/spi/container/ContainerRequest;", "containerRequest", "Lcom/sun/jersey/spi/container/ContainerResponse;", "containerResponse", "analytics"})
public final class RateLimitFilter
extends PrerequestFilter {
    @NotNull
    private final RateLimitService rateLimitService;
    @NotNull
    private final SettingsService settingsService;
    private final Logger log;

    public RateLimitFilter(@NotNull RateLimitService rateLimitService, @NotNull SettingsService settingsService) {
        Intrinsics.checkNotNullParameter((Object)rateLimitService, (String)"rateLimitService");
        Intrinsics.checkNotNullParameter((Object)settingsService, (String)"settingsService");
        this.rateLimitService = rateLimitService;
        this.settingsService = settingsService;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    @NotNull
    public ContainerRequest filter(@NotNull ContainerRequest containerRequest) {
        Intrinsics.checkNotNullParameter((Object)containerRequest, (String)"containerRequest");
        if (this.settingsService.getRateLimitSettings().getEnabled()) {
            String string = containerRequest.getHeaderValue("cookie");
            if (string == null) {
                string = "";
            }
            String sessionId = string;
            this.log.debug("Checking if rate limiting logic needs to be applied to session: [{}]", (Object)sessionId.hashCode());
            if (!StringsKt.isBlank((CharSequence)sessionId) && this.rateLimitService.rateLimit(sessionId)) {
                this.log.debug("Response will have been modified in RL handler. Stop here and return.");
                Response.ResponseBuilder response = Response.status((Response.Status)Response.Status.FORBIDDEN).type("application/json").entity((Object)new ErrorResponseDto(new ErrorDto("RequestRateLimited", "There are too many users trying to open analytics at the same time. Try again in 1 minute from now.")));
                throw new WebApplicationException(response.build());
            }
        }
        this.log.debug("Request not blocked due to RL so continue on.");
        return containerRequest;
    }

    @Override
    @NotNull
    public ContainerResponse filter(@NotNull ContainerRequest containerRequest, @NotNull ContainerResponse containerResponse) {
        Intrinsics.checkNotNullParameter((Object)containerRequest, (String)"containerRequest");
        Intrinsics.checkNotNullParameter((Object)containerResponse, (String)"containerResponse");
        if (this.settingsService.getRateLimitSettings().getEnabled()) {
            String string = containerRequest.getHeaderValue("cookie");
            if (string == null) {
                string = "";
            }
            String sessionId = string;
            this.log.debug("Decrementing the active operation count for session: [{}]", (Object)sessionId.hashCode());
            if (!StringsKt.isBlank((CharSequence)sessionId)) {
                this.rateLimitService.decrementOperationCount(sessionId);
            }
        }
        return containerResponse;
    }
}

