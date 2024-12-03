/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.module.scope;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ScopeCheckFailedException
extends WebApplicationException {
    public ScopeCheckFailedException(Response.Status status) {
        super(Response.status(status).entity("Scope Check Failed").build());
    }
}

