/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.container.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class CsrfProtectionFilter
implements ContainerRequestFilter {
    private static final Set<String> METHODS_TO_IGNORE;
    private static final String HEADER_NAME = "X-Requested-By";

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        if (!METHODS_TO_IGNORE.contains(request.getMethod()) && !request.getRequestHeaders().containsKey(HEADER_NAME)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return request;
    }

    static {
        HashSet<String> mti = new HashSet<String>();
        mti.add("GET");
        mti.add("OPTIONS");
        mti.add("HEAD");
        METHODS_TO_IGNORE = Collections.unmodifiableSet(mti);
    }
}

