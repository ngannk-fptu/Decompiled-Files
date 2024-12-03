/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.security;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class CorsPreflightCheckCompleteException
extends WebApplicationException {
    public CorsPreflightCheckCompleteException(Response response) {
        super(response);
    }
}

