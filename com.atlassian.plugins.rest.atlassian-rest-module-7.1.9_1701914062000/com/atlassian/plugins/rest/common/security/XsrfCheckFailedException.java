/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.security;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class XsrfCheckFailedException
extends WebApplicationException {
    public XsrfCheckFailedException() {
        this(Response.Status.FORBIDDEN);
    }

    public XsrfCheckFailedException(Response.Status status) {
        super(Response.status(status).entity("XSRF check failed").build());
    }
}

