/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.container.ContainerRequest
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Provider
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest.filter;

import com.addonengine.addons.analytics.rest.dto.ErrorDto;
import com.addonengine.addons.analytics.rest.dto.ErrorResponseDto;
import com.addonengine.addons.analytics.rest.filter.PrerequestFilter;
import com.addonengine.addons.analytics.service.confluence.LicenseService;
import com.addonengine.addons.analytics.service.confluence.model.LicenseStatus;
import com.sun.jersey.spi.container.ContainerRequest;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Named
@Provider
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2={"Lcom/addonengine/addons/analytics/rest/filter/ValidAddonLicenseFilter;", "Lcom/addonengine/addons/analytics/rest/filter/PrerequestFilter;", "licenseService", "Lcom/addonengine/addons/analytics/service/confluence/LicenseService;", "(Lcom/addonengine/addons/analytics/service/confluence/LicenseService;)V", "filter", "Lcom/sun/jersey/spi/container/ContainerRequest;", "containerRequest", "analytics"})
public final class ValidAddonLicenseFilter
extends PrerequestFilter {
    @NotNull
    private final LicenseService licenseService;

    @Inject
    public ValidAddonLicenseFilter(@NotNull LicenseService licenseService) {
        Intrinsics.checkNotNullParameter((Object)licenseService, (String)"licenseService");
        this.licenseService = licenseService;
    }

    @Override
    @NotNull
    public ContainerRequest filter(@NotNull ContainerRequest containerRequest) {
        Intrinsics.checkNotNullParameter((Object)containerRequest, (String)"containerRequest");
        if (this.licenseService.getStatus() == LicenseStatus.VALID) {
            return containerRequest;
        }
        Response.ResponseBuilder response = Response.status((Response.Status)Response.Status.FORBIDDEN).type("application/json").entity((Object)new ErrorResponseDto(new ErrorDto("InvalidAddonLicense", "Add-on license is not valid.")));
        throw new WebApplicationException(response.build());
    }
}

