/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api;

import com.sun.jersey.api.Responses;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ConflictException
extends WebApplicationException {
    public ConflictException() {
        super(Responses.conflict().build());
    }

    public ConflictException(String message) {
        super(Response.status(409).entity(message).type("text/plain").build());
    }
}

