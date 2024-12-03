/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.module.filter;

import com.atlassian.plugins.rest.common.security.CorsHeaders;
import com.atlassian.plugins.rest.common.security.jersey.CorsResourceFilter;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsAcceptOptionsPreflightFilter
implements ContainerRequestFilter {
    @Override
    public ContainerRequest filter(ContainerRequest request) {
        if (request.getMethod().equals("OPTIONS")) {
            String origin = CorsResourceFilter.extractOrigin(request);
            String targetMethod = request.getHeaderValue(CorsHeaders.ACCESS_CONTROL_REQUEST_METHOD.value());
            if (targetMethod != null && origin != null) {
                request.setMethod(targetMethod);
                request.getProperties().put("Cors-Preflight-Requested", "true");
            }
        }
        return request;
    }
}

