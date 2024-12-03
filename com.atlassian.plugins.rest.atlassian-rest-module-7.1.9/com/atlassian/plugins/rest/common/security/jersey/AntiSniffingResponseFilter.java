/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.rest.common.security.jersey;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
public class AntiSniffingResponseFilter
implements ContainerResponseFilter {
    @Context
    private HttpServletResponse response;
    public static final String ANTI_SNIFFING_HEADER_NAME = "X-Content-Type-Options";
    public static final String ANTI_SNIFFING_HEADER_VALUE = "nosniff";

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse containerResponse) {
        if (!this.response.containsHeader(ANTI_SNIFFING_HEADER_NAME) && !containerResponse.getHttpHeaders().containsKey(ANTI_SNIFFING_HEADER_NAME)) {
            containerResponse.getHttpHeaders().add(ANTI_SNIFFING_HEADER_NAME, ANTI_SNIFFING_HEADER_VALUE);
        }
        return containerResponse;
    }
}

