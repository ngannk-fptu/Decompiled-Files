/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.http.method.Methods
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.common.security.jersey;

import com.atlassian.http.method.Methods;
import com.atlassian.plugins.rest.common.security.XsrfCheckFailedException;
import com.atlassian.plugins.rest.common.security.jersey.XsrfResourceFilter;
import com.sun.jersey.spi.container.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OriginBasedXsrfResourceFilter
extends XsrfResourceFilter {
    private static final Logger log = LoggerFactory.getLogger(OriginBasedXsrfResourceFilter.class);

    OriginBasedXsrfResourceFilter() {
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        if (!Methods.isMutative((String)request.getMethod()) || !this.isLikelyToBeFromBrowser(request)) {
            return request;
        }
        if (this.passesAdditionalBrowserChecks(request)) {
            return request;
        }
        if (request.getMediaType() != null && this.isXsrfable(request.getMethod(), request.getMediaType())) {
            this.logXsrfFailureButNotBeingEnforced(request, log);
            return request;
        }
        throw new XsrfCheckFailedException();
    }
}

