/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AuthorisationException
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  com.sun.jersey.spi.container.ContainerResponseFilter
 *  com.sun.jersey.spi.container.ResourceFilter
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.zdu.rest.filter;

import com.atlassian.plugins.rest.common.security.AuthorisationException;
import com.atlassian.zdu.LicenseService;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class DataCenterLicenseFilter
implements ResourceFilter {
    private final LicenseService licenseService;

    public DataCenterLicenseFilter(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    public ContainerRequestFilter getRequestFilter() {
        return containerRequest -> {
            if (!this.licenseService.isDataCenter()) {
                throw new AuthorisationException("Rolling upgrades feature is available only with Data Center license.");
            }
            return containerRequest;
        };
    }

    public ContainerResponseFilter getResponseFilter() {
        return null;
    }
}

