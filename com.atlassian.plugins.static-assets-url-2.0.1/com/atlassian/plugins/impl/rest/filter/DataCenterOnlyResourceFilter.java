/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.sun.jersey.spi.container.ContainerRequest
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  com.sun.jersey.spi.container.ContainerResponseFilter
 *  com.sun.jersey.spi.container.ResourceFilter
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.plugins.impl.rest.filter;

import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class DataCenterOnlyResourceFilter
implements ResourceFilter,
ContainerRequestFilter {
    private static final String ATLASSIAN_DEV_MODE = "atlassian.dev.mode";
    private final LicenseHandler licenseHandler;

    public DataCenterOnlyResourceFilter(LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    public ContainerRequest filter(ContainerRequest request) {
        if (this.isDevMode() || this.licenseHandler.getAllProductLicenses().stream().allMatch(BaseLicenseDetails::isDataCenter)) {
            return request;
        }
        throw new WebApplicationException(Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)"This feature is only available to data center customers").build());
    }

    private boolean isDevMode() {
        return Boolean.getBoolean(ATLASSIAN_DEV_MODE);
    }

    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    public ContainerResponseFilter getResponseFilter() {
        return null;
    }
}

